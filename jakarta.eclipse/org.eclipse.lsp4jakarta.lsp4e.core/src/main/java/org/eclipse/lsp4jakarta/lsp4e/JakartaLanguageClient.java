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

package org.eclipse.lsp4jakarta.lsp4e;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.lsp4e.LanguageClientImpl;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.jsonrpc.CancelChecker;
import org.eclipse.lsp4j.jsonrpc.CompletableFutures;
import org.eclipse.lsp4jakarta.commons.JakartaJavaCodeActionParams;
import org.eclipse.lsp4jakarta.commons.JakartaJavaCompletionParams;
import org.eclipse.lsp4jakarta.commons.JakartaJavaCompletionResult;
import org.eclipse.lsp4jakarta.commons.JakartaJavaDiagnosticsParams;
import org.eclipse.lsp4jakarta.commons.JakartaJavaFileInfo;
import org.eclipse.lsp4jakarta.commons.JakartaJavaFileInfoParams;
import org.eclipse.lsp4jakarta.commons.JakartaJavaProjectLabelsParams;
import org.eclipse.lsp4jakarta.commons.JavaCursorContextResult;
import org.eclipse.lsp4jakarta.commons.ProjectLabelInfoEntry;
import org.eclipse.lsp4jakarta.commons.codeaction.CodeActionResolveData;
import org.eclipse.lsp4jakarta.commons.utils.JSONUtility;
import org.eclipse.lsp4jakarta.jdt.core.ProjectLabelManager;
import org.eclipse.lsp4jakarta.jdt.core.PropertiesManagerForJava;
import org.eclipse.lsp4jakarta.jdt.internal.core.ls.JDTUtilsLSImpl;
import org.eclipse.lsp4jakarta.ls.api.JakartaLanguageClientAPI;

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

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<JakartaJavaCompletionResult> getJavaCompletion(JakartaJavaCompletionParams javaParams) {
        return CompletableFutures.computeAsync(cancelChecker -> {
            IProgressMonitor monitor = getProgressMonitor(cancelChecker);
            CompletionList completionList;
            try {
                completionList = PropertiesManagerForJava.getInstance().completion(javaParams,
                                                                                   JDTUtilsLSImpl.getInstance(), monitor);
                JavaCursorContextResult javaCursorContext = PropertiesManagerForJava.getInstance().javaCursorContext(javaParams, JDTUtilsLSImpl.getInstance(), monitor);
                return new JakartaJavaCompletionResult(completionList, javaCursorContext);
            } catch (JavaModelException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<List<ProjectLabelInfoEntry>> getAllJavaProjectLabels() {
        return CompletableFutures.computeAsync((cancelChecker) -> {
            IProgressMonitor monitor = getProgressMonitor(cancelChecker);
            return ProjectLabelManager.getInstance().getProjectLabelInfo();
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<ProjectLabelInfoEntry> getJavaProjectLabels(JakartaJavaProjectLabelsParams javaParams) {
        return CompletableFutures.computeAsync((cancelChecker) -> {
            IProgressMonitor monitor = getProgressMonitor(cancelChecker);
            return ProjectLabelManager.getInstance().getProjectLabelInfo(javaParams, JDTUtilsLSImpl.getInstance(),
                                                                         monitor);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<JakartaJavaFileInfo> getJavaFileInfo(JakartaJavaFileInfoParams javaParams) {
        return CompletableFutures.computeAsync(cancelChecker -> {
            IProgressMonitor monitor = getProgressMonitor(cancelChecker);
            return PropertiesManagerForJava.getInstance().fileInfo(javaParams, JDTUtilsLSImpl.getInstance());
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<List<PublishDiagnosticsParams>> getJavaDiagnostics(
                                                                                JakartaJavaDiagnosticsParams javaParams) {
        return CompletableFutures.computeAsync((cancelChecker) -> {
            IProgressMonitor monitor = getProgressMonitor(cancelChecker);
            try {
                return PropertiesManagerForJava.getInstance().diagnostics(javaParams, JDTUtilsLSImpl.getInstance(),
                                                                          monitor);
            } catch (JavaModelException e) {
                return Collections.emptyList();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<List<CodeAction>> getJavaCodeAction(JakartaJavaCodeActionParams javaParams) {
        return CompletableFutures.computeAsync((cancelChecker) -> {
            IProgressMonitor monitor = getProgressMonitor(cancelChecker);
            try {
                return (List<CodeAction>) PropertiesManagerForJava.getInstance().codeAction(javaParams,
                                                                                            JDTUtilsLSImpl.getInstance(), monitor);
            } catch (JavaModelException e) {
                return Collections.emptyList();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<CodeAction> resolveCodeAction(CodeAction unresolved) {
        return CompletableFutures.computeAsync((cancelChecker) -> {
            IProgressMonitor monitor = getProgressMonitor(cancelChecker);
            try {
                CodeActionResolveData resolveData = JSONUtility.toModel(unresolved.getData(), CodeActionResolveData.class);
                unresolved.setData(resolveData);
                return (CodeAction) PropertiesManagerForJava.getInstance().resolveCodeAction(unresolved,
                                                                                             JDTUtilsLSImpl.getInstance(), monitor);
            } catch (JavaModelException e) {
                return null;
            }
        });
    }
}
