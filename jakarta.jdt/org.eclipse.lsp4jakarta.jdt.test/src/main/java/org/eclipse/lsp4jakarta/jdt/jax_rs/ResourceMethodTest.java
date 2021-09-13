/*******************************************************************************
 * Copyright (c) 2021 IBM Corporation, Matthew Shocrylas, Bera Sogut and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation, Matthew Shocrylas - initial API and implementation, Bera Sogut
 *******************************************************************************/

package org.eclipse.lsp4jakarta.jdt.jax_rs;

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

public class ResourceMethodTest extends BaseJakartaTest {

    protected static JDTUtils JDT_UTILS = new JDTUtils();
    
    @Test
    public void NonPublicMethod() throws Exception {
        JDTUtils utils = JDT_UTILS;
        IJavaProject javaProject = loadJavaProject("jakarta-sample", "");
        IFile javaFile = javaProject.getProject()
                .getFile(new Path("src/main/java/io/openliberty/sample/jakarta/jax_rs/NotPublicResourceMethod.java"));
        String uri = javaFile.getLocation().toFile().toURI().toString();
        
        JakartaDiagnosticsParams diagnosticsParams = new JakartaDiagnosticsParams();
        diagnosticsParams.setUris(Arrays.asList(uri));
        
        
        Diagnostic d = d(20, 17, 30, "Only public methods may be exposed as resource methods",
                DiagnosticSeverity.Error, "jakarta-jax_rs", "NonPublicResourceMethod");
        
        assertJavaDiagnostics(diagnosticsParams, utils, d);
        
        // Test for quick-fix code action
        JakartaJavaCodeActionParams codeActionParams = createCodeActionParams(uri, d);
        TextEdit te = te(20, 4, 20, 11, "public"); // range may need to change
        CodeAction ca = ca(uri, "Make method public", d, te);
        assertJavaCodeAction(codeActionParams, utils, ca);
    }

    @Test
    public void multipleEntityParamsMethod() throws Exception {
        JDTUtils utils = JDT_UTILS;
        IJavaProject javaProject = loadJavaProject("jakarta-sample", "");
        IFile javaFile = javaProject.getProject()
                .getFile(new Path("src/main/java/io/openliberty/sample/jakarta/jax_rs/MultipleEntityParamsResourceMethod.java"));
        String uri = javaFile.getLocation().toFile().toURI().toString();

        JakartaDiagnosticsParams diagnosticsParams = new JakartaDiagnosticsParams();
        diagnosticsParams.setUris(Arrays.asList(uri));


        Diagnostic d = d(21, 13, 46, "Resource methods cannot have more than one entity parameter",
                DiagnosticSeverity.Error, "jakarta-jax_rs", "ResourceMethodMultipleEntityParams");

        assertJavaDiagnostics(diagnosticsParams, utils, d);


        // Test for quick-fix code action
        JakartaJavaCodeActionParams codeActionParams = createCodeActionParams(uri, d);

        TextEdit te1 = te(21, 112, 21, 130, "");
        CodeAction ca1 = ca(uri, "Remove all entity parameters except entityParam1", d, te1);

        TextEdit te2 = te(21, 47, 21, 68, "");
        CodeAction ca2 = ca(uri, "Remove all entity parameters except entityParam2", d, te2);

        assertJavaCodeAction(codeActionParams, utils, ca1, ca2);
    }

}
