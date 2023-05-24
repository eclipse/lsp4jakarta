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

package org.eclipse.lsp4jakarta.commons.snippets;

/**
 * Loader used to load snippets in a given registry for a language id
 * Modified from https://github.com/eclipse/lsp4mp/blob/master/microprofile.ls/org.eclipse.lsp4mp.ls/src/main/java/org/eclipse/lsp4mp/ls/commons/snippets/ISnippetRegistryLoader.java
 * 
 * @author Ankush Sharma, credit to Angelo ZERR
 */
public interface ISnippetRegistryLoader {
    /**
     * Register a given snippet in the register
     * 
     * @param registry <SnippetRegistry>
     * @throws Exception
     */
    void load(SnippetRegistry registry) throws Exception;
    
	/**
	 * Returns the language id and null otherwise.
	 *
	 * @return the language id and null otherwise.
	 */
	default String getLanguageId() {
		return null;
	}
}
