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
 *     Yijia Jing
 *******************************************************************************/

package org.eclipse.lsp4jakarta.jdt.core.jsonp;

import org.eclipse.lsp4j.DiagnosticSeverity;

public class JsonpConstants {

    /* Source */
    public static final String DIAGNOSTIC_SOURCE = "jakarta-jsonp";

    /* Severity */
    public static final DiagnosticSeverity SEVERITY = DiagnosticSeverity.Error;
    
    public static final String CREATE_POINTER = "createPointer";
    public static final String DIAGNOSTIC_CODE_CREATE_POINTER = "InvalidCreatePointerParam";
}
