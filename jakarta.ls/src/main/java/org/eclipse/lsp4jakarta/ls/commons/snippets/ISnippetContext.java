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

/**
 * Snippet context used to filter the snippet.
 *
 * @author Angelo ZERR
 *
 * @param <T> the value type waited by the snippet context.
 */
public interface ISnippetContext<T> {

    /**
     * Return true if the given value match the snippet context and false otherwise.
     *
     * @param value the value to check.
     * @return true if the given value match the snippet context and false
     *         otherwise.
     */
    boolean isMatch(T value);
}
