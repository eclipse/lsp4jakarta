/******************************************************************************* 
 * Copyright (c) 2022, 2023 IBM Corporation and others.
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

import static org.eclipse.lsp4jakarta.jdt.internal.core.ls.ArgumentUtils.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ls.core.internal.IDelegateCommandHandler;
import org.eclipse.jdt.ls.core.internal.JavaLanguageServerPlugin;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionContext;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.jsonrpc.CompletableFutures;
import org.eclipse.lsp4jakarta.commons.JakartaJavaCompletionParams;
import org.eclipse.lsp4jakarta.commons.JakartaJavaCompletionResult;
import org.eclipse.lsp4jakarta.commons.JavaCursorContextResult;
import org.eclipse.lsp4jakarta.jdt.core.JDTUtils;
import org.eclipse.lsp4jakarta.jdt.core.PropertiesManagerForJava;

/**
 * Delegate Command Handler for LSP4Jakarta JDT LS extension commands
 */
public class JakartaDelegateCommandHandlerForJava implements IDelegateCommandHandler {

	private static final String JAVA_COMPLETION_COMMAND_ID = "jakarta/java/completion";

	public JakartaDelegateCommandHandlerForJava() {
	}

	@Override
	public Object executeCommand(String commandId, List<Object> arguments, IProgressMonitor monitor) throws Exception {
		JavaLanguageServerPlugin
				.logInfo(String.format("Executing command '%s' in LSP4Jakarta JDT LS extension", commandId));
		switch (commandId) {
			case JAVA_COMPLETION_COMMAND_ID:
				return getCompletionForJava(arguments, commandId, monitor);
			default:
				throw new UnsupportedOperationException(String.format("Unsupported command '%s'!", commandId));
		}
	}

	/**
	 * Return the completion result for the given arguments
	 *
	 * @param arguments
	 * @param commandId
	 * @param monitor
	 * @return the completion result for the given arguments
	 * @throws JavaModelException
	 * @throws CoreException
	 */
	private static JakartaJavaCompletionResult getCompletionForJava(List<Object> arguments, String commandId,
			IProgressMonitor monitor) throws JavaModelException, CoreException {
		JakartaJavaCompletionParams params = createJakartaJavaCompletionParams(arguments, commandId);
		CompletionList completionList = PropertiesManagerForJava.getInstance().completion(params,
				JDTUtilsLSImpl.getInstance(), monitor);
		JavaCursorContextResult cursorContext = PropertiesManagerForJava.getInstance().javaCursorContext(params,
				JDTUtilsLSImpl.getInstance(), monitor);
		return new JakartaJavaCompletionResult(completionList, cursorContext);
	}

	/**
	 * Create the completion parameters from the given argument map
	 *
	 * @param arguments
	 * @param commandId
	 * @return the completion parameters from the given argument map
	 */
	private static JakartaJavaCompletionParams createJakartaJavaCompletionParams(List<Object> arguments,
			String commandId) {
		Map<String, Object> obj = getFirst(arguments);
		if (obj == null) {
			throw new UnsupportedOperationException(String.format(
					"Command '%s' must be called with one JakartaJavaCompletionParams argument!", commandId));
		}
		String javaFileUri = getString(obj, "uri");
		if (javaFileUri == null) {
			throw new UnsupportedOperationException(String.format(
					"Command '%s' must be called with required JakartaJavaCompletionParams.uri (java URI)!",
					commandId));
		}
		Position position = getPosition(obj, "position");
		if (position == null) {
			throw new UnsupportedOperationException(String.format(
					"Command '%s' must be called with required JakartaJavaCompletionParams.position (completion trigger location)!",
					commandId));
		}
		JakartaJavaCompletionParams params = new JakartaJavaCompletionParams(javaFileUri, position);
		return params;
	}
}
