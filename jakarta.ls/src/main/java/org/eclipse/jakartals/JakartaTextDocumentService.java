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

package org.eclipse.jakartals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.eclipse.jakartals.commons.JakartaDiagnosticsParams;
import org.eclipse.jakartals.commons.SnippetContextForJava;
import org.eclipse.jakartals.commons.SnippetRegistry;
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
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.TextDocumentService;
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
        /*
         * Code completion functionality for eclipse Jakarta EE. This method is
         * automatically called by the Language Server Client provided it has provided a
         * java-completion-computer extension on the client side.
         */
        String uri = position.getTextDocument().getUri();
        // Method that gets all the snippet contexts and send to JDT to find which exist
        // in classpath
        CompletableFuture<List<String>> getSnippetContexts = CompletableFuture.supplyAsync(() -> {
            return snippetRegistry.getSnippets().stream().map(snippet -> {
                return ((SnippetContextForJava) snippet.getContext()).getTypes().get(0);
            }).collect(Collectors.toList());
        }).thenCompose(snippetctx -> {
            return jakartaLanguageServer.getLanguageClient().getContextBasedFilter(uri, snippetctx);
        }).thenApply(classpath -> {
            return classpath;
        });

        // An array of snippet contexts is provided to the snippet registry to determine
        // which snippets to show
        return getSnippetContexts.thenApply(ctx -> {
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
        return jakartaLanguageServer.getLanguageClient().getCodeAction(params) //
                .thenApply(codeActions -> {
                    return codeActions.stream() //
                            .map(ca -> {
                                Either<Command, CodeAction> e = Either.forRight(ca);
                                return e;
                            }).collect(Collectors.toList());
                });

    }

    private void triggerValidationFor(List<String> uris) {
        if (uris.isEmpty()) {
            return;
        }
        JakartaDiagnosticsParams javaParams = new JakartaDiagnosticsParams(uris);
        // TODO: Use settings to see if markdown is supported
        // boolean markdownSupported =
        // sharedSettings.getHoverSettings().isContentFormatSupported(MarkupKind.MARKDOWN);
        // if (markdownSupported) {
        // javaParams.setDocumentFormat(DocumentFormat.Markdown);
        // }
        javaParams.setDocumentFormat(DocumentFormat.Markdown);
        jakartaLanguageServer.getLanguageClient().getJavaDiagnostics(javaParams) //
                .thenApply(diagnostics -> {
                    if (diagnostics == null) {
                        return null;
                    }
                    for (PublishDiagnosticsParams diagnostic : diagnostics) {
                        jakartaLanguageServer.getLanguageClient().publishDiagnostics(diagnostic);
                    }
                    return null;
                });
    }
}
