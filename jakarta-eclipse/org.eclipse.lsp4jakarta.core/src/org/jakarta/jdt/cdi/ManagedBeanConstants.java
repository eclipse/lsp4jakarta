package org.jakarta.jdt.cdi;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.lsp4j.DiagnosticSeverity;

public class ManagedBeanConstants {
    public static final String DIAGNOSTIC_SOURCE = "jakarta-cdi";
    public static final String DIAGNOSTIC_CODE = "InvalidManagedBeanAnnotation";
    public static final DiagnosticSeverity SEVERITY = DiagnosticSeverity.Error;
    public static final Set<String> SCOPES = new HashSet<String>(
            Arrays.asList("Dependent", "ApplicationScoped", "ConversationScoped", "RequestScoped", "SessionScoped"));
}
