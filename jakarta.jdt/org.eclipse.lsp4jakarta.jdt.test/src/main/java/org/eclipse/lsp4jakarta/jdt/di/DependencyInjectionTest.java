package org.eclipse.lsp4jakarta.jdt.di;

import static org.eclipse.lsp4jakarta.jdt.core.JakartaForJavaAssert.assertJavaDiagnostics;
import static org.eclipse.lsp4jakarta.jdt.core.JakartaForJavaAssert.d;

import java.util.Arrays;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4jakarta.commons.JakartaDiagnosticsParams;
import org.eclipse.lsp4jakarta.jdt.core.BaseJakartaTest;
import org.eclipse.lsp4jakarta.jdt.core.JDTUtils;
import org.junit.Test;

public class DependencyInjectionTest extends BaseJakartaTest {
    protected static JDTUtils JDT_UTILS = new JDTUtils();

    @Test
    public void DependencyInjectionDiagnostics() throws Exception {
        IJavaProject javaProject = loadJavaProject("jakarta-sample", "");
        IFile javaFile = javaProject.getProject()
                .getFile(new Path("src/main/java/io/openliberty/sample/jakarta/di/GreetingServlet.java"));
        String uri = javaFile.getLocation().toFile().toURI().toString();

        JakartaDiagnosticsParams diagnosticsParams = new JakartaDiagnosticsParams();
        diagnosticsParams.setUris(Arrays.asList(uri));

        /* create expected diagnostics
         * 
         */
        Diagnostic d1 = d(28, 27, 35, "Injectable fields cannot be final",
                DiagnosticSeverity.Error, "jakarta-di", "RemoveInjectOrFinal");
        d1.setData(IType.FIELD);

        Diagnostic d2 = d(44, 25, 39, "Injectable methods cannot be abstract",
                DiagnosticSeverity.Error, "jakarta-di", "RemoveInjectOrAbstract");
        d2.setData(IType.METHOD);
        
        Diagnostic d3 = d(37, 22, 33, "Injectable methods cannot be final",
                DiagnosticSeverity.Error, "jakarta-di", "RemoveInjectOrFinal");
        d3.setData(IType.METHOD);
 
        Diagnostic d4 = d(54, 23, 36, "Injectable methods cannot be generic",
                DiagnosticSeverity.Error, "jakarta-di", "RemoveInject");
        d4.setData(IType.METHOD);
        
        Diagnostic d5 = d(48, 23, 35, "Injectable methods cannot be static",
                DiagnosticSeverity.Error, "jakarta-di", "RemoveInjectOrStatic");
        d5.setData(IType.METHOD);
        

        assertJavaDiagnostics(diagnosticsParams, JDT_UTILS, d1, d2, d3, d4, d5);

    }
}
