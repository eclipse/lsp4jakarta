/*******************************************************************************
* Copyright (c) 2020 IBM Corporation and others.
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

import org.eclipse.lsp4mp.commons.DocumentFormat;
import java.util.List;

/**
 * This is a duplicate JakartaDiagnosticsParams from lsp4jakarta. This class is
 * required so that the eclipse test plugin (org.eclipse.lsp4jakarta.tests) can
 * make use of JakartaDiagnosticsParams.
 * 
 * @author Kathryn Kodama
 *
 */
public class JakartaDiagnosticsParams {

    private List<String> uris;

    private DocumentFormat documentFormat;

    public JakartaDiagnosticsParams() {
        this(null);
    }

    public JakartaDiagnosticsParams(List<String> uris) {
        setUris(uris);
    }

    /**
     * Returns the java file uris list.
     *
     * @return the java file uris list.
     */
    public List<String> getUris() {
        return uris;
    }

    /**
     * Set the java file uris list.
     *
     * @param uris the java file uris list.
     */
    public void setUris(List<String> uris) {
        this.uris = uris;
    }

    public DocumentFormat getDocumentFormat() {
        return documentFormat;
    }

    public void setDocumentFormat(DocumentFormat documentFormat) {
        this.documentFormat = documentFormat;
    }
}
