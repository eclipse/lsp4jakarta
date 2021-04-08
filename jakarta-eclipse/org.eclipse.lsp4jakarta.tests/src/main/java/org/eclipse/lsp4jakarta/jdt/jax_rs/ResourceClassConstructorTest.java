package org.eclipse.lsp4jakarta.jdt.jax_rs;

import static org.eclipse.lsp4jakarta.jdt.core.JakartaForJavaAssert.assertJavaDiagnostics;
import static org.eclipse.lsp4jakarta.jdt.core.JakartaForJavaAssert.d;

import java.util.Arrays;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4jakarta.commons.JakartaDiagnosticsParams;
import org.eclipse.lsp4jakarta.jdt.core.BaseJakartaTest;
import org.jakarta.jdt.JDTUtils;
import org.junit.Test;

public class ResourceClassConstructorTest extends BaseJakartaTest {
    protected static JDTUtils JDT_UTILS = new JDTUtils();
    
    @Test
    public void MultipleConstructorsWithEqualParams() throws Exception {
        JDTUtils utils = JDT_UTILS;
        IJavaProject javaProject = loadJavaProject("jakarta-sample", "");
        IFile javaFile = javaProject.getProject()
                .getFile(new Path("src/main/java/io/openliberty/sample/jakarta/jax_rs/MultipleEntityParamsResourceMethod.java"));
        String uri = javaFile.getLocation().toFile().toURI().toString();
        
        JakartaDiagnosticsParams diagnosticsParams = new JakartaDiagnosticsParams();
        diagnosticsParams.setUris(Arrays.asList(uri));
        
        
        Diagnostic d = d(20, 17, 30, "Only public methods may be exposed as resource methods",
                DiagnosticSeverity.Error, "jakarta-jax_rs", "NonPublicResourceMethod");
        
        assertJavaDiagnostics(diagnosticsParams, utils, d);

    }
    
    @Test
    public void MultipleConstructorsWithDifferentLength() throws Exception {
        JDTUtils utils = JDT_UTILS;
        IJavaProject javaProject = loadJavaProject("jakarta-sample", "");
        IFile javaFile = javaProject.getProject()
                .getFile(new Path("src/main/java/io/openliberty/sample/jakarta/jax_rs/MultipleEntityParamsResourceMethod.java"));
        String uri = javaFile.getLocation().toFile().toURI().toString();
        
        JakartaDiagnosticsParams diagnosticsParams = new JakartaDiagnosticsParams();
        diagnosticsParams.setUris(Arrays.asList(uri));
        
        
        Diagnostic d = d(20, 17, 30, "Only public methods may be exposed as resource methods",
                DiagnosticSeverity.Error, "jakarta-jax_rs", "NonPublicResourceMethod");
        
        assertJavaDiagnostics(diagnosticsParams, utils, d);

    }
}
