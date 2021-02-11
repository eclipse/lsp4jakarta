/*******************************************************************************
 * Copyright (c) 2021 IBM Corporation, Matthew Shocrylas and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation, Matthew Shocrylas - initial API and implementation
 *******************************************************************************/

package org.jakarta.jdt.jax_rs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.lsp4j.DiagnosticSeverity;

public class Jax_RSConstants {

    public static final String RESOURCE_METHOD = "ResourceMethod";
    
    /* Annotation Constants */
    public static final ArrayList<String> METHOD_DESIGNATORS = new ArrayList<String>(List.of(
            "Path", "GET", "POST", "PUT", "DELETE", "HEAD"));

    /* Source */
    public static final String DIAGNOSTIC_SOURCE = "jakarta-jax_rs";

    /* Severity */
    public static final DiagnosticSeverity SEVERITY = DiagnosticSeverity.Error;
    
    /* Diagnostics fields constants */
    public static final String DIAGNOSTIC_CODE = "AddPublicResourceMethod";

}
