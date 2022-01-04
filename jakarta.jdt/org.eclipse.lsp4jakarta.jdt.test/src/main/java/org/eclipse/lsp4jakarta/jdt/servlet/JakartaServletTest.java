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
import org.eclipse.lsp4jakarta.commons.JakartaDiagnosticsParams;
import org.eclipse.lsp4jakarta.commons.JakartaJavaCodeActionParams;
import org.eclipse.lsp4jakarta.jdt.core.BaseJakartaTest;
import org.eclipse.lsp4jakarta.jdt.core.JDTUtils;
import org.junit.Ignore;
import org.junit.Test;

public class JakartaServletTest extends BaseJakartaTest {

    protected static JDTUtils JDT_UTILS = new JDTUtils();

    @Test
    @Ignore // getAllSuperTypes() returns nothing for tests. See #232
    public void ExtendWebServlet() throws Exception {
        IJavaProject javaProject = loadJavaProject("jakarta-sample", "");
        IFile javaFile = javaProject.getProject()
                .getFile(new Path("src/main/java/io/openliberty/sample/jakarta/servlet/DontExtendHttpServlet.java"));
        String uri = javaFile.getLocation().toFile().toURI().toString();

        JakartaDiagnosticsParams diagnosticsParams = new JakartaDiagnosticsParams();
        diagnosticsParams.setUris(Arrays.asList(uri));

        // expected
        Diagnostic d = d(10, 13, 34, "Annotated classes with @WebServlet must extend the HttpServlet class.",
                DiagnosticSeverity.Warning, "jakarta-servlet", "ExtendHttpServlet");

        assertJavaDiagnostics(diagnosticsParams, JDT_UTILS, d);

        // test associated quick-fix code action
        JakartaJavaCodeActionParams codeActionParams = createCodeActionParams(uri, d);
        TextEdit te = te(10, 34, 10, 34, " extends HttpServlet");
        CodeAction ca = ca(uri, "Let 'DontExtendHttpServlet' extend 'HttpServlet'", d, te);
        assertJavaCodeAction(codeActionParams, JDT_UTILS, ca);
    }

    @Test
    @Ignore // getAllSuperTypes() returns nothing for tests. See #232
    public void CompleteWebServletAnnotation() throws Exception {
        IJavaProject javaProject = loadJavaProject("jakarta-sample", "");
        IFile javaFile = javaProject.getProject()
                .getFile(new Path("src/main/java/io/openliberty/sample/jakarta/servlet/InvalidWebServlet.java"));
        String uri = javaFile.getLocation().toFile().toURI().toString();

        JakartaDiagnosticsParams diagnosticsParams = new JakartaDiagnosticsParams();
        diagnosticsParams.setUris(Arrays.asList(uri));

        Diagnostic d = d(9, 0, 13,
                "The annotation @WebServlet must define the attribute urlPatterns or the attribute value.",
                DiagnosticSeverity.Error, "jakarta-servlet", "CompleteHttpServletAttributes");

        assertJavaDiagnostics(diagnosticsParams, JDT_UTILS, d);

        JakartaJavaCodeActionParams codeActionParams = createCodeActionParams(uri, d);
        TextEdit te1 = te(9, 0, 10, 0, "@WebServlet(value = \"\")\n");
        CodeAction ca1 = ca(uri, "Add the `value` attribute to @WebServlet", d, te1);

        TextEdit te2 = te(9, 0, 10, 0, "@WebServlet(urlPatterns = \"\")\n");
        CodeAction ca2 = ca(uri, "Add the `urlPatterns` attribute to @WebServlet", d, te2);
        assertJavaCodeAction(codeActionParams, JDT_UTILS, ca1, ca2);
    }

}
