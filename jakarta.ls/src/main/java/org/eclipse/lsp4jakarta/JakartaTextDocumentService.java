/*******************************************************************************
* Copyright (c) 2020, 2022 IBM Corporation and others.
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
import java.util.List;
import java.util.concurrent.CompletableFuture;
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
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4jakarta.commons.JakartaClasspathParams;
import org.eclipse.lsp4jakarta.commons.JakartaDiagnosticsParams;
import org.eclipse.lsp4jakarta.commons.JakartaJavaCodeActionParams;
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
        // clear diagnostics
        jakartaLanguageServer.getLanguageClient()
                .publishDiagnostics(new PublishDiagnosticsParams(uri, new ArrayList<Diagnostic>()));
    }

    @Override
    public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(CompletionParams position) {
        String uri = position.getTextDocument().getUri();
        // Async thread to query the JDT LS ext for snippet contexts on project's
        // classpath
        CompletableFuture<List<String>> getSnippetContexts = CompletableFuture.supplyAsync(() -> {
            // Get the list of snippet contexts to pass to the JDT LS ext
            List<String> snippetReg = snippetRegistry.getSnippets().stream().map(snippet -> {
                return ((SnippetContextForJava) snippet.getContext()).getTypes().get(0);
            }).collect(Collectors.toList());
            try {
                // Pass JakartaClasspathParams to IDE client, to be forwarded to the JDT LS ext
                // Returns a CompletableFuture List<String> of snippet context that are on the
                // project's classpath
                return jakartaLanguageServer.getLanguageClient()
                        .getContextBasedFilter(new JakartaClasspathParams(uri, snippetReg)).get();
            } catch (Exception e) {
                LOGGER.severe("Return LSP4Jakarta getContextBasedFilter() from client did not succeed: " + e.getMessage());
                return new ArrayList<String>();
            }
        });

        // Put list of CompletionItems in an Either and wrap as a CompletableFuture
        return getSnippetContexts.thenApply(ctx -> {
            // Given the snippet contexts that are on the project's classpath, return the
            // corresponding list of CompletionItems
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
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<List<Either<Command, CodeAction>>> codeAction(CodeActionParams params) {
        // Prepare the JakartaJavaCodeActionParams
        JakartaJavaCodeActionParams jakartaCodeActionParams = new JakartaJavaCodeActionParams(params);
        // Pass the JakartaJavaCodeActionParams to IDE client, to be forwarded to the
        // JDT LS ext
        // Async thread to get the list of code actions from the JDT LS ext
        return jakartaLanguageServer.getLanguageClient().getCodeAction(jakartaCodeActionParams) //
                .thenApply(codeActions -> {
                    // Return the corresponding list of CodeActions, put in an Either and wrap as a
                    // CompletableFuture
                    return codeActions.stream().map(ca -> {
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
        // Prepare the JakartaDiagnosticsParams
        JakartaDiagnosticsParams javaParams = new JakartaDiagnosticsParams(uris);
        // TODO: Use settings to see if markdown is supported
        // boolean markdownSupported =
        // sharedSettings.getHoverSettings().isContentFormatSupported(MarkupKind.MARKDOWN);
        // if (markdownSupported) {
        // javaParams.setDocumentFormat(DocumentFormat.Markdown);
        // }
        javaParams.setDocumentFormat(DocumentFormat.Markdown);

        // Async thread to query the JDT LS ext for Jakarta EE diagnostics
        CompletableFuture<List<PublishDiagnosticsParams>> getJakartaDiagnostics = CompletableFuture.supplyAsync(() -> {
            try {
                // Pass the JakartaDiagnosticsParams to IDE client, to be forwarded to the JDT
                // LS ext
                return jakartaLanguageServer.getLanguageClient().getJavaDiagnostics(javaParams).get();
            } catch (Exception e) {
                LOGGER.severe("Return LSP4Jakarta getJavaDiagnostics() from client did not succeed: " + e.getMessage());
                return new ArrayList<PublishDiagnosticsParams>();
            }
        }).thenApply(jakartaDiagnostics -> {
            // Publish the corresponding diagnostic items returned from the IDE client (from
            // the JDT LS ext)
            for (PublishDiagnosticsParams diagnostic : jakartaDiagnostics) {
                jakartaLanguageServer.getLanguageClient().publishDiagnostics(diagnostic);
            }
            return jakartaDiagnostics;
        });
    }
}
