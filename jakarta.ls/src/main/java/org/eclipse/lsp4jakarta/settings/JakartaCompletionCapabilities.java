/*******************************************************************************
* Copyright (c) 2019 Red Hat Inc. and others.
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
package org.eclipse.lsp4jakarta.settings;

import org.eclipse.lsp4j.CompletionCapabilities;

/**
 * A wrapper around LSP {@link CompletionCapabilities}.
 *
 */
public class JakartaCompletionCapabilities {

    private CompletionCapabilities completionCapabilities;

    public void setCapabilities(CompletionCapabilities completionCapabilities) {
        this.completionCapabilities = completionCapabilities;
    }

    public CompletionCapabilities getCompletionCapabilities() {
        return completionCapabilities;
    }

    /**
     * Returns <code>true</code> if the client support snippet and
     * <code>false</code> otherwise.
     *
     * @return <code>true</code> if the client support snippet and
     *         <code>false</code> otherwise.
     */
    public boolean isCompletionSnippetsSupported() {
        return completionCapabilities != null && completionCapabilities.getCompletionItem() != null
               && completionCapabilities.getCompletionItem().getSnippetSupport() != null
               && completionCapabilities.getCompletionItem().getSnippetSupport();
    }

    /**
     * Returns <code>true</code> if the client support the given documentation
     * format and <code>false</code> otherwise.
     *
     * @return <code>true</code> if the client support the given documentation
     *         format and <code>false</code> otherwise.
     */
    public boolean isDocumentationFormatSupported(String documentationFormat) {
        return completionCapabilities != null && completionCapabilities.getCompletionItem() != null
               && completionCapabilities.getCompletionItem().getDocumentationFormat() != null
               && completionCapabilities.getCompletionItem().getDocumentationFormat().contains(documentationFormat);
    }

    /**
     * Returns true if the client supports resolving the documentation property in
     * completionItem/resolve and false otherwise.
     *
     * @return true if the client supports resolving the documentation property in
     *         completionItem/resolve and false otherwise
     */
    public boolean isCompletionResolveDocumentationSupported() {
        return completionCapabilities != null && completionCapabilities.getCompletionItem() != null
               && completionCapabilities.getCompletionItem().getResolveSupport() != null
               && completionCapabilities.getCompletionItem().getResolveSupport().getProperties() != null
               && completionCapabilities.getCompletionItem().getResolveSupport().getProperties().contains("documentation");
    }

    /**
     * Returns true if the client supports editRange in itemDefaults support and
     * false otherwise.
     *
     * @param param the completion itemDefaults parameter to be checked
     * @return true if the client supports editRange in itemDefaults support and
     *         false otherwise.
     */
    public boolean isCompletionListItemDefaultsSupport(String param) {
        return completionCapabilities != null && completionCapabilities.getCompletionList() != null
               && completionCapabilities.getCompletionList().getItemDefaults() != null
               && completionCapabilities.getCompletionList().getItemDefaults().contains(param);
    }

}
