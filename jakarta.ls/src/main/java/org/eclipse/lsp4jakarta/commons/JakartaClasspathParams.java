/*******************************************************************************
* Copyright (c) 2022 IBM Corporation and others.
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
package org.eclipse.lsp4jakarta.commons;

import java.util.List;

public class JakartaClasspathParams {

    private String uri;

    private List<String> snippetCtx;

    public JakartaClasspathParams() {

    }

    public JakartaClasspathParams(String uri, List<String> snippetCtx) {
        setUri(uri);
        setSnippetCtx(snippetCtx);
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return this.uri;
    }

    public void setSnippetCtx(List<String> snippetCtx) {
        this.snippetCtx = snippetCtx;
    }

    public List<String> getSnippetCtx() {
        return this.snippetCtx;
    }
}
