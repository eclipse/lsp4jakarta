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
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.HoverParams;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.jsonrpc.CancelChecker;
import org.eclipse.lsp4j.jsonrpc.CompletableFutures;
import org.eclipse.lsp4jakarta.api.JakartaLanguageClientAPI;
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
    public CompletableFuture<Hover> getJavaHover(HoverParams params) {
        return CompletableFuture.completedFuture(null);
        // return dummy test hover object
//		return CompletableFutures.computeAsync((cancelChecker) -> {
//			IProgressMonitor monitor = getProgressMonitor(cancelChecker);
//			Hover testHover = new Hover();
//			List<Either<String, MarkedString>> contents = new ArrayList<>();
//			contents.add(Either.forLeft("this is test hover"));
//			testHover.setContents(contents);
//			return testHover;
//		});
    }

    @Override
    public CompletableFuture<List<PublishDiagnosticsParams>> getJavaDiagnostics(JakartaDiagnosticsParams javaParams) {
        return CompletableFutures.computeAsync((cancelChecker) -> {
            IProgressMonitor monitor = getProgressMonitor(cancelChecker);

            List<PublishDiagnosticsParams> publishDiagnostics = new ArrayList<PublishDiagnosticsParams>();
            publishDiagnostics = JDTServicesManager.getInstance().getJavaDiagnostics(javaParams, monitor);
            return publishDiagnostics;
        });
    }

    /**
     * @author ankushsharma
     * @brief creates a filter to let the language server know which contexts exist
     *        in the Java Project
     * @param uri            - String representing file from which to derive project
     *                       classpath
     * @param snippetContext - get all the context fields from the snippets and
     *                       check if they exist in this method
     * @return List<String>
     */
    @Override
    public CompletableFuture<List<String>> getContextBasedFilter(String uri, List<String> snippetContexts) {
        return CompletableFutures.computeAsync((cancelChecker) -> {
            return JDTServicesManager.getInstance().getExistingContextsFromClassPath(uri, snippetContexts);
        });
    }

    public CompletableFuture<List<CodeAction>> getCodeAction(CodeActionParams params) {
        JDTUtils utils = new JDTUtils();

        return CompletableFutures.computeAsync((cancelChecker) -> {
            IProgressMonitor monitor = getProgressMonitor(cancelChecker);
            try {
                JakartaJavaCodeActionParams JakartaParams = new JakartaJavaCodeActionParams(params.getTextDocument(),
                        params.getRange(), params.getContext());
                return (List<CodeAction>) JDTServicesManager.getInstance().getCodeAction(JakartaParams, utils, monitor);
            } catch (JavaModelException e) {
                return null;
            }
        });
    }

}
