/*******************************************************************************
* Copyright (c) 2021, 2022 IBM Corporation and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0.
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     IBM Corporation, Adit Rada, Yijia Jing - initial API and implementation
*******************************************************************************/
package org.eclipse.lsp4jakarta.jdt.jsonb;

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
import org.eclipse.lsp4jakarta.commons.JakartaDiagnosticsParams;
import org.eclipse.lsp4jakarta.commons.JakartaJavaCodeActionParams;
import org.eclipse.lsp4jakarta.jdt.core.BaseJakartaTest;
import org.eclipse.lsp4jakarta.jdt.core.JDTUtils;
import org.junit.Test;

import com.google.gson.Gson;


public class JsonbDiagnosticsCollectorTest extends BaseJakartaTest {
    protected static JDTUtils JDT_UTILS = new JDTUtils();

    @Test
    public void deleteExtraJsonbCreatorAnnotation() throws Exception {
        JDTUtils utils = JDT_UTILS;
        IJavaProject javaProject = loadJavaProject("jakarta-sample", "");
        IFile javaFile = javaProject.getProject().getFile(
                new Path("src/main/java/io/openliberty/sample/jakarta/jsonb/ExtraJsonbCreatorAnnotations.java"));
        String uri = javaFile.getLocation().toFile().toURI().toString();

        JakartaDiagnosticsParams diagnosticsParams = new JakartaDiagnosticsParams();
        diagnosticsParams.setUris(Arrays.asList(uri));

        Diagnostic d1 = d(18, 11, 39,
                "Only one constructor or static factory method can be annotated with @JsonbCreator in a given class.",
                DiagnosticSeverity.Error, "jakarta-jsonb", "MultipleJsonbCreatorAnnotations");
        
        Diagnostic d2 = d(21, 48, 61,
                "Only one constructor or static factory method can be annotated with @JsonbCreator in a given class.",
                DiagnosticSeverity.Error, "jakarta-jsonb", "MultipleJsonbCreatorAnnotations");

        assertJavaDiagnostics(diagnosticsParams, utils, d1, d2);

        // test code actions
        JakartaJavaCodeActionParams codeActionParams1 = createCodeActionParams(uri, d1);
        TextEdit te1 = te(17, 4, 18, 4, "");
        CodeAction ca1 = ca(uri, "Remove @JsonbCreator", d1, te1);
        
        assertJavaCodeAction(codeActionParams1, utils, ca1);

        JakartaJavaCodeActionParams codeActionParams2 = createCodeActionParams(uri, d2);
        TextEdit te2 = te(20, 4, 21, 4, "");
        CodeAction ca2 = ca(uri, "Remove @JsonbCreator", d2, te2);

        assertJavaCodeAction(codeActionParams2, utils, ca2);
    }
    
    @Test
    public void JsonbTransientNotMutuallyExclusive() throws Exception {
        JDTUtils utils = JDT_UTILS;
        IJavaProject javaProject = loadJavaProject("jakarta-sample", "");
        IFile javaFile = javaProject.getProject()
                .getFile(new Path("src/main/java/io/openliberty/sample/jakarta/jsonb/JsonbTransientDiagnostic.java"));
        String uri = javaFile.getLocation().toFile().toURI().toString();

        JakartaDiagnosticsParams diagnosticsParams = new JakartaDiagnosticsParams();
        diagnosticsParams.setUris(Arrays.asList(uri));

        // Diagnostic for the field "name"
        Diagnostic d1 = d(25, 19, 23,
                "When a class field is annotated with @JsonbTransient, this field, getter or setter must not be annotated with other JSON Binding annotations.",
                DiagnosticSeverity.Error, "jakarta-jsonb", "NonmutualJsonbTransientAnnotation");
        d1.setData(new Gson().toJsonTree(Arrays.asList("JsonbProperty",  "JsonbTransient")));

        // Diagnostic for the field "favoriteLanguage"
        Diagnostic d2 = d(30, 19, 35,
                "When a class field is annotated with @JsonbTransient, this field, getter or setter must not be annotated with other JSON Binding annotations.",
                DiagnosticSeverity.Error, "jakarta-jsonb", "NonmutualJsonbTransientAnnotation");
        d2.setData(new Gson().toJsonTree(Arrays.asList("JsonbProperty", "JsonbAnnotation",  "JsonbTransient")));
        
        // Diagnostic for the field "favoriteEditor"
        Diagnostic d3 = d(39, 19, 33,
                "When an accessor is annotated with @JsonbTransient, then its field or the accessor must not be annotated with other JSON Binding annotations.",
                DiagnosticSeverity.Error, "jakarta-jsonb", "NonmutualJsonbTransientAnnotationOnAccessor");
        d3.setData(new Gson().toJsonTree(Arrays.asList("JsonbProperty")));
        
        // Diagnostic for the getter "getId"
        Diagnostic d4 = d(42, 16, 21,
                "When a class field is annotated with @JsonbTransient, this field, getter or setter must not be annotated with other JSON Binding annotations.",
                DiagnosticSeverity.Error, "jakarta-jsonb", "NonmutualJsonbTransientAnnotation");
        d4.setData(new Gson().toJsonTree(Arrays.asList("JsonbProperty")));
       
        // Diagnostic for the setter "setId"
        Diagnostic d5 = d(49, 17, 22,
                "When a class field is annotated with @JsonbTransient, this field, getter or setter must not be annotated with other JSON Binding annotations.",
                DiagnosticSeverity.Error, "jakarta-jsonb", "NonmutualJsonbTransientAnnotation");
        d5.setData(new Gson().toJsonTree(Arrays.asList("JsonbNillable")));
        
        // Diagnostic for the setter "favoriteEditor"
        Diagnostic d6 = d(73, 19, 36,
                "When an accessor is annotated with @JsonbTransient, then its field or the accessor must not be annotated with other JSON Binding annotations.",
                DiagnosticSeverity.Error, "jakarta-jsonb", "NonmutualJsonbTransientAnnotationOnAccessor");
        d6.setData(new Gson().toJsonTree(Arrays.asList("JsonbNillable", "JsonbTransient")));
  
        assertJavaDiagnostics(diagnosticsParams, utils, d1, d2, d3, d4, d5, d6);


        // Test code actions
        // Quick fix for the field "name"
        JakartaJavaCodeActionParams codeActionParams1 = createCodeActionParams(uri, d1);
        TextEdit te1 = te(24, 4, 25, 4, "");
        TextEdit te2 = te(23, 4, 24, 4, "");
        CodeAction ca1 = ca(uri, "Remove @JsonbTransient", d1, te1);
        CodeAction ca2 = ca(uri, "Remove @JsonbProperty", d1, te2);
        assertJavaCodeAction(codeActionParams1, utils, ca1, ca2);
       
        // Quick fix for the field "favoriteLanguage"
        JakartaJavaCodeActionParams codeActionParams2 = createCodeActionParams(uri, d2);
        TextEdit te3 = te(29, 4, 30, 4, "");
        TextEdit te4 = te(27, 4, 29, 4, "");
        CodeAction ca3 = ca(uri, "Remove @JsonbTransient", d2, te3);
        CodeAction ca4 = ca(uri, "Remove @JsonbProperty, @JsonbAnnotation", d2, te4);
        assertJavaCodeAction(codeActionParams2, utils, ca3, ca4);

    }
}
