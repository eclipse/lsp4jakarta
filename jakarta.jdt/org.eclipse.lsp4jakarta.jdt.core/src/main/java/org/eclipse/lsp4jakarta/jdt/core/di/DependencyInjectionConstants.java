/*******************************************************************************
* Copyright (c) 2021 IBM Corporation.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0.
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     Himanshu Chotwani
*******************************************************************************/

package org.eclipse.lsp4jakarta.jdt.core.di;

import org.eclipse.lsp4j.DiagnosticSeverity;

public class DependencyInjectionConstants {

    /* Annotation Constants */
    public static final String PRODUCES = "Produces";
    public static final String INJECT = "Inject";
    public static final String QUALIFIER = "Qualifier";
    public static final String NAMED = "Named";

    /* Diagnostics fields constants */
    public static final String DIAGNOSTIC_SOURCE = "jakarta-di";
    public static final String DIAGNOSTIC_CODE_INJECT_FINAL = "RemoveInjectOrFinal";
    public static final String DIAGNOSTIC_CODE_INJECT_CONSTRUCTOR = "RemoveInject";
    public static final String DIAGNOSTIC_CODE_INJECT_ABSTRACT = "RemoveInjectOrAbstract";
    public static final String DIAGNOSTIC_CODE_INJECT_STATIC = "RemoveInjectOrStatic";
    public static final String DIAGNOSTIC_CODE_INJECT_GENERIC = "RemoveInjectForGeneric";

    public static final DiagnosticSeverity SEVERITY = DiagnosticSeverity.Error;
}
