/*******************************************************************************
 * Copyright (c) 2022 IBM Corporation and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Yijia Jing
 *******************************************************************************/
package org.eclipse.lsp4jakarta.jdt.jsonp;

import static org.eclipse.lsp4jakarta.jdt.core.JakartaForJavaAssert.*;

import java.util.Arrays;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4jakarta.commons.JakartaDiagnosticsParams;
import org.eclipse.lsp4jakarta.jdt.core.BaseJakartaTest;
import org.eclipse.lsp4jakarta.jdt.core.JDTUtils;
import org.junit.Test;

public class JakartaJsonpTest extends BaseJakartaTest {
    protected static JDTUtils JDT_UTILS = new JDTUtils();

    @Test
    public void invalidPointerTarget() throws Exception {
        IJavaProject javaProject = loadJavaProject("jakarta-sample", "");
        IFile javaFile = javaProject.getProject().getFile(
                new Path("src/main/java/io/openliberty/sample/jakarta/jsonp/CreatePointerInvalidTarget.java"));
        String uri = javaFile.getLocation().toFile().toURI().toString();
        
        JakartaDiagnosticsParams diagnosticsParams = new JakartaDiagnosticsParams();
        diagnosticsParams.setUris(Arrays.asList(uri));
        
        Diagnostic d1 = d(20, 60, 64, 
                "Json.createPointer target must be a sequence of '/' prefixed tokens or an empty String", 
                DiagnosticSeverity.Error, "jakarta-jsonp", "InvalidCreatePointerArg");
        
        Diagnostic d2 = d(21, 62, 70, 
                "Json.createPointer target must be a sequence of '/' prefixed tokens or an empty String", 
                DiagnosticSeverity.Error, "jakarta-jsonp", "InvalidCreatePointerArg");
        
        Diagnostic d3 = d(22, 60, 80, 
                "Json.createPointer target must be a sequence of '/' prefixed tokens or an empty String", 
                DiagnosticSeverity.Error, "jakarta-jsonp", "InvalidCreatePointerArg");
        
        assertJavaDiagnostics(diagnosticsParams, JDT_UTILS, d1, d2, d3);
    }
}
