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
*     IBM Corporation - initial API and implementation
*******************************************************************************/
package org.eclipse.lsp4jakarta.jdt.websocket;

import static org.eclipse.lsp4jakarta.jdt.core.JakartaForJavaAssert.assertJavaDiagnostics;
import static org.eclipse.lsp4jakarta.jdt.core.JakartaForJavaAssert.d;

import java.util.Arrays;

import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.lsp4jakarta.jdt.core.BaseJakartaTest;
import org.eclipse.lsp4jakarta.jdt.core.JDTUtils;
import org.eclipse.lsp4jakarta.commons.JakartaDiagnosticsParams;
import org.junit.Test;

public class JakartaWebSocketTest extends BaseJakartaTest {
    protected static JDTUtils JDT_UTILS = new JDTUtils();

    @Test
    public void addPathParamsAnnotation() throws Exception {
        IJavaProject javaProject = loadJavaProject("jakarta-sample", "");
        IFile javaFile = javaProject.getProject()
                .getFile(new Path("src/main/java/io/openliberty/sample/jakarta/websocket/AnnotationTest.java"));
        String uri = javaFile.getLocation().toFile().toURI().toString();

        JakartaDiagnosticsParams diagnosticsParams = new JakartaDiagnosticsParams();
        diagnosticsParams.setUris(Arrays.asList(uri));

        // OnOpen PathParams Annotation check
        Diagnostic d1 = d(19, 47, 64,
                "Parameters of type String, any Java primitive type, or boxed version thereof must be annotated with @PathParams.",
                DiagnosticSeverity.Error, "jakarta-websocket", "AddPathParamsAnnotation"
        );
        
        // OnClose PathParams Annotation check
        Diagnostic d2 = d(25, 49, 67,
                "Parameters of type String, any Java primitive type, or boxed version thereof must be annotated with @PathParams.",
                DiagnosticSeverity.Error, "jakarta-websocket", "AddPathParamsAnnotation"
        );
        
        Diagnostic d3 = d(25, 76, 94,
                "Parameters of type String, any Java primitive type, or boxed version thereof must be annotated with @PathParams.",
                DiagnosticSeverity.Error, "jakarta-websocket", "AddPathParamsAnnotation"
        );

        Diagnostic d4 = d(31, 48, 65,
                "Parameters of type String, any Java primitive type, or boxed version thereof must be annotated with @PathParams.",
                DiagnosticSeverity.Error, "jakarta-websocket", "AddPathParamsAnnotation"
        );

        assertJavaDiagnostics(diagnosticsParams, JDT_UTILS, d1, d2, d3, d4);        
    }

    @Test
    public void changeInvalidParamType() throws Exception {
        IJavaProject javaProject = loadJavaProject("jakarta-sample", "");
        IFile javaFile = javaProject.getProject()
                .getFile(new Path("src/main/java/io/openliberty/sample/jakarta/websocket/InvalidParamType.java"));
        String uri = javaFile.getLocation().toFile().toURI().toString();

        JakartaDiagnosticsParams diagnosticsParams = new JakartaDiagnosticsParams();
        diagnosticsParams.setUris(Arrays.asList(uri));

        // OnOpen Invalid Param Types
        Diagnostic d1 = d(19, 47, 59,
                "Invalid parameter type. When using @OnOpen, parameter must be of type: \n- jakarta.websocket.EndpointConfig\n- jakarta.websocket.Session\n- annotated with @PathParams and of type String or any Java primitive type or boxed version thereof",
                DiagnosticSeverity.Error, "jakarta-websocket", "OnOpenChangeInvalidParam");
        
        // OnClose Invalid Param Type
        Diagnostic d2 = d(24, 73, 85,
                "Invalid parameter type. When using @OnClose, parameter must be of type: \n- jakarta.websocket.CloseReason\n- jakarta.websocket.Session\n- annotated with @PathParams and of type String or any Java primitive type or boxed version thereof",
                DiagnosticSeverity.Error, "jakarta-websocket", "OnCloseChangeInvalidParam");
        
        // OnError Invalid Param Type
        Diagnostic d3 = d(29, 48, 60,
                "Invalid parameter type. When using @OnError, parameter must be of type: \n- jakarta.websocket.Session\n- java.lang.Throwable\n- annotated with @PathParams and of type String or any Java primitive type or boxed version thereof",
                DiagnosticSeverity.Error, "jakarta-websocket", "OnErrorChangeInvalidParam");

        assertJavaDiagnostics(diagnosticsParams, JDT_UTILS, d1, d2, d3);
    }
    
    @Test
    public void testPathParamInvalidURI() throws Exception {
        IJavaProject javaProject = loadJavaProject("jakarta-sample", "");
        IFile javaFile = javaProject.getProject().getFile(
                new Path("src/main/java/io/openliberty/sample/jakarta/websockets/PathParamURIWarningTest.java"));
        String uri = javaFile.getLocation().toFile().toURI().toString();

        JakartaDiagnosticsParams diagnosticsParams = new JakartaDiagnosticsParams();
        diagnosticsParams.setUris(Arrays.asList(uri));

        Diagnostic d = d(22, 59, 77, "PathParam value does not match specified Endpoint URI",
                DiagnosticSeverity.Warning, "jakarta-websocket", "ChangePathParamValue");

        assertJavaDiagnostics(diagnosticsParams, JDT_UTILS, d);
    }
}