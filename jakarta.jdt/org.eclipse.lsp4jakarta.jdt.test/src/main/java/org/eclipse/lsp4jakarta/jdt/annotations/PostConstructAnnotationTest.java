package org.eclipse.lsp4jakarta.jdt.annotations;

import static org.eclipse.lsp4jakarta.jdt.core.JakartaForJavaAssert.assertJavaCodeAction;
import static org.eclipse.lsp4jakarta.jdt.core.JakartaForJavaAssert.assertJavaDiagnostics;
import static org.eclipse.lsp4jakarta.jdt.core.JakartaForJavaAssert.ca;
import static org.eclipse.lsp4jakarta.jdt.core.JakartaForJavaAssert.createCodeActionParams;
import static org.eclipse.lsp4jakarta.jdt.core.JakartaForJavaAssert.d;
import static org.eclipse.lsp4jakarta.jdt.core.JakartaForJavaAssert.te;

import java.util.Arrays;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4jakarta.commons.JakartaDiagnosticsParams;
import org.eclipse.lsp4jakarta.commons.JakartaJavaCodeActionParams;
import org.eclipse.lsp4jakarta.jdt.core.BaseJakartaTest;
import org.eclipse.lsp4jakarta.jdt.core.JDTUtils;
import org.junit.Test;

public class PostConstructAnnotationTest extends BaseJakartaTest{
	
	protected static JDTUtils JDT_UTILS = new JDTUtils();

    @Test
    public void GeneratedAnnotation() throws Exception {
        IJavaProject javaProject = loadJavaProject("jakarta-sample", "");
        IFile javaFile = javaProject.getProject()
                .getFile(new Path("src/main/java/io/openliberty/sample/jakarta/annotations/PostConstructAnnotation.java"));
        String uri = javaFile.getLocation().toFile().toURI().toString();

        JakartaDiagnosticsParams diagnosticsParams = new JakartaDiagnosticsParams();
        diagnosticsParams.setUris(Arrays.asList(uri));

        // expected annotations
        
        Diagnostic d1 = d(15, 16, 28, "A method with the annotation @PostConstruct must be void.",
                DiagnosticSeverity.Error, "jakarta-annotations", "PostConstructReturnType");
        
        Diagnostic d2 = d(20, 13, 25, "A method with the annotation @PostConstruct should not have any parameters.",
                DiagnosticSeverity.Error, "jakarta-annotations", "PostConstructParams");
        
        Diagnostic d3 = d(25, 13, 25, "A method with the annotation @PostConstruct must not throw checked exceptions.",
        		DiagnosticSeverity.Warning, "jakarta-annotations", "PostConstructException");

        assertJavaDiagnostics(diagnosticsParams, JDT_UTILS, d1, d2, d3);
        

    }

}
