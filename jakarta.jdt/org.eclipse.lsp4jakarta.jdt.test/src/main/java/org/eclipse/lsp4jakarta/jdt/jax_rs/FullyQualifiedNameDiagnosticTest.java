package org.eclipse.lsp4jakarta.jdt.jax_rs;

import static org.eclipse.lsp4jakarta.jdt.core.JakartaForJavaAssert.assertJavaDiagnosticsPresent;
import static org.eclipse.lsp4jakarta.jdt.core.JakartaForJavaAssert.d;

import java.util.Arrays;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4jakarta.commons.JakartaDiagnosticsParams;
import org.eclipse.lsp4jakarta.jdt.core.BaseJakartaTest;
import org.eclipse.lsp4jakarta.jdt.core.JDTUtils;
import org.junit.Test;

public class FullyQualifiedNameDiagnosticTest extends BaseJakartaTest {

    protected static JDTUtils JDT_UTILS = new JDTUtils();

	@Test
    public void verifyDiagnosticForFullyQualifiedName() throws Exception {
        JDTUtils utils = JDT_UTILS;
        IJavaProject javaProject = loadJavaProject("jakarta-sample", "");
        IFile javaFile = javaProject.getProject()
                .getFile(new Path("src/main/java/io/openliberty/sample/jakarta/jax_rs/FullyQualifiedNameDiagnostic.java"));
        String uri = javaFile.getLocation().toFile().toURI().toString();

        JakartaDiagnosticsParams diagnosticsParams = new JakartaDiagnosticsParams();
        diagnosticsParams.setUris(Arrays.asList(uri));
       
        Diagnostic d = d(8, 13, 46, "Resource methods cannot have more than one entity parameter",
                DiagnosticSeverity.Error, "jakarta-jax_rs", "ResourceMethodMultipleEntityParams");
        assertJavaDiagnosticsPresent(diagnosticsParams, utils, d);

        }

}
