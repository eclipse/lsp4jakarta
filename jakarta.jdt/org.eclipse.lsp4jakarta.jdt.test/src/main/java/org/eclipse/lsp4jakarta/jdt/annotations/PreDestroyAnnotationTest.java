/*******************************************************************************
* Copyright (c) 2021 IBM Corporation and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0.
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     IBM Corporation - initial API and implementation
*******************************************************************************/
package org.eclipse.lsp4jakarta.jdt.annotations;

import static org.eclipse.lsp4jakarta.jdt.core.JakartaForJavaAssert.*;

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

public class PreDestroyAnnotationTest extends BaseJakartaTest {

    protected static JDTUtils JDT_UTILS = new JDTUtils();

    @Test
    public void GeneratedAnnotation() throws Exception {
        IJavaProject javaProject = loadJavaProject("jakarta-sample", "");
        IFile javaFile = javaProject.getProject()
                .getFile(new Path("src/main/java/io/openliberty/sample/jakarta/annotations/PreDestroyAnnotation.java"));
        String uri = javaFile.getLocation().toFile().toURI().toString();

        JakartaDiagnosticsParams diagnosticsParams = new JakartaDiagnosticsParams();
        diagnosticsParams.setUris(Arrays.asList(uri));

        // expected annotations
        
        Diagnostic d1 = d(20, 16, 28, "A method with the annotation @PreDestroy must not have any parameters.",
                DiagnosticSeverity.Error, "jakarta-annotations", "PreDestroyParams");
        
        Diagnostic d2 = d(26, 20, 31, "A method with the annotation @PreDestroy must not be static.",
                DiagnosticSeverity.Error, "jakarta-annotations", "PreDestroyStatic");
        d2.setData(9);
        
        Diagnostic d3 = d(31, 13, 25, "A method with the annotation @PreDestroy must not throw checked exceptions.",
                DiagnosticSeverity.Warning, "jakarta-annotations", "PreDestroyException");

        assertJavaDiagnostics(diagnosticsParams, JDT_UTILS, d2, d1, d3);
        
        
        JakartaJavaCodeActionParams codeActionParams = createCodeActionParams(uri, d1);
        TextEdit te = te(19, 1, 20, 1,"");
        TextEdit te1 = te(20, 29, 20, 40,"");
        CodeAction ca = ca(uri, "Remove @PreDestroy", d1, te);
        CodeAction ca1= ca(uri, "Remove all parameters", d1, te1);
        assertJavaCodeAction(codeActionParams, JDT_UTILS, ca, ca1);
        
        JakartaJavaCodeActionParams codeActionParams1 = createCodeActionParams(uri, d2);
        TextEdit te2 = te(25, 1, 26, 1,"");
        TextEdit te3 = te(26, 7, 26, 14,"");
        CodeAction ca2 = ca(uri, "Remove @PreDestroy", d2, te2);
        CodeAction ca3= ca(uri, "Remove the 'static' modifier from this method", d2, te3);
        assertJavaCodeAction(codeActionParams1, JDT_UTILS, ca2, ca3);

    }

}
