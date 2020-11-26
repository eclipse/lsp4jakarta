/*******************************************************************************
* Copyright (c) 2020 Red Hat Inc. and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
* which is available at https://www.apache.org/licenses/LICENSE-2.0.
*
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
*
* Contributors:
*     Red Hat Inc. - initial API and implementation
*******************************************************************************/

package org.jakarta.codeAction;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;

/**
 * Java codeAction participants API.
 * reused from https://github.com/eclipse/lsp4mp/blob/b88710cc54170844717f655b9bff8bb4c4649a8d/microprofile.jdt/org.eclipse.lsp4mp.jdt.core/src/main/java/org/eclipse/lsp4mp/jdt/core/java/codeaction/IJavaCodeActionParticipant.java
 *
 * @author credit to Angelo ZERR
 *
 */
public interface IJavaCodeActionParticipant {
	/**
	 * Returns true if the code actions are adaptable for the given context and false
	 * otherwise.
	 *
	 * <p>
	 * Participants can override this to check if some classes are on the classpath
	 * before deciding to process the code actions.
	 * </p>
	 *
	 * @param context java code action context
	 * @param monitor the progress monitor
	 * @return true if adaptable and false
	 *         otherwise.
	 *
	 */
	default boolean isAdaptedForCodeAction(JavaCodeActionContext context, IProgressMonitor monitor)
			throws CoreException {
		return true;
	}

	/**
	 * Return the code action list for a given compilation unit and null otherwise.
	 *
	 * @param context    the java code action context.
	 * @param diagnostic the diagnostic which must be fixed and null otherwise.
	 * @param monitor    the progress monitor
	 * @return the code action list for a given compilation unit and null otherwise.
	 * @throws CoreException
	 */
	List<? extends CodeAction> getCodeActions(JavaCodeActionContext context, Diagnostic diagnostic,
			IProgressMonitor monitor) throws CoreException;
}
