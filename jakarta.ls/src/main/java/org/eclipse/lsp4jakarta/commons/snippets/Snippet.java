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

import java.util.List;
import java.util.function.Predicate;

/**
 * Reused from https://github.com/eclipse/lsp4mp/blob/master/microprofile.ls/org.eclipse.lsp4mp.ls/src/main/java/org/eclipse/lsp4mp/ls/commons/snippets/Snippet.java 
 * @author Ankush Sharma, credit to Angelo ZERR
 *
 */
public class Snippet {
    private List<String> prefixes;
    private List<String> body;
    private String description;
    private String scope;
    private ISnippetContext<?> context;

    public List<String> getPrefixes() {
        return prefixes;
    }

    public void setPrefixes(List<String> prefixes) {
        this.prefixes = prefixes;
    }

    public List<String> getBody() {
        return body;
    }

    public void setBody(List<String> body) {
        this.body = body;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public ISnippetContext<?> getContext() {
        return context;
    }

    public void setContext(ISnippetContext<?> context) {
        this.context = context;
    }

    public boolean hasContext() {
        return getContext() != null;
    }

    public boolean match(Predicate<ISnippetContext<?>> contextFilter) {
        if (!hasContext()) {
            return true;
        }
        return contextFilter.test(getContext());
    }
}
