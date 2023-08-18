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

package org.eclipse.lsp4jakarta.ls;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import org.eclipse.lsp4jakarta.commons.JakartaJavaCompletionParams;
import org.eclipse.lsp4jakarta.commons.JakartaJavaCompletionResult;
import org.eclipse.lsp4jakarta.commons.JavaCursorContextResult;
import org.eclipse.lsp4jakarta.ls.java.JakartaTextDocuments;
import org.eclipse.lsp4jakarta.ls.java.JakartaTextDocuments.JakartaTextDocument;
import org.eclipse.lsp4jakarta.ls.java.JavaTextDocumentSnippetRegistry;
import org.eclipse.lsp4jakarta.snippets.JavaSnippetCompletionContext;
import org.eclipse.lsp4jakarta.snippets.SnippetContextForJava;
import org.eclipse.lsp4jakarta.commons.DocumentFormat;
import org.eclipse.lsp4jakarta.ls.commons.BadLocationException;
import org.eclipse.lsp4jakarta.ls.commons.TextDocument;
import org.eclipse.lsp4jakarta.ls.commons.TextDocuments;

public class JakartaTextDocumentService implements TextDocumentService {

    private static final Logger LOGGER = Logger.getLogger(JakartaTextDocumentService.class.getName());

    private final JakartaLanguageServer jakartaLanguageServer;

    // Text document manager that maintains the contexts of the text documents
    // AJM made this change to allow LS to complete completion
    private final JakartaTextDocuments documents;
    
    public JakartaTextDocumentService(JakartaLanguageServer jls, JakartaTextDocuments jakartaTextDocuments) {
        this.jakartaLanguageServer = jls;
        //this.documents = new JakartaTextDocuments(jls, jls);
        this.documents = jakartaTextDocuments;
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
    public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(CompletionParams params) {
    	
    	JakartaTextDocument document = documents.get(params.getTextDocument().getUri());
    	
    	return document.executeIfInJakartaProject((projectInfo, cancelChecker) -> {
			JakartaJavaCompletionParams javaParams = new JakartaJavaCompletionParams(
					params.getTextDocument().getUri(), params.getPosition());
    	

		// get the completion capabilities from the java language server component
		CompletableFuture<JakartaJavaCompletionResult> javaParticipantCompletionsFuture = jakartaLanguageServer
				.getLanguageClient().getJavaCompletion(javaParams);
		
		// calculate params for Java snippets
		Integer completionOffset = null;
		try {
			completionOffset = document.offsetAt(params.getPosition());
		} catch (BadLocationException e) {
			//LOGGER.log(Level.SEVERE, "Error while getting java snippet completions", e);
			return null;
		} 
		
		final Integer finalizedCompletionOffset = completionOffset;
		boolean canSupportMarkdown = true;
		boolean snippetsSupported = true;
    	
		cancelChecker.checkCanceled();

		return javaParticipantCompletionsFuture.thenApply((completionResult) -> {
			cancelChecker.checkCanceled();

			// We currently do not get any completion items from the JDT Extn layer - the completion
			// list will be null, so we will new it up here to add the LS based snippets. 
			// Will we in the future?
			CompletionList list = completionResult.getCompletionList();
			if (list == null) {
				list = new CompletionList();
			}

			// We do get a cursorContext obj back from the JDT Extn layer  - we will need that for snippet selection
			JavaCursorContextResult cursorContext = completionResult.getCursorContext();

			// calculate the snippet completion items based on the cursor context
			List<CompletionItem> snippetCompletionItems = documents.getSnippetRegistry().getCompletionItems(document, finalizedCompletionOffset,
					canSupportMarkdown, snippetsSupported, (context, model) -> {
						if (context != null && context instanceof SnippetContextForJava) {
							return ((SnippetContextForJava) context)
									.isMatch(new JavaSnippetCompletionContext(projectInfo, cursorContext));
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
    	/*
        String uri = position.getTextDocument().getUri();
        TextDocument document = documents.get(uri);
        // Async thread to query the JDT LS ext for snippet contexts on project's classpath
        CompletableFuture<List<String>> getSnippetContexts = CompletableFuture.supplyAsync(() -> {
            // Get the list of snippet contexts to pass to the JDT LS ext
            List<String> snippetReg = snippetRegistry.getSnippets().stream().map(snippet -> {
                return ((SnippetContextForJava) snippet.getContext()).getTypes().get(0);
            }).collect(Collectors.toList());
            JakartaClasspathParams filterParams = new JakartaClasspathParams(uri, snippetReg);
            try {
                // Pass JakartaClasspathParams to IDE client, to be forwarded to the JDT LS ext
                // Returns a CompletableFuture List<String> of snippet context that are on the
                // project's classpath
                return jakartaLanguageServer.getLanguageClient()
                        .getContextBasedFilter(filterParams).get();
            } catch (Exception e) {
                LOGGER.severe("Return LSP4Jakarta getContextBasedFilter() from client did not succeed: " + e.getMessage());
                return new ArrayList<String>();
            }
        });

        // Async thread to query the JDT LS ext for cursor contexts in Java
        JakartaJavaCompletionParams javaParams = new JakartaJavaCompletionParams(position.getTextDocument().getUri(), position.getPosition());
        CompletableFuture<JavaCursorContextResult> getCursorContext = CompletableFuture.supplyAsync(() -> {
            try {
                // Pass JakartaJavaCompletionParams to IDE client, to be forwarded to the JDT LS ext
                // Returns a CompletableFuture JavaCursorContextResult of cursor context in the Java file
                JavaCursorContextResult res = jakartaLanguageServer.getLanguageClient()
                        .getJavaCursorContext(javaParams).get();
                return res;
            } catch (Exception e) {
                LOGGER.severe("Return LSP4Jakarta getJavaCursorContext() from client did not succeed: " + e.getMessage());
                return new JavaCursorContextResult(JavaCursorContextKind.BEFORE_CLASS, ""); // error recovery
            }
        });
        
        try {
            int offset = document.offsetAt(position.getPosition());
            StringBuffer prefix = new StringBuffer();
            Range replaceRange = getReplaceRange(document, offset, prefix);
            if (replaceRange != null) {
                // Put list of CompletionItems in an Either and wrap as a CompletableFuture
            	return getCursorContext.thenCombineAsync(getSnippetContexts, (cursorContext, list) -> {
                    // Given the snippet contexts that are on the project's classpath, return the
                    // corresponding list of CompletionItems
                    if (cursorContext == null) {
                        LOGGER.severe("No Java cursor context provided, using default values to compute snippets.");
                        cursorContext = new JavaCursorContextResult(JavaCursorContextKind.BEFORE_CLASS, ""); // error recovery
                    }
                    return Either.forLeft(
                            snippetRegistry.getCompletionItem(replaceRange, "\n", true, list, cursorContext, prefix.toString()));
            	});
            }
        } catch (BadLocationException e) {
            LOGGER.severe("Failed to get completions: " + e.getMessage());
        }
        return CompletableFuture.completedFuture(Either.forLeft(new ArrayList<CompletionItem>()));
        */
    }


    @Override
    public CompletableFuture<Hover> hover(HoverParams params) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void didOpen(DidOpenTextDocumentParams params) {
        TextDocument document = documents.onDidOpenTextDocument(params);
        String uri = document.getUri();
        //triggerValidationFor(Arrays.asList(uri));
    }

    @Override
    public void didChange(DidChangeTextDocumentParams params) {
        TextDocument document = documents.onDidChangeTextDocument(params);
        if (document != null) {
            String uri = document.getUri();
            //triggerValidationFor(Arrays.asList(uri));
        }
    }

	@Override
	public void didSave(DidSaveTextDocumentParams params) {
	// validate all opened java files
        //triggerValidationForAll();
	}

}
