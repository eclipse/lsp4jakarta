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

import static org.eclipse.lsp4jakarta.jdt.core.JakartaForJavaAssert.assertJavaDiagnostics;
import static org.eclipse.lsp4jakarta.jdt.core.JakartaForJavaAssert.d;

import java.util.Arrays;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4jakarta.commons.JakartaDiagnosticsParams;
import org.eclipse.lsp4jakarta.jdt.core.BaseJakartaTest;
import org.jakarta.jdt.JDTUtils;
import org.junit.Test;

public class ResourceClassConstructorTest extends BaseJakartaTest {
    protected static JDTUtils JDT_UTILS = new JDTUtils();
    
    @Test
    public void MultipleConstructorsWithEqualParams() throws Exception {
        JDTUtils utils = JDT_UTILS;
        IJavaProject javaProject = loadJavaProject("jakarta-sample", "");
        IFile javaFile = javaProject.getProject()
                .getFile(new Path("src/main/java/io/openliberty/sample/jakarta/jax_rs/RootResourceClassConstructors.java"));
        String uri = javaFile.getLocation().toFile().toURI().toString();
        
        JakartaDiagnosticsParams diagnosticsParams = new JakartaDiagnosticsParams();
        diagnosticsParams.setUris(Arrays.asList(uri));
        
        // test expected diagnostics
        Diagnostic d1 = d(15, 8, 37, "Multiple constructors have the same number of parameters, it may be ambiguous which constructor is used.",
                DiagnosticSeverity.Warning, "jakarta-jax_rs", "AmbiguousConstructors");
        
        Diagnostic d2 = d(11, 8, 37, "Multiple constructors have the same number of parameters, it may be ambiguous which constructor is used.",
                DiagnosticSeverity.Warning, "jakarta-jax_rs", "AmbiguousConstructors");
        
        
        assertJavaDiagnostics(diagnosticsParams, utils, d1, d2);

    }
    
    @Test
    public void MultipleConstructorsWithDifferentLength() throws Exception {
        JDTUtils utils = JDT_UTILS;
        IJavaProject javaProject = loadJavaProject("jakarta-sample", "");
        IFile javaFile = javaProject.getProject()
                .getFile(new Path("src/main/java/io/openliberty/sample/jakarta/jax_rs/RootResourceClassConstructors.java"));
        String uri = javaFile.getLocation().toFile().toURI().toString();
        
        JakartaDiagnosticsParams diagnosticsParams = new JakartaDiagnosticsParams();
        diagnosticsParams.setUris(Arrays.asList(uri));
        
        // test expected diagnostics
        Diagnostic d = d(7, 8, 37, "This constructor is unused, as root resource classes will only use the constructor with the most parameters.",
                DiagnosticSeverity.Warning, "jakarta-jax_rs", "UnusedConstructor");
        
        
        
        assertJavaDiagnostics(diagnosticsParams, utils, d);

    }
}
