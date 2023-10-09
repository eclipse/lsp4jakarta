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
import org.eclipse.lsp4jakarta.commons.JakartaJavaCodeActionParams;
import org.eclipse.lsp4jakarta.commons.JakartaJavaDiagnosticsParams;
import org.eclipse.lsp4jakarta.jdt.core.BaseJakartaTest;
import org.eclipse.lsp4jakarta.jdt.core.utils.IJDTUtils;
import org.eclipse.lsp4jakarta.jdt.internal.core.ls.JDTUtilsLSImpl;
import org.junit.Ignore;
import org.junit.Test;

public class JakartaServletTest extends BaseJakartaTest {

    protected static IJDTUtils IJDT_UTILS = JDTUtilsLSImpl.getInstance();

    @Test
    @Ignore // getAllSuperTypes() returns nothing for tests. See #232
    public void ExtendWebServlet() throws Exception {
        IJavaProject javaProject = loadJavaProject("jakarta-sample", "");
        IFile javaFile = javaProject.getProject().getFile(new Path("src/main/java/io/openliberty/sample/jakarta/servlet/DontExtendHttpServlet.java"));
        String uri = javaFile.getLocation().toFile().toURI().toString();

        JakartaJavaDiagnosticsParams diagnosticsParams = new JakartaJavaDiagnosticsParams();
        diagnosticsParams.setUris(Arrays.asList(uri));

        // expected
        Diagnostic d = d(5, 13, 34, "Annotated classes with @WebServlet must extend the HttpServlet class.",
                         DiagnosticSeverity.Warning, "jakarta-servlet",
                         "WebServletAnnotatedClassUnknownSuperTypeDoesNotExtendHttpServlet");

        assertJavaDiagnostics(diagnosticsParams, IJDT_UTILS, d);

        // test associated quick-fix code action
        JakartaJavaCodeActionParams codeActionParams = createCodeActionParams(uri, d);
        TextEdit te = te(5, 34, 5, 34, " extends HttpServlet");
        CodeAction ca = ca(uri, "Let 'DontExtendHttpServlet' extend 'HttpServlet'", d, te);
        assertJavaCodeAction(codeActionParams, IJDT_UTILS, ca);
    }

    @Test
    @Ignore // getAllSuperTypes() returns nothing for tests. See #232
    public void CompleteWebServletAnnotation() throws Exception {
        IJavaProject javaProject = loadJavaProject("jakarta-sample", "");
        IFile javaFile = javaProject.getProject().getFile(new Path("src/main/java/io/openliberty/sample/jakarta/servlet/InvalidWebServlet.java"));
        String uri = javaFile.getLocation().toFile().toURI().toString();

        JakartaJavaDiagnosticsParams diagnosticsParams = new JakartaJavaDiagnosticsParams();
        diagnosticsParams.setUris(Arrays.asList(uri));

        Diagnostic d = d(9, 0, 13,
                         "The @WebServlet annotation must define the attribute 'urlPatterns' or 'value'.",
                         DiagnosticSeverity.Error, "jakarta-servlet", "WebServletAnnotationMissingAttributes");

        assertJavaDiagnostics(diagnosticsParams, IJDT_UTILS, d);

        JakartaJavaCodeActionParams codeActionParams = createCodeActionParams(uri, d);
        TextEdit te1 = te(9, 0, 10, 0, "@WebServlet(value = \"\")\n");
        CodeAction ca1 = ca(uri, "Add the `value` attribute to @WebServlet", d, te1);

        TextEdit te2 = te(9, 0, 10, 0, "@WebServlet(urlPatterns = \"\")\n");
        CodeAction ca2 = ca(uri, "Add the `urlPatterns` attribute to @WebServlet", d, te2);
        assertJavaCodeAction(codeActionParams, IJDT_UTILS, ca1, ca2);
    }

}
