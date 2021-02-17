package org.eclipse.lsp4jakarta.jdt.persistence;

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

public class JakartaPersistenceTest extends BaseJakartaTest {
	protected static JDTUtils JDT_UTILS = new JDTUtils();
	
	@Test
	public void deleteMapKeyOrMapKeyClass() throws Exception {
		JDTUtils utils = this.JDT_UTILS;
        IJavaProject javaProject = loadJavaProject("jakarta-servlet", "");
        
        IFile javaFile = javaProject.getProject()
                .getFile(new Path("src/main/java/io/openliberty/sample/jakarta/persistence/MapKeyAndMapKeyClassTogether.java"));
        String uri = javaFile.getLocation().toFile().toURI().toString();

        JakartaDiagnosticsParams diagnosticsParams = new JakartaDiagnosticsParams();
        diagnosticsParams.setUris(Arrays.asList(uri));
        
        Diagnostic d1 = d(16, 32, 42, "@MapKeyClass and @MapKey annotations cannot be used on the same field or property",
                DiagnosticSeverity.Error, "jakarta-persistence", "RemoveMapKeyorMapKeyClass");
        
        Diagnostic d2 = d(11, 25, 32, "@MapKeyClass and @MapKey annotations cannot be used on the same field or property",
                DiagnosticSeverity.Error, "jakarta-persistence", "RemoveMapKeyorMapKeyClass");
        
        
        assertJavaDiagnostics(diagnosticsParams, utils, d1, d2);
        
        // test associated quick-fix code action
//        JakartaJavaCodeActionParams codeActionParams1 = createCodeActionParams(uri, d1);
//        
//        TextEdit te1 = te(15, 4, 16, 4, "");
//        TextEdit te2 = te(14, 4, 15, 4, "");
//        CodeAction ca1 = ca(uri, "Remove @MapKeyClass", d1, te1);
//        CodeAction ca2 = ca(uri, "Remove @MapKey", d1, te2);
//        
//        assertJavaCodeAction(codeActionParams1, utils, ca1, ca2);
//        
//        
//        JakartaJavaCodeActionParams codeActionParams2 = createCodeActionParams(uri, d2);
//        
//        TextEdit te3 = te(9, 13, 10, 27, "");
//        TextEdit te4 = te(9, 4, 10, 4, "");
//        CodeAction ca3 = ca(uri, "Remove @MapKeyClass", d2, te3);
//        CodeAction ca4 = ca(uri, "Remove @MapKey", d2, te4);
//        
//        assertJavaCodeAction(codeActionParams2, utils, ca3, ca4);
	}
	

}
