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

package org.eclipse.jakartals.commons;

import org.eclipse.lsp4j.CodeActionContext;
import org.eclipse.lsp4j.CodeActionParams;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextDocumentIdentifier;

/**
 * This is a duplicate JakartaJavaCodeActionParams from jakarta.ls. This class is
 * required so that the eclipse test plugin (org.eclipse.lsp4jakarta.tests) can
 * make use of JakartaJavaCodeActionParams.
 * 
 * @author Kathryn Kodama
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