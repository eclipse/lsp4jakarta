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
*     Hani Damlaj
*******************************************************************************/

package org.jakarta.jdt.cdi;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.lsp4j.DiagnosticSeverity;

public class ManagedBeanConstants {
    /* Annotation Constants */
    public static final String PRODUCES = "Produces";
    public static final String INJECT = "Inject";
    public static final String DISPOSES = "Disposes";
    public static final String OBSERVES = "Observes";
    public static final String OBSERVES_ASYNC = "ObservesAsync";
    
    
    public static final String DIAGNOSTIC_SOURCE = "jakarta-cdi";
    public static final String DIAGNOSTIC_CODE = "InvalidManagedBeanAnnotation";
    public static final String DIAGNOSTIC_CODE_PRODUCES_INJECT = "RemoveProducesOrInject";
    
    public static final String CONSTRUCTOR_DIAGNOSTIC_CODE = "InvalidManagedBeanConstructor";
    
    public static final String DIAGNOSTIC_CODE_INJECT_DISPOSES = "RemoveInjectOrDisposes";
    public static final String DIAGNOSTIC_CODE_INJECT_OBSERVES = "RemoveInjectOrObserves";
    public static final String DIAGNOSTIC_CODE_INJECT_OBSERVES_ASYNC = "RemoveInjectOrObservesAsync";
    
    public static final DiagnosticSeverity SEVERITY = DiagnosticSeverity.Error;

    // List can be found in the cdi doc here:
    // https://jakarta.ee/specifications/cdi/3.0/jakarta-cdi-spec-3.0.html#bean_defining_annotations
    public static final Set<String> SCOPES = new HashSet<String>(
            Arrays.asList("Dependent", "ApplicationScoped", "ConversationScoped", "RequestScoped", "SessionScoped",
                    "NormalScope", "Interceptor", "Decorator", "Stereotype"));
}
