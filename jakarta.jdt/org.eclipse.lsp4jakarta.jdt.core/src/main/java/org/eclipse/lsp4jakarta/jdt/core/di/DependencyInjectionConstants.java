
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
    
    public static final DiagnosticSeverity SEVERITY = DiagnosticSeverity.Error;
}
