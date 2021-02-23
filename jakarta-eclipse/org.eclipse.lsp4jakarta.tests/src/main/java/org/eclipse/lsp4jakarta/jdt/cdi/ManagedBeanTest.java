package org.eclipse.lsp4jakarta.jdt.cdi;

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

public class ManagedBeanTest extends BaseJakartaTest {

    protected static JDTUtils JDT_UTILS = new JDTUtils();

    @Test
    public void managedBeanAnnotations() throws Exception {
        IJavaProject javaProject = loadJavaProject("jakarta-sample", "");
        IFile javaFile = javaProject.getProject()
                .getFile(new Path("src/main/java/io/openliberty/sample/jakarta/cdi/ManagedBean.java"));
        String uri = javaFile.getLocation().toFile().toURI().toString();

        JakartaDiagnosticsParams diagnosticsParams = new JakartaDiagnosticsParams();
        diagnosticsParams.setUris(Arrays.asList(uri));

        // test expected diagnostic
        Diagnostic d = d(6, 12, 13,
                "A managed bean with a non-static public field must not declare any scope other than @Dependent",
                DiagnosticSeverity.Error, "jakarta-cdi", "InvalidManagedBeanAnnotation");

        assertJavaDiagnostics(diagnosticsParams, JDT_UTILS, d);
        
        // test expected quick-fix      
        JakartaJavaCodeActionParams codeActionParams = createCodeActionParams(uri, d);
        TextEdit te = te(2, 0, 5, 0, "import jakarta.enterprise.context.Dependent;\nimport jakarta.exterprise.context.*;\n\n@Dependent\n");
        CodeAction ca = ca(uri, "Replace current scope with @Dependent", d, te);
        assertJavaCodeAction(codeActionParams, JDT_UTILS, ca);
    }

}
