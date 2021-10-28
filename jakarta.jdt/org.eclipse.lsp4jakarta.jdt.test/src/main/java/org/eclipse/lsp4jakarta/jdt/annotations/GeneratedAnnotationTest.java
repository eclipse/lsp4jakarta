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

public class GeneratedAnnotationTest extends BaseJakartaTest {

    protected static JDTUtils JDT_UTILS = new JDTUtils();

    @Test
    public void GeneratedAnnotation() throws Exception {
        IJavaProject javaProject = loadJavaProject("jakarta-sample", "");
        IFile javaFile = javaProject.getProject()
                .getFile(new Path("src/main/java/io/openliberty/sample/jakarta/annotations/GeneratedAnnotation.java"));
        String uri = javaFile.getLocation().toFile().toURI().toString();

        JakartaDiagnosticsParams diagnosticsParams = new JakartaDiagnosticsParams();
        diagnosticsParams.setUris(Arrays.asList(uri));

        // expected
        Diagnostic d = d(6, 1, 60, "The 'date' attribute of the Generated annotation MUST follow ISO 8601.",
                DiagnosticSeverity.Error, null, "InvalidDateFormat");

        assertJavaDiagnostics(diagnosticsParams, JDT_UTILS, d);

        // test associated quick-fix code action
        // JakartaJavaCodeActionParams codeActionParams = createCodeActionParams(uri, d);
        // TextEdit te = te(10, 34, 10, 34, " extends HttpServlet");
        // CodeAction ca = ca(uri, "Let 'DontExtendHttpServlet' extend 'HttpServlet'", d, te);
        // assertJavaCodeAction(codeActionParams, JDT_UTILS, ca);
    }

}
