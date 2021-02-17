package org.eclipse.lsp4jakarta.jdt.jax_rs;

import static org.eclipse.lsp4jakarta.jdt.core.JakartaForJavaAssert.*;

import java.util.Arrays;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4jakarta.jdt.core.BaseJakartaTest;
import org.jakarta.jdt.JDTUtils;
import org.junit.Test;

import io.microshed.jakartals.commons.JakartaDiagnosticsParams;
import io.microshed.jakartals.commons.JakartaJavaCodeActionParams;

public class ResourceMethodTest extends BaseJakartaTest {

    protected static JDTUtils JDT_UTILS = new JDTUtils();
    
    @Test
    public void NonPublicMethod() throws Exception {
        JDTUtils utils = JDT_UTILS;
        IJavaProject javaProject = loadJavaProject("JAX-RS", ""); // TODO: create project called JAX-RS
        IFile javaFile = javaProject.getProject()
                .getFile(new Path("PLACEHOLDER FILE"));
        String uri = javaFile.getLocation().toFile().toURI().toString();
        
        JakartaDiagnosticsParams diagnosticsParams = new JakartaDiagnosticsParams();
        diagnosticsParams.setUris(Arrays.asList(uri));
        
        
        Diagnostic d = d(0, 0, 0, "Only public methods may be exposed as resource methods",
                DiagnosticSeverity.Error, "jakarta-jax_rs", "AddPublicResourceMethod");
        
        assertJavaDiagnostics(diagnosticsParams, utils, d);
        
        // Test for quick-fix code action
        JakartaJavaCodeActionParams codeActionParams = createCodeActionParams(uri, d);
        TextEdit te = te(0, 0, 0, 0, "PLACEHOLDER");
        CodeAction ca = ca(uri, "PLACEHOLDER", d, te);
        assertJavaCodeAction(codeActionParams, utils, ca);
    }

}
