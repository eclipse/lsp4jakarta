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
package org.eclipse.lsp4jakarta.jdt.core;

import org.eclipse.jdt.core.dom.ASTNode;

/**
 * Utility methods for working with {@link ASTNode}.
 */
public class ASTNodeUtils {

	private ASTNodeUtils() {
	}

	/**
	 * Returns true if the given <code>ASTNode</code> represents an annotation, and false otherwise.
	 *
	 * @param node the ast node to check, assumed to be non-null
	 * @return true if the given <code>ASTNode</code> represents an annotation, and false otherwise
	 */
	public static boolean isAnnotation(ASTNode node) {
		int nodeType = node.getNodeType();
		return nodeType == ASTNode.MARKER_ANNOTATION || nodeType == ASTNode.SINGLE_MEMBER_ANNOTATION
				|| nodeType == ASTNode.NORMAL_ANNOTATION;
	}

}
