/*******************************************************************************
* Copyright (c) 2020, 2023 IBM Corporation and others.
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

package org.eclipse.lsp4jakarta.ls;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
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
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4jakarta.commons.DocumentFormat;
import org.eclipse.lsp4jakarta.commons.JakartaJavaCodeActionParams;
import org.eclipse.lsp4jakarta.commons.JakartaJavaCompletionParams;
import org.eclipse.lsp4jakarta.commons.JakartaJavaCompletionResult;
import org.eclipse.lsp4jakarta.commons.JakartaJavaDiagnosticsParams;
import org.eclipse.lsp4jakarta.commons.JakartaJavaDiagnosticsSettings;
import org.eclipse.lsp4jakarta.commons.JavaCursorContextResult;
import org.eclipse.lsp4jakarta.ls.commons.BadLocationException;
import org.eclipse.lsp4jakarta.ls.commons.TextDocument;
import org.eclipse.lsp4jakarta.ls.commons.ValidatorDelayer;
import org.eclipse.lsp4jakarta.ls.java.JakartaTextDocuments;
import org.eclipse.lsp4jakarta.ls.java.JakartaTextDocuments.JakartaTextDocument;
import org.eclipse.lsp4jakarta.ls.java.JavaTextDocumentSnippetRegistry;
import org.eclipse.lsp4jakarta.snippets.JavaSnippetCompletionContext;
import org.eclipse.lsp4jakarta.snippets.SnippetContextForJava;

public class JakartaTextDocumentService implements TextDocumentService {

    private static final Logger LOGGER = Logger.getLogger(JakartaTextDocumentService.class.getName());

    private final JakartaLanguageServer jakartaLanguageServer;

    // Text document manager that maintains the contexts of the text documents
    private final JakartaTextDocuments documents;

    private ValidatorDelayer<JakartaTextDocument> validatorDelayer;

    public JakartaTextDocumentService(JakartaLanguageServer jls, JakartaTextDocuments jakartaTextDocuments) {
        this.jakartaLanguageServer = jls;
        this.documents = jakartaTextDocuments;
        this.validatorDelayer = new ValidatorDelayer<>((javaTextDocument) -> {
            triggerValidationFor(javaTextDocument);
        });
    }

    @Override
    public void didClose(DidCloseTextDocumentParams params) {
        documents.onDidCloseTextDocument(params);
        String uri = params.getTextDocument().getUri();
        // clear diagnostics
        jakartaLanguageServer.getLanguageClient().publishDiagnostics(new PublishDiagnosticsParams(uri, new ArrayList<Diagnostic>()));
    }

    @Override
    public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(CompletionParams params) {

        JakartaTextDocument document = documents.get(params.getTextDocument().getUri());

        return document.executeIfInJakartaProject((projectInfo, cancelChecker) -> {
            JakartaJavaCompletionParams javaParams = new JakartaJavaCompletionParams(params.getTextDocument().getUri(), params.getPosition());

            // get the completion capabilities from the java language server component
            CompletableFuture<JakartaJavaCompletionResult> javaParticipantCompletionsFuture = jakartaLanguageServer.getLanguageClient().getJavaCompletion(javaParams);

            // calculate params for Java snippets
            Integer completionOffset = null;
            try {
                completionOffset = document.offsetAt(params.getPosition());
            } catch (BadLocationException e) {
                // LOGGER.log(Level.SEVERE, "Error while getting java snippet completions", e);
                return null;
            }

            final Integer finalizedCompletionOffset = completionOffset;
            boolean canSupportMarkdown = true;
            boolean snippetsSupported = true;

            cancelChecker.checkCanceled();

            return javaParticipantCompletionsFuture.thenApply((completionResult) -> {
                cancelChecker.checkCanceled();

                // We currently do not get any completion items from the JDT Extn layer - the
                // completion
                // list will be null, so we will new it up here to add the LS based snippets.
                // Will we in the future?
                CompletionList list = completionResult.getCompletionList();
                if (list == null) {
                    list = new CompletionList();
                }

                // We do get a cursorContext obj back from the JDT Extn layer - we will need
                // that for snippet selection
                JavaCursorContextResult cursorContext = completionResult.getCursorContext();

                // calculate the snippet completion items based on the cursor context
                JavaTextDocumentSnippetRegistry snippetRegistry = documents.getSnippetRegistry();
                List<CompletionItem> snippetCompletionItems = snippetRegistry.getCompletionItems(
                                                                                                 document, finalizedCompletionOffset, canSupportMarkdown,
                                                                                                 snippetsSupported,
                                                                                                 (context, model) -> {
                                                                                                     if (context != null
                                                                                                         && context instanceof SnippetContextForJava) {
                                                                                                         return ((SnippetContextForJava) context).isMatch(new JavaSnippetCompletionContext(projectInfo, cursorContext));
                                                                                                     }
                                                                                                     return true;
                                                                                                 }, projectInfo);
                list.getItems().addAll(snippetCompletionItems);

                // This reduces the number of completion requests to the server. See:
                // https://microsoft.github.io/language-server-protocol/specifications/specification-current/#textDocument_completion
                list.setIsIncomplete(false);
                return Either.forRight(list);
            });

        }, Either.forLeft(Collections.emptyList()));
    }

    @Override
    public CompletableFuture<List<Either<Command, CodeAction>>> codeAction(CodeActionParams params) {
        // Prepare the JakartaJavaCodeActionParams
        JakartaJavaCodeActionParams jakartaCodeActionParams = new JakartaJavaCodeActionParams();
        jakartaCodeActionParams.setTextDocument(params.getTextDocument());
        jakartaCodeActionParams.setRange(params.getRange());
        jakartaCodeActionParams.setContext(params.getContext());

        // TODO: Retrieve client capabilities, and pass it along in the parameter object:
        // Investigate:
        // jakartaCodeActionParams.setResourceOperationSupported(),
        // jakartaCodeActionParams.setCommandConfigurationUpdateSupported(),
        // jakartaCodeActionParams.etResolveSupported().

        // Pass the JakartaJavaCodeActionParams to IDE client, to be forwarded to the
        // JDT LS extension.
        return jakartaLanguageServer.getLanguageClient().getJavaCodeAction(jakartaCodeActionParams) //
                        .thenApply(codeActions -> {
                            // Return the corresponding list of CodeActions, put in an Either and wrap as a
                            // CompletableFuture
                            return codeActions.stream().map(ca -> {
                                Either<Command, CodeAction> e = Either.forRight(ca);
                                return e;
                            }).collect(Collectors.toList());
                        });
    }

    @Override
    public CompletableFuture<CodeAction> resolveCodeAction(CodeAction unresolved) {
        return jakartaLanguageServer.getLanguageClient().resolveCodeAction(unresolved);
    }

    @Override
    public CompletableFuture<Hover> hover(HoverParams params) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void didOpen(DidOpenTextDocumentParams params) {
        validate(documents.onDidOpenTextDocument(params), false);
    }

    @Override
    public void didChange(DidChangeTextDocumentParams params) {
        validate(documents.onDidChangeTextDocument(params), true);
    }

    @Override
    public void didSave(DidSaveTextDocumentParams params) {
        // validate all opened java files which belong to a Jakarta project
        triggerValidationForAll(null);
    }

    private void validate(JakartaTextDocument javaTextDocument, boolean delay) {
        if (delay) {
            validatorDelayer.validateWithDelay(javaTextDocument);
        } else {
            triggerValidationFor(javaTextDocument);
        }
    }

    /**
     * Validate all opened Java files which belong to a Jakarta project.
     *
     * @param projectURIs list of project URIs filter and null otherwise.
     */
    private void triggerValidationForAll(Set<String> projectURIs) {
        triggerValidationFor(documents.all().stream() //
                        .filter(document -> projectURIs == null || projectURIs.contains(document.getProjectURI())) //
                        .map(TextDocument::getUri) //
                        .collect(Collectors.toList()));
    }

    /**
     * Validate the given opened Java file.
     *
     * @param document the opened Java file.
     */
    private void triggerValidationFor(JakartaTextDocument document) {
        document.executeIfInJakartaProject((projectinfo, cancelChecker) -> {
            String uri = document.getUri();
            triggerValidationFor(Arrays.asList(uri));
            return null;
        }, null, true);
    }

    /**
     * Validate all given Java files uris.
     *
     * @param uris Java files uris to validate.
     */
    private void triggerValidationFor(List<String> uris) {
        if (uris.isEmpty()) {
            return;
        }

        JakartaJavaDiagnosticsParams javaParams = new JakartaJavaDiagnosticsParams(uris, new JakartaJavaDiagnosticsSettings(null));

        // TODO: Use settings to see if markdown format is supported, or remove it if not needed.
        // Leave it hard coded for now.
        // sharedSettings.getHoverSettings().isContentFormatSupported(MarkupKind.MARKDOWN);
        javaParams.setDocumentFormat(DocumentFormat.Markdown);

        jakartaLanguageServer.getLanguageClient().getJavaDiagnostics(javaParams).thenApply(diagnostics -> {
            if (diagnostics == null) {
                return null;
            }
            for (PublishDiagnosticsParams diagnostic : diagnostics) {
                jakartaLanguageServer.getLanguageClient().publishDiagnostics(diagnostic);
            }
            return null;
        });
    }

    protected void cleanDiagnostics() {
        // clear existing diagnostics
        documents.all().forEach(doc -> {
            jakartaLanguageServer.getLanguageClient().publishDiagnostics(new PublishDiagnosticsParams(doc.getUri(), new ArrayList<Diagnostic>()));
        });
    }
}
