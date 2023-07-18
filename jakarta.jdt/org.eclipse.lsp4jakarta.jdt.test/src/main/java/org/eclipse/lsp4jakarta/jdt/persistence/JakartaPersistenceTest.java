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
package org.eclipse.lsp4jakarta.jdt.persistence;

import static org.eclipse.lsp4jakarta.jdt.core.JakartaForJavaAssert.*;

import java.util.Arrays;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
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

public class JakartaPersistenceTest extends BaseJakartaTest {
    protected static JDTUtils JDT_UTILS = new JDTUtils();

    @Test
    public void deleteMapKeyOrMapKeyClass() throws Exception {
        JDTUtils utils = JDT_UTILS;
        IJavaProject javaProject = loadJavaProject("jakarta-sample", "");

        IFile javaFile = javaProject.getProject().getFile(
                new Path("src/main/java/io/openliberty/sample/jakarta/persistence/MapKeyAndMapKeyClassTogether.java"));
        String uri = javaFile.getLocation().toFile().toURI().toString();

        JakartaDiagnosticsParams diagnosticsParams = new JakartaDiagnosticsParams();
        diagnosticsParams.setUris(Arrays.asList(uri));

        Diagnostic d1 = d(16, 32, 42,
                "@MapKeyClass and @MapKey annotations cannot be used on the same field or property.",
                DiagnosticSeverity.Error, "jakarta-persistence", "RemoveMapKeyorMapKeyClass");

        Diagnostic d2 = d(11, 25, 32,
                "@MapKeyClass and @MapKey annotations cannot be used on the same field or property.",
                DiagnosticSeverity.Error, "jakarta-persistence", "RemoveMapKeyorMapKeyClass");

        assertJavaDiagnostics(diagnosticsParams, utils, d1, d2);

        JakartaJavaCodeActionParams codeActionParams1 = createCodeActionParams(uri, d1);

        TextEdit te1 = te(15, 4, 16, 4, "");
        TextEdit te2 = te(14, 4, 15, 4, "");
        CodeAction ca1 = ca(uri, "Remove @MapKeyClass", d1, te1);
        CodeAction ca2 = ca(uri, "Remove @MapKey", d1, te2);

        assertJavaCodeAction(codeActionParams1, utils, ca1, ca2);

        JakartaJavaCodeActionParams codeActionParams2 = createCodeActionParams(uri, d2);

        TextEdit te3 = te(9, 13, 10, 27, "");
        TextEdit te4 = te(9, 4, 10, 4, "");
        CodeAction ca3 = ca(uri, "Remove @MapKeyClass", d2, te3);
        CodeAction ca4 = ca(uri, "Remove @MapKey", d2, te4);

        assertJavaCodeAction(codeActionParams2, utils, ca3, ca4);
    }

    
    @Test
    public void completeMapKeyJoinColumnAnnotation() throws Exception {
        JDTUtils utils = JDT_UTILS;

        IJavaProject javaProject = loadJavaProject("jakarta-sample", "");
        IFile javaFile = javaProject.getProject().getFile(
                new Path("src/main/java/io/openliberty/sample/jakarta/persistence/MultipleMapKeyAnnotations.java"));
        String uri = javaFile.getLocation().toFile().toURI().toString();
        
        JakartaDiagnosticsParams diagnosticsParams = new JakartaDiagnosticsParams();
        diagnosticsParams.setUris(Arrays.asList(uri));

        // test diagnostics are present
        Diagnostic d1 = d(12, 25, 30,
                "A field with multiple @MapKeyJoinColumn annotations must specify both the name and referencedColumnName attributes in the corresponding @MapKeyJoinColumn annotations.",
                DiagnosticSeverity.Error, "jakarta-persistence", "SupplyAttributesToAnnotations");
        Diagnostic d2 = d(12, 25, 30,
                "A field with multiple @MapKeyJoinColumn annotations must specify both the name and referencedColumnName attributes in the corresponding @MapKeyJoinColumn annotations.",
                DiagnosticSeverity.Error, "jakarta-persistence", "SupplyAttributesToAnnotations");

        Diagnostic d3 = d(16, 25, 30,
                "A field with multiple @MapKeyJoinColumn annotations must specify both the name and referencedColumnName attributes in the corresponding @MapKeyJoinColumn annotations.",
                DiagnosticSeverity.Error, "jakarta-persistence", "SupplyAttributesToAnnotations");
        Diagnostic d4 = d(16, 25, 30,
                "A field with multiple @MapKeyJoinColumn annotations must specify both the name and referencedColumnName attributes in the corresponding @MapKeyJoinColumn annotations.",
                DiagnosticSeverity.Error, "jakarta-persistence", "SupplyAttributesToAnnotations");

        Diagnostic d5 = d(20, 25, 30,
                "A field with multiple @MapKeyJoinColumn annotations must specify both the name and referencedColumnName attributes in the corresponding @MapKeyJoinColumn annotations.",
                DiagnosticSeverity.Error, "jakarta-persistence", "SupplyAttributesToAnnotations");
        
        assertJavaDiagnostics(diagnosticsParams, utils, d1, d2, d3, d4, d5); 
        
        // test quick fixes
        JakartaJavaCodeActionParams codeActionParams1 = createCodeActionParams(uri, d1);
        TextEdit te1 = te(10, 4, 11, 23,  "@MapKeyJoinColumn(name = \"\", referencedColumnName = \"\")\n\t@MapKeyJoinColumn(name = \"\", referencedColumnName = \"\")");
        CodeAction ca1 = ca(uri, "Add the missing attributes to the @MapKeyJoinColumn annotation", d1, te1);

        assertJavaCodeAction(codeActionParams1, utils, ca1);
        
        JakartaJavaCodeActionParams codeActionParams2 = createCodeActionParams(uri, d3);
        TextEdit te2 = te(14, 4, 15, 52,  "@MapKeyJoinColumn(referencedColumnName = \"rcn2\", name = \"\")\n\t@MapKeyJoinColumn(name = \"n1\", referencedColumnName = \"\")");
        CodeAction ca2 = ca(uri, "Add the missing attributes to the @MapKeyJoinColumn annotation", d3, te2);

        assertJavaCodeAction(codeActionParams2, utils, ca2);
        
        JakartaJavaCodeActionParams codeActionParams3 = createCodeActionParams(uri, d5);
        TextEdit te3 = te(18, 4, 19, 23,  "@MapKeyJoinColumn(name = \"\", referencedColumnName = \"\")\n\t@MapKeyJoinColumn(name = \"n1\", referencedColumnName = \"rcn1\")");
        CodeAction ca3 = ca(uri, "Add the missing attributes to the @MapKeyJoinColumn annotation", d5, te3);

        assertJavaCodeAction(codeActionParams3, utils, ca3);
    }

    @Test
    public void addEmptyConstructor() throws Exception {
        JDTUtils utils = JDT_UTILS;

        IJavaProject javaProject = loadJavaProject("jakarta-sample", "");
        IFile javaFile = javaProject.getProject().getFile(
                new Path("src/main/java/io/openliberty/sample/jakarta/persistence/EntityMissingConstructor.java"));
        String uri = javaFile.getLocation().toFile().toURI().toString();

        JakartaDiagnosticsParams diagnosticsParams = new JakartaDiagnosticsParams();
        diagnosticsParams.setUris(Arrays.asList(uri));

        // test diagnostics are present
        Diagnostic d = d(5, 13, 37,
                "A class using the @Entity annotation must contain a public or protected constructor with no arguments.",
                DiagnosticSeverity.Error, "jakarta-persistence", "MissingEmptyConstructor");

        assertJavaDiagnostics(diagnosticsParams, utils, d);

        // test quick fixes
        JakartaJavaCodeActionParams codeActionParams1 = createCodeActionParams(uri, d);
        TextEdit te1 = te(7, 4, 7, 4,  "protected EntityMissingConstructor() {\n\t}\n\n\t");
        CodeAction ca1 = ca(uri, "Add a no-arg protected constructor to this class", d, te1);
        TextEdit te2 = te(7, 4, 7, 4,  "public EntityMissingConstructor() {\n\t}\n\n\t");
        CodeAction ca2 = ca(uri, "Add a no-arg public constructor to this class", d, te2);

        assertJavaCodeAction(codeActionParams1, utils, ca1, ca2);
    }

    @Test
    public void removeFinalModifiers() throws Exception {
        JDTUtils utils = JDT_UTILS;

        IJavaProject javaProject = loadJavaProject("jakarta-sample", "");
        IFile javaFile = javaProject.getProject().getFile(
                new Path("src/main/java/io/openliberty/sample/jakarta/persistence/FinalModifiers.java"));
        String uri = javaFile.getLocation().toFile().toURI().toString();

        JakartaDiagnosticsParams diagnosticsParams = new JakartaDiagnosticsParams();
        diagnosticsParams.setUris(Arrays.asList(uri));

        // test diagnostics are present
        Diagnostic d1 = d(10, 21, 28,
                "A class using the @Entity annotation cannot contain any methods that are declared final.",
                DiagnosticSeverity.Error, "jakarta-persistence", "RemoveFinalMethods");
        d1.setData(IJavaElement.METHOD);

        Diagnostic d2 = d(7, 14, 15,
                "A class using the @Entity annotation cannot contain any persistent instance variables that are declared final.",
                DiagnosticSeverity.Error, "jakarta-persistence", "RemoveFinalVariables");
        d2.setData(IJavaElement.FIELD);

        Diagnostic d3 = d(8, 17, 18,
                "A class using the @Entity annotation cannot contain any persistent instance variables that are declared final.",
                DiagnosticSeverity.Error, "jakarta-persistence", "RemoveFinalVariables");
        d3.setData(IJavaElement.FIELD);

        Diagnostic d4 = d(8, 30, 31,
                "A class using the @Entity annotation cannot contain any persistent instance variables that are declared final.",
                DiagnosticSeverity.Error, "jakarta-persistence", "RemoveFinalVariables");
        d4.setData(IJavaElement.FIELD);

        Diagnostic d5 = d(5, 19, 33,
                "A class using the @Entity annotation must not be final.",
                DiagnosticSeverity.Error, "jakarta-persistence", "InvalidClass");
        d5.setData(IJavaElement.TYPE);

        assertJavaDiagnostics(diagnosticsParams, utils, d1, d2, d3, d4, d5);

        // test quick fixes
        JakartaJavaCodeActionParams codeActionParams1 = createCodeActionParams(uri, d1);
        TextEdit te1 = te(10, 10, 10, 16, "");
        CodeAction ca1 = ca(uri,  "Remove the 'final' modifier from this method", d1, te1);

        assertJavaCodeAction(codeActionParams1, utils, ca1);

        JakartaJavaCodeActionParams codeActionParams2 = createCodeActionParams(uri, d2);
        TextEdit te2 = te(7, 4, 7, 10, "");
        CodeAction ca2 = ca(uri,  "Remove the 'final' modifier from this field", d2, te2);

        assertJavaCodeAction(codeActionParams2, utils, ca2);

        JakartaJavaCodeActionParams codeActionParams3 = createCodeActionParams(uri, d3);
        TextEdit te3 = te(8, 4, 8, 10, "");
        CodeAction ca3 = ca(uri,  "Remove the 'final' modifier from this field", d3, te3);

        assertJavaCodeAction(codeActionParams3, utils, ca3);

        JakartaJavaCodeActionParams codeActionParams4 = createCodeActionParams(uri, d4);
        TextEdit te4 = te(8, 4, 8, 10, "");
        CodeAction ca4 = ca(uri,  "Remove the 'final' modifier from this field", d4, te4);

        assertJavaCodeAction(codeActionParams4, utils, ca4);

        JakartaJavaCodeActionParams codeActionParams5 = createCodeActionParams(uri, d5);
        TextEdit te5 = te(5, 6, 5, 12, "");
        CodeAction ca5 = ca(uri, "Remove the 'final' modifier from this class", d5, te5);

        assertJavaCodeAction(codeActionParams5, utils, ca5);
    }
}