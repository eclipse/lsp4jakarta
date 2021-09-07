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

package org.eclipse.lsp4jakarta.commons;

import org.eclipse.lsp4j.CodeActionContext;
import org.eclipse.lsp4j.CodeActionParams;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextDocumentIdentifier;

/**
 * Java codeAction parameters. reused from
 * https://github.com/eclipse/lsp4mp/blob/master/microprofile.jdt/org.eclipse.lsp4mp.jdt.core/src/main/java/org/eclipse/lsp4mp/commons/MicroProfileJavaCodeActionParams.java
 *
 * @author credit to Angelo ZERR
 *
 */
public class JakartaJavaCodeActionParams extends CodeActionParams {

    private boolean resourceOperationSupported;

    public JakartaJavaCodeActionParams() {
        super();
    }

    public JakartaJavaCodeActionParams(final TextDocumentIdentifier textDocument, final Range range,
            final CodeActionContext context) {
        super(textDocument, range, context);
    }

    public String getUri() {
        return getTextDocument().getUri();
    }

    public boolean isResourceOperationSupported() {
        return resourceOperationSupported;
    }

    public void setResourceOperationSupported(boolean resourceOperationSupported) {
        this.resourceOperationSupported = resourceOperationSupported;
    }

}