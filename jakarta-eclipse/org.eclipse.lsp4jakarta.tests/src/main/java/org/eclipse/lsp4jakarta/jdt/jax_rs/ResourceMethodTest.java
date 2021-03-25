/*******************************************************************************
 * Copyright (c) 2021 IBM Corporation, Matthew Shocrylas and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation, Matthew Shocrylas - initial API and implementation
 *******************************************************************************/

package org.eclipse.lsp4jakarta.jdt.jax_rs;

import static org.eclipse.lsp4jakarta.jdt.core.JakartaForJavaAssert.*;

import java.util.Arrays;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.jakartals.commons.JakartaDiagnosticsParams;
import org.eclipse.jakartals.commons.JakartaJavaCodeActionParams;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4jakarta.jdt.core.BaseJakartaTest;
import org.jakarta.jdt.JDTUtils;
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

}
