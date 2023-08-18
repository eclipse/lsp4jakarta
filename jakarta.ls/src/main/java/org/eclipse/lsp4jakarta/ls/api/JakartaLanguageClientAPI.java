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

package org.eclipse.lsp4jakarta.ls.api;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4jakarta.commons.JakartaJavaCompletionParams;
import org.eclipse.lsp4jakarta.commons.JakartaJavaCompletionResult;
import org.eclipse.lsp4jakarta.commons.JavaCursorContextResult;

/**
 * API of the client consuming the Language Server for Jakarta EE. Used to send
 * messages back to the client to ask for information about the Java project.
 */
public interface JakartaLanguageClientAPI extends LanguageClient, JakartaJavaCompletionProvider, JakartaJavaProjectLabelsProvider, JakartaJavaFileInfoProvider {

    @JsonRequest("jakarta/java/cursorcontext")
    default CompletableFuture<JavaCursorContextResult> getJavaCursorContext(JakartaJavaCompletionParams context) {
        return CompletableFuture.completedFuture(null);
    }
}
