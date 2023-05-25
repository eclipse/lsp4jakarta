/*******************************************************************************
* Copyright (c) 2023 Red Hat Inc. and others.
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
*     IBM Corporation - convert to Jakarta
*******************************************************************************/
package org.eclipse.lsp4jakarta.commons;

import org.eclipse.lsp4j.CompletionList;

/**
 * Represents completion information and context calculated by the Java language server component.
 */
public class JakartaJavaCompletionResult {
	private CompletionList completionList;
	private JavaCursorContextResult cursorContext;

	public JakartaJavaCompletionResult(CompletionList completionList,
			JavaCursorContextResult cursorContext) {
		this.completionList = completionList;
		this.cursorContext = cursorContext;
	}

	public JakartaJavaCompletionResult() {
		this(null, null);
	}

	/**
	 * Returns the list of completion items contributed by the Java language server
	 * component.
	 *
	 * @return the list of completion items contributed by the Java language server
	 *         component
	 */
	public CompletionList getCompletionList() {
		return completionList;
	}

	/**
	 * Returns information on the context of the cursor in the Java file, calculated
	 * by the Java language server component.
	 *
	 * @return information on the context of the cursor in the Java file, calculated
	 *         by the Java language server component
	 */
	public JavaCursorContextResult getCursorContext() {
		return cursorContext;
	}

}
