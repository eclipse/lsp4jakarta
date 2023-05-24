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
*******************************************************************************/
package org.eclipse.lsp4jakarta.snippets;

import org.eclipse.lsp4jakarta.commons.JavaCursorContextResult;
import org.eclipse.lsp4jakarta.commons.ProjectLabelInfoEntry;

/**
 * Represents the context from the Java file needed in order to determine what
 * snippets should be suggested.
 */
public class JavaSnippetCompletionContext {

	private ProjectLabelInfoEntry projectLabelInfoEntry;
	private JavaCursorContextResult javaCursorContextResult;

	public JavaSnippetCompletionContext(ProjectLabelInfoEntry projectLabelInfoEntry,
			JavaCursorContextResult javaCursorContextResult) {
		this.projectLabelInfoEntry = projectLabelInfoEntry;
		this.javaCursorContextResult = javaCursorContextResult;
	}

	/**
	 * Returns the project label info entry for the file in which completion was
	 * triggered.
	 *
	 * @return the project label info entry for the file in which completion was
	 *         triggered
	 */
	public ProjectLabelInfoEntry getProjectLabelInfoEntry() {
		return projectLabelInfoEntry;
	}

	/**
	 * Returns the context related to the cursor location in the given document.
	 *
	 * @return the context related to the cursor location in the given document
	 */
	public JavaCursorContextResult getJavaCursorContextResult() {
		return javaCursorContextResult;
	}

}
