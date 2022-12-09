/******************************************************************************* 
 * Copyright (c) 2022 IBM Corporation and others. 
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
package org.eclipse.lsp4jakarta.jdt.internal.core.ls;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ls.core.internal.IDelegateCommandHandler;
import org.eclipse.jdt.ls.core.internal.JavaLanguageServerPlugin;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionContext;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.jsonrpc.CompletableFutures;
import org.eclipse.lsp4jakarta.commons.JakartaJavaCodeActionParams;
import org.eclipse.lsp4jakarta.jdt.core.PropertiesManagerForJava;
import org.eclipse.lsp4jakarta.jdt.core.JDTUtils;

/**
 * Delegate Command Handler for LSP4Jakarta JDT LS extension commands
 */
public class JakartaDelegateCommandHandlerForJava implements IDelegateCommandHandler {

    private static final String JAVA_CODEACTION_COMMAND_ID = "jakarta/java/codeaction";
    private static final String JAVA_COMPLETION_COMMAND_ID = "jakarta/java/classpath";
    private static final String JAVA_DIAGNOSTICS_COMMAND_ID = "jakarta/java/diagnostics";

    public JakartaDelegateCommandHandlerForJava() {
    }

    @Override
    public Object executeCommand(String commandId, List<Object> arguments, IProgressMonitor monitor) throws Exception {
        JavaLanguageServerPlugin
                .logInfo(String.format("Executing command '%s' in LSP4Jakarta JDT LS extension", commandId));
        switch (commandId) {
            case JAVA_CODEACTION_COMMAND_ID:
                return getCodeActionForJava(arguments, commandId, monitor).get();
            case JAVA_COMPLETION_COMMAND_ID:
                return getContextBasedFilter(arguments, commandId, monitor).get();
            case JAVA_DIAGNOSTICS_COMMAND_ID:
                return getDiagnosticsForJava(arguments, commandId, monitor).get();
            default:
                throw new UnsupportedOperationException(String.format("Unsupported command '%s'!", commandId));
        }
    }

    /**
     * Returns the completion items for the given arguments
     * 
     * @param arguments JakartaClasspathParams @see
     *                  org.eclipse.lsp4jakarta.commons.JakartaClasspathParams
     * @param monitor
     * @return list of completion items as CompletableFuture<Object>
     */
    public CompletableFuture<Object> getContextBasedFilter(List<Object> arguments, String commandId,
            IProgressMonitor monitor) {
        Map<String, Object> obj = ArgumentUtils.getFirst(arguments);
        if (obj == null) {
            throw new UnsupportedOperationException(
                    String.format("Command '%s' must be called with one JakartaClasspathParams argument", commandId));
        }
        String uri = ArgumentUtils.getString(obj, "uri");
        List<String> snippetCtx = ArgumentUtils.getStringList(obj, "snippetCtx");
        return CompletableFutures.computeAsync((cancelChecker) -> {
            return PropertiesManagerForJava.getInstance().getExistingContextsFromClassPath(uri, snippetCtx);
        });
    }

    /**
     * Returns the publish diagnostics list for a given java file URIs.
     *
     * @param arguments JakartaDiagnosticsParams @see
     *                  org.eclipse.lsp4jakarta.commons.JakartaDiagnosticsParams
     * @param monitor
     * @return list of diagnostics as
     *         CompletableFuture<List<PublishDiagnosticsParams>>
     */
    private CompletableFuture<List<PublishDiagnosticsParams>> getDiagnosticsForJava(List<Object> arguments,
            String commandId, IProgressMonitor monitor) {
        Map<String, Object> obj = ArgumentUtils.getFirst(arguments);
        if (obj == null) {
            throw new UnsupportedOperationException(
                    String.format("Command '%s' must be called with one JakartaDiagnosticsParams argument", commandId));
        }
        List<String> uri = ArgumentUtils.getStringList(obj, "uris");
        return CompletableFutures.computeAsync((cancelChecker) -> {
            List<PublishDiagnosticsParams> publishDiagnostics = new ArrayList<PublishDiagnosticsParams>();
            publishDiagnostics = PropertiesManagerForJava.getInstance().getJavaDiagnostics(uri, monitor);
            return publishDiagnostics;
        });
    }

    /**
     * Returns the code actions list for the given arguments
     * 
     * @param arguments JakartaJavaCodeActionParams @see
     *                  org.eclipse.lsp4jakarta.commons.JakartaDiagnosticsParams
     * @param commandId
     * @param monitor
     * @return list of code actions as CompletableFuture<List<CodeAction>>
     */
    private CompletableFuture<List<CodeAction>> getCodeActionForJava(List<Object> arguments, String commandId,
            IProgressMonitor monitor) {
        Map<String, Object> obj = ArgumentUtils.getFirst(arguments);
        if (obj == null) {
            throw new UnsupportedOperationException(String
                    .format("Command '%s' must be called with one JakartaJavaCodeActionParams argument", commandId));
        }
        // reconstruct JakartaJavaCodeActionParams
        TextDocumentIdentifier textDocumentIdentifier = ArgumentUtils.getTextDocumentIdentifier(obj, "textDocument");
        if (textDocumentIdentifier == null) {
            throw new UnsupportedOperationException(String.format(
                    "Command '%s' must be called with required JakartaJavaCodeActionParams.texdDocumentIdentifier",
                    commandId));
        }
        Range range = ArgumentUtils.getRange(obj, "range");
        CodeActionContext context = ArgumentUtils.getCodeActionContext(obj, "context");
        boolean resourceOperationSupported = ArgumentUtils.getBoolean(obj, "resourceOperationSupported");
        JakartaJavaCodeActionParams params = new JakartaJavaCodeActionParams();
        params.setTextDocument(textDocumentIdentifier);
        params.setRange(range);
        params.setContext(context);
        params.setResourceOperationSupported(resourceOperationSupported);
        JDTUtils utils = new JDTUtils();
        return CompletableFutures.computeAsync((cancelChecker) -> {
            List<CodeAction> codeActions = new ArrayList<CodeAction>();
            try {
                codeActions = PropertiesManagerForJava.getInstance().getCodeAction(params, utils, monitor);
            } catch (JavaModelException e) {
                // TODO Auto-generated catch block
                JavaLanguageServerPlugin
                        .logException(String.format("Command '%s' unable to gather code actions", commandId), e);
            }
            return codeActions;
        });
    }

}
