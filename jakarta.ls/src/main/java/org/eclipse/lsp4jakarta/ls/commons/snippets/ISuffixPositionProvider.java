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
package org.eclipse.lsp4jakarta.ls.commons.snippets;

import org.eclipse.lsp4j.Position;

/**
 * Suffix position provider API.
 * 
 * @author Angelo ZERR
 *
 */
public interface ISuffixPositionProvider {

	/**
	 * Returns the suffix position provider of the given <code>sufix</code> and null
	 * otherwise.
	 * 
	 * @param suffix 
	 * @return  the suffix position provider of the given <code>sufix</code> and null
	 * otherwise.
	 */
	Position findSuffixPosition(String suffix);
}
