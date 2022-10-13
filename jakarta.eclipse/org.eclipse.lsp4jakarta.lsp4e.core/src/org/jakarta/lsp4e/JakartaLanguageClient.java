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

package org.jakarta.lsp4e;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.lsp4e.LanguageClientImpl;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionParams;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.jsonrpc.CancelChecker;
import org.eclipse.lsp4j.jsonrpc.CompletableFutures;
import org.eclipse.lsp4jakarta.api.JakartaLanguageClientAPI;
import org.eclipse.lsp4jakarta.commons.JakartaClasspathParams;
import org.eclipse.lsp4jakarta.commons.JakartaDiagnosticsParams;
import org.eclipse.lsp4jakarta.commons.JakartaJavaCodeActionParams;
import org.eclipse.lsp4jakarta.jdt.core.JDTServicesManager;
import org.eclipse.lsp4jakarta.jdt.core.JDTUtils;

public class JakartaLanguageClient extends LanguageClientImpl implements JakartaLanguageClientAPI {

    public JakartaLanguageClient() {
        // do nothing
    }

    private IProgressMonitor getProgressMonitor(CancelChecker cancelChecker) {
        IProgressMonitor monitor = new NullProgressMonitor() {
            public boolean isCanceled() {
                cancelChecker.checkCanceled();
                return false;
            };
        };
        return monitor;
    }

    @Override
    public CompletableFuture<List<PublishDiagnosticsParams>> getJavaDiagnostics(
            JakartaDiagnosticsParams jakartaParams) {
        return CompletableFutures.computeAsync((cancelChecker) -> {
            IProgressMonitor monitor = getProgressMonitor(cancelChecker);

            List<PublishDiagnosticsParams> publishDiagnostics = new ArrayList<PublishDiagnosticsParams>();
            publishDiagnostics = JDTServicesManager.getInstance().getJavaDiagnostics(jakartaParams.getUris(), monitor);
            return publishDiagnostics;
        });
    }

    @Override
    public CompletableFuture<List<String>> getContextBasedFilter(JakartaClasspathParams classpathParams) {
        return CompletableFutures.computeAsync((cancelChecker) -> {
            return JDTServicesManager.getInstance().getExistingContextsFromClassPath(classpathParams.getUri(),
                    classpathParams.getSnippetCtx());
        });
    }

    public CompletableFuture<List<CodeAction>> getCodeAction(JakartaJavaCodeActionParams params) {
        JDTUtils utils = new JDTUtils();

        return CompletableFutures.computeAsync((cancelChecker) -> {
            IProgressMonitor monitor = getProgressMonitor(cancelChecker);
            try {
                return (List<CodeAction>) JDTServicesManager.getInstance().getCodeAction(params, utils, monitor);
            } catch (JavaModelException e) {
                return null;
            }
        });
    }

}
