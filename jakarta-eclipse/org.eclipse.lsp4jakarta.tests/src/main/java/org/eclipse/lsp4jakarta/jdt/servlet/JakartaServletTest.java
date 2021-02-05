package org.eclipse.lsp4jakarta.jdt.servlet;

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

public class JakartaServletTest extends BaseJakartaTest {

    protected static JDTUtils JDT_UTILS = new JDTUtils();

    @Test
    public void ExtendWebServlet() throws Exception {

        JDTUtils utils = JDT_UTILS;
        IJavaProject javaProject = loadJavaProject("jakarta-servlet-project", "");
        IFile javaFile = javaProject.getProject()
                .getFile(new Path("src/main/java/io/openliberty/sample/jakarta/servlet/DontExtendHttpServlet.java"));
        String uri = javaFile.getLocation().toFile().toURI().toString();

        JakartaDiagnosticsParams diagnosticsParams = new JakartaDiagnosticsParams();
        diagnosticsParams.setUris(Arrays.asList(uri));

        // expected
        Diagnostic d = d(10, 13, 34, "Classes annotated with @WebServlet must extend the HttpServlet class.",
                DiagnosticSeverity.Error, "jakarta-servlet", "ExtendHttpServlet");

        assertJavaDiagnostics(diagnosticsParams, utils, d);

        // test associated quick-fix code action
        JakartaJavaCodeActionParams codeActionParams = createCodeActionParams(uri, d);
        TextEdit te = te(10, 34, 10, 34, " extends HttpServlet");
        CodeAction ca = ca(uri, "Let 'DontExtendHttpServlet' extend 'HttpServlet'", d, te);
        assertJavaCodeAction(codeActionParams, utils, ca);
    }

}
