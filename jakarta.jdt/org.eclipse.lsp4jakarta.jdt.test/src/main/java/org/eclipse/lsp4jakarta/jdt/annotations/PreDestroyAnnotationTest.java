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
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4jakarta.commons.JakartaDiagnosticsParams;
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

    }

}
