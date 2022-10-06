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

package org.eclipse.lsp4jakarta.api;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionParams;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4jakarta.commons.JakartaDiagnosticsParams;

/**
 * API of the client consuming the Jakarta EE Language Server Used to send
 * messages back to the client to ask for information about the Java project
 * Client then delegates that request to the IDEs built in java language
 * support.
 */
public interface JakartaLanguageClientAPI extends LanguageClient {

    @JsonRequest("jakarta/java/diagnostics")
    default CompletableFuture<List<PublishDiagnosticsParams>> getJavaDiagnostics(JakartaDiagnosticsParams javaParams) {
        return CompletableFuture.completedFuture(null);
    }

    /**
     * @author Ankush Sharma
     * @param uri
     * @param snippetContext
     * @return A List of Strings, each representing an item in the project classpath
     *         This method compares JavaProject classpath to the snippets contexts,
     *         returning a String for the snippets contexts that exist in the
     *         classpath and null for those that do not
     */
    @JsonRequest("jakarta/java/classpath")
    default CompletableFuture<List<String>> getContextBasedFilter(String uri, List<String> snippetContexts) {
        return CompletableFuture.completedFuture(null);
    }

    @JsonRequest("jakarta/java/codeaction")
    default CompletableFuture<List<CodeAction>> getCodeAction(CodeActionParams params) {
        return CompletableFuture.completedFuture(null);
    }
}
