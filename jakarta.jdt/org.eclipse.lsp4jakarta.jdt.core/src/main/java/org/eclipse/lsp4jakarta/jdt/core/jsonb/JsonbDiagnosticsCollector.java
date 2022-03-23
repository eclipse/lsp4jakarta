/*******************************************************************************
* Copyright (c) 2020, 2022 IBM Corporation, Matheus Cruz and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0.
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     IBM Corporation, Matheus Cruz, Yijia Jing - initial API and implementation
*******************************************************************************/

package org.eclipse.lsp4jakarta.jdt.core.jsonb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IAnnotatable;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4jakarta.jdt.core.DiagnosticsCollector;
import org.eclipse.lsp4jakarta.jdt.core.JDTUtils;
import org.eclipse.lsp4jakarta.jdt.core.JakartaCorePlugin;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

/**
 * This class contains logic for Jsonb diagnostics:
 * 1) Multiple JsonbCreator annotations on constructors will cause a diagnostic.
 * 2) JsonbTransient not being a mutually exclusive Jsonb annotation will cause a diagnostic. 
 */
public class JsonbDiagnosticsCollector implements DiagnosticsCollector {

    @Override
    public void completeDiagnostic(Diagnostic diagnostic) {
        diagnostic.setSource(JsonbConstants.DIAGNOSTIC_SOURCE);
        diagnostic.setSeverity(DiagnosticSeverity.Error);
    }

    @Override
    public void collectDiagnostics(ICompilationUnit unit, List<Diagnostic> diagnostics) {
        if (unit == null)
            return;

        try {
            collectJsonbCreatorDiagnostics(unit, diagnostics);
            collectJsonbTransientFieldDiagnostics(unit, diagnostics);
        } catch (JavaModelException e) {
            JakartaCorePlugin.logException("Cannot calculate jakarta-jsonb diagnostics", e);
        }
    }
    
    private void collectJsonbCreatorDiagnostics(ICompilationUnit unit,
            List<Diagnostic> diagnostics) throws JavaModelException {
            List<IMethod> methods = new ArrayList<>();
        for (IType type : unit.getAllTypes()) {
            for (IMethod method : type.getMethods())
                for (IAnnotation annotation : method.getAnnotations())
                    if (method.isConstructor() || Flags.isStatic(method.getFlags()))
                        if (annotation.getElementName().equals(JsonbConstants.JSONB_CREATOR))
                            methods.add(method);

            if (methods.size() > JsonbConstants.MAX_METHOD_WITH_JSONBCREATOR) {
                for (IMethod method : methods) {
                    Diagnostic diagnostic = createDiagnosticBy(unit, method, JsonbConstants.JSONB_CREATOR);
                    diagnostics.add(diagnostic);
                }
            }
        }
    }

    private void collectJsonbTransientFieldDiagnostics(ICompilationUnit unit,
            List<Diagnostic> diagnostics) throws JavaModelException {
        for (IType type : unit.getAllTypes()) {
            for (IField field : type.getFields()) {
                List<String> jsonbAnnotationsForField = getJsonbAnnotationNamesForField(field);
                if (jsonbAnnotationsForField.contains(JsonbConstants.JSONB_TRANSIENT)) {
                    if (hasJsonbAnnotationOtherThanTransient(field)) {
                        Diagnostic diagnostic = createDiagnosticBy(unit, field, JsonbConstants.JSONB_TRANSIENT);
                        diagnostic.setData((JsonArray)(new Gson().toJsonTree(jsonbAnnotationsForField)));
                        diagnostics.add(diagnostic);
                    }
                    // Diagnostics on the getter and setter of the field are created when they are
                    // annotated with Jsonb annotations other than JsonbTransient.
                    for (IMethod accessor : JDTUtils.getAccessors(field)) {
                        if (hasJsonbAnnotationOtherThanTransient(accessor)) {
                            Diagnostic diagnostic = createDiagnosticBy(unit, accessor, JsonbConstants.JSONB_TRANSIENT);
                            diagnostics.add(diagnostic);
                        }
                    }
                }
            }
        }
    }

    private Diagnostic createDiagnosticBy(ICompilationUnit unit,
            IMember member, String diagnosticType) throws JavaModelException {
        Diagnostic d = new Diagnostic();
        ISourceRange sourceRange = JDTUtils.getNameRange(member);
        Range range = JDTUtils.toRange(unit, sourceRange.getOffset(), sourceRange.getLength());
        d.setRange(range);
        d.setSeverity(DiagnosticSeverity.Error);
        d.setSource(JsonbConstants.DIAGNOSTIC_SOURCE);

        if (diagnosticType.equals(JsonbConstants.JSONB_CREATOR))
            addJsonbCreatorSpecificDiagnostics(d);
        else if (diagnosticType.equals(JsonbConstants.JSONB_TRANSIENT))
            addJsonbTransientSpecificDiagnostics(d);

        return d;
    }
    
    private void addJsonbCreatorSpecificDiagnostics(Diagnostic d) {
        d.setMessage(JsonbConstants.ERROR_MESSAGE_JSONB_CREATOR);
        d.setCode(JsonbConstants.DIAGNOSTIC_CODE_ANNOTATION);
    }
    
    private void addJsonbTransientSpecificDiagnostics(Diagnostic d) {
        d.setMessage(JsonbConstants.ERROR_MESSAGE_JSONB_TRANSIENT);
        d.setCode(JsonbConstants.DIAGNOSTIC_CODE_ANNOTATION_TRANSIENT_FIELD);
    }
    
    private boolean hasJsonbAnnotationOtherThanTransient(IAnnotatable annotable) throws JavaModelException {
        for (IAnnotation annotation : annotable.getAnnotations()) {
            String annotationName = annotation.getElementName();
            if (JsonbConstants.JSONB_ANNOTATIONS.contains(annotationName) 
                    && !annotationName.equals(JsonbConstants.JSONB_TRANSIENT)) {
                return true;
            }
        }
        return false;
    }
    
    private List<String> getJsonbAnnotationNamesForField(IField field) throws JavaModelException {
        IAnnotation fieldAnnotations[] = field.getAnnotations();
        List<IAnnotation> fieldAnnotationsList = Arrays.asList(fieldAnnotations);
        List<String> annotationNames = fieldAnnotationsList.stream().map(
                fieldAnnotation -> fieldAnnotation.getElementName()).collect(Collectors.toList());

        List<String> jsonbAnnotationNames = new ArrayList<String>();
        for (String annotation : annotationNames)
            if (annotation.startsWith(JsonbConstants.JSONB_PREFIX))
                jsonbAnnotationNames.add(annotation);
        return jsonbAnnotationNames;
    }
}
