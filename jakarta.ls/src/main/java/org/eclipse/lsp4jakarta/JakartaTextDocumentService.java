/*******************************************************************************
* Copyright (c) 2020 IBM Corporation and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0.
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     IBM Corporation - initial API and implementation
*******************************************************************************/

package org.eclipse.lsp4jakarta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionParams;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.HoverParams;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.CancelChecker;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4jakarta.commons.JakartaClasspathParams;
import org.eclipse.lsp4jakarta.commons.JakartaDiagnosticsParams;
import org.eclipse.lsp4jakarta.commons.JakartaJavaCodeActionParams;
import org.eclipse.lsp4jakarta.commons.Snippet;
import org.eclipse.lsp4jakarta.commons.SnippetContextForJava;
import org.eclipse.lsp4jakarta.commons.SnippetRegistry;
import org.eclipse.lsp4mp.commons.DocumentFormat;
import org.eclipse.lsp4mp.ls.commons.TextDocument;
import org.eclipse.lsp4mp.ls.commons.TextDocuments;

public class JakartaTextDocumentService implements TextDocumentService {

    private static final Logger LOGGER = Logger.getLogger(JakartaTextDocumentService.class.getName());

    private final JakartaLanguageServer jakartaLanguageServer;

    private SnippetRegistry snippetRegistry = new SnippetRegistry();

    // Text document manager that maintains the contexts of the text documents
    private final TextDocuments<TextDocument> documents = new TextDocuments<TextDocument>();

    public JakartaTextDocumentService(JakartaLanguageServer jls) {
        this.jakartaLanguageServer = jls;
    }

    @Override
    public void didOpen(DidOpenTextDocumentParams params) {
        TextDocument document = documents.onDidOpenTextDocument(params);
        String uri = document.getUri();
        triggerValidationFor(Arrays.asList(uri));
    }

    @Override
    public void didChange(DidChangeTextDocumentParams params) {
        TextDocument document = documents.onDidChangeTextDocument(params);
        String uri = document.getUri();
        triggerValidationFor(Arrays.asList(uri));
    }

    @Override
    public void didClose(DidCloseTextDocumentParams params) {
        documents.onDidCloseTextDocument(params);
        String uri = params.getTextDocument().getUri();
        jakartaLanguageServer.getLanguageClient()
                .publishDiagnostics(new PublishDiagnosticsParams(uri, new ArrayList<Diagnostic>()));
    }
    
    @Override
    public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(CompletionParams position) {
        // textDocument/completion request
        LOGGER.info("Completion request");
        /*
         * Code completion functionality for eclipse Jakarta EE. This method is
         * automatically called by the Language Server Client provided it has provided a
         * java-completion-computer extension on the client side.
         */
        String uri = position.getTextDocument().getUri();
        LOGGER.info("uri: " + uri);
        
        /**
         * prep params
         * wrapped
         * goes to client
         * unwrapped
         * list<string>
         * put in Either and wrap
         * return as completablefuture
         */

        // write async thread that retrieves         
        CompletableFuture<List<String>> getSnippetContexts = CompletableFuture.supplyAsync(() -> {
            List<String> snippetReg = snippetRegistry.getSnippets().stream().map(snippet -> {
                return ((SnippetContextForJava) snippet.getContext()).getTypes().get(0);
            }).collect(Collectors.toList());
            try {
                // getLanguageClient() -> wraps client response in a CompletableFuture
                // get() unwraps the client response CompletableFuture into just a list of strings
                return jakartaLanguageServer.getLanguageClient().getContextBasedFilter(new JakartaClasspathParams(uri, snippetReg)).get();
            } catch (Exception e) {
                LOGGER.severe("Return request from client did not succeed: " + e.getMessage());
                return new ArrayList<String>();
            }
        });
        
        return getSnippetContexts.thenApply(ctx -> {
            LOGGER.info("ctx: " + ctx);
            // putting into Either and thenApply chains into a CompletableFuture
            return Either.forLeft(snippetRegistry
                    .getCompletionItem(new Range(position.getPosition(), position.getPosition()), "\n", true, ctx));
        });
    }

    @Override
    public void didSave(DidSaveTextDocumentParams params) {
        // validate all opened java files
        triggerValidationForAll();
    }

    /**
     * Validate all opened Java files which belong to a MicroProfile project.
     *
     * @param projectURIs list of project URIs filter and null otherwise.
     */
    private void triggerValidationForAll() {
        List<String> allDocs = documents.all().stream().map(doc -> doc.getUri()).collect(Collectors.toList());
        triggerValidationFor(allDocs);
    }

    @Override
    public CompletableFuture<Hover> hover(HoverParams params) {
        LOGGER.info("received textDocument/hover request");
        return jakartaLanguageServer.getLanguageClient().getJavaHover(params);
    }

    @Override
    public CompletableFuture<List<Either<Command, CodeAction>>> codeAction(CodeActionParams params) {
        /**
         * prep params
         * wrapped
         * goes to client
         * unwrapped
         * list<string>
         * put in Either and wrap
         * return as completablefuture
         */
        JakartaJavaCodeActionParams jakartaCodeActionParams = new JakartaJavaCodeActionParams(params);
        return jakartaLanguageServer.getLanguageClient().getCodeAction(jakartaCodeActionParams) //
                .thenApply(codeActions -> {
                    return codeActions.stream() //
                            .map(ca -> {
                                Either<Command, CodeAction> e = Either.forRight(ca);
                                return e;
                            }).collect(Collectors.toList());
                });

    }

    // diagnostic request
    private void triggerValidationFor(List<String> uris) {
        if (uris.isEmpty()) {
            return;
        }
        LOGGER.info("1: " + uris);
        JakartaDiagnosticsParams javaParams = new JakartaDiagnosticsParams(uris);
        // TODO: Use settings to see if markdown is supported
        // boolean markdownSupported =
        // sharedSettings.getHoverSettings().isContentFormatSupported(MarkupKind.MARKDOWN);
        // if (markdownSupported) {
        // javaParams.setDocumentFormat(DocumentFormat.Markdown);
        // }
        javaParams.setDocumentFormat(DocumentFormat.Markdown);
        LOGGER.info("2: " + uris);
        
        /**
         * prep params
         * wrapped
         * goes to client
         * unwrapped
         * list<PublishDiagnosticsParams>
         * publish list<PublishDiagnosticsParams>
         */
        CompletableFuture<List<PublishDiagnosticsParams>> getJakartaDiagnostics = CompletableFuture.supplyAsync(() -> {
            
            try {
                LOGGER.info("3????");
                // new thread
                return jakartaLanguageServer.getLanguageClient().getJavaDiagnostics(javaParams).get();
            } catch (Exception e) {
                LOGGER.severe("Return request from client did not succeed: " + e.getMessage());
                return new ArrayList<PublishDiagnosticsParams>();
            }
        }).thenApply(jakartaDiagnostics -> {
            LOGGER.info("5: " + jakartaDiagnostics);
            for (PublishDiagnosticsParams diagnostic : jakartaDiagnostics) {
                LOGGER.info("6: " + diagnostic);
                jakartaLanguageServer.getLanguageClient().publishDiagnostics(diagnostic);
            }
            return jakartaDiagnostics;
        });
        LOGGER.info("~4");
    }
}
