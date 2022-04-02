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
            collectJsonbTransientDiagnostics(unit, diagnostics);
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
                    Diagnostic diagnostic = createDiagnosticBy(unit, method, JsonbConstants.ERROR_MESSAGE_JSONB_CREATOR);
                    diagnostics.add(diagnostic);
                }
            }
        }
    }

    private void collectJsonbTransientDiagnostics(ICompilationUnit unit,
            List<Diagnostic> diagnostics) throws JavaModelException {
        for (IType type : unit.getAllTypes()) {
            for (IField field : type.getFields()) {
                //List<IMethod> fieldAccessors = JDTUtils.getAccessors(field);
                collectJsonbTransientFieldDiagnostics(unit, diagnostics, field);
                collectJsonbTransientAccessorDiagnostics(unit, diagnostics, field);
            }
        }
    }
    
    private void collectJsonbTransientFieldDiagnostics(ICompilationUnit unit,
            List<Diagnostic> diagnostics, IField field) throws JavaModelException {
        List<String> jsonbAnnotations = getJsonbAnnotationNames(field);
        
        if (jsonbAnnotations.contains(JsonbConstants.JSONB_TRANSIENT)) {
            createDiagnosticIfMemberHasJsonbAnnotationOtherThanTransient(unit, diagnostics, field,
                    jsonbAnnotations, JsonbConstants.ERROR_MESSAGE_JSONB_TRANSIENT_ON_FIELD);

            // Diagnostics on the getter and setter of the field are created when they are
            // annotated with Jsonb annotations other than JsonbTransient.
            for (IMethod accessor : JDTUtils.getAccessors(field)) {
                jsonbAnnotations = getJsonbAnnotationNames(accessor);
                createDiagnosticIfMemberHasJsonbAnnotationOtherThanTransient(unit, diagnostics, accessor,
                        jsonbAnnotations, JsonbConstants.ERROR_MESSAGE_JSONB_TRANSIENT_ON_FIELD);
            }
        }
    }
    
    private void collectJsonbTransientAccessorDiagnostics(ICompilationUnit unit,
            List<Diagnostic> diagnostics, IField field) throws JavaModelException {
        List<IMethod> accessors = getFieldAccessors(unit, field);
        boolean createdDiagnostifForField = false;
        for (IMethod accessor : accessors) {
            List<String> jsonbAnnotations = getJsonbAnnotationNames(accessor);
            if (jsonbAnnotations.contains(JsonbConstants.JSONB_TRANSIENT)) {
                createDiagnosticIfMemberHasJsonbAnnotationOtherThanTransient(unit, diagnostics, accessor,
                        jsonbAnnotations, JsonbConstants.ERROR_MESSAGE_JSONB_TRANSIENT_ON_ACCESSOR);
                
                // Diagnostic is created when the field of this accessor has a annotation other then JsonbTransient
                jsonbAnnotations = getJsonbAnnotationNames(field);
                if (!createdDiagnostifForField)
                    createdDiagnostifForField = createDiagnosticIfMemberHasJsonbAnnotationOtherThanTransient(unit, diagnostics, field,
                        jsonbAnnotations, JsonbConstants.ERROR_MESSAGE_JSONB_TRANSIENT_ON_ACCESSOR);
            }
        }
    }
    
    private List<IMethod> getFieldAccessors(ICompilationUnit unit, IField field) throws JavaModelException {
        List<IMethod> accessors = new ArrayList<IMethod>();
        String fieldName = field.getElementName();
        fieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        String getterName = "get" + fieldName;
        String setterName = "set" + fieldName;
        
        for (IType type : unit.getAllTypes()) {
            for (IMethod method : type.getMethods()) {
                String methodName = method.getElementName();
                if (methodName.equals(getterName) || methodName.equals(setterName))
                    accessors.add(method);
            }
        }
        return accessors;
    }
    
    private boolean createDiagnosticIfMemberHasJsonbAnnotationOtherThanTransient(ICompilationUnit unit,
            List<Diagnostic> diagnostics, IMember member, List<String> jsonbAnnotations,
            String diagnosticErrorMessage) throws JavaModelException {
        if (hasJsonbAnnotationOtherThanTransient(jsonbAnnotations)) {
            Diagnostic diagnostic = createDiagnosticBy(unit, member, diagnosticErrorMessage);
            diagnostic.setData((JsonArray)(new Gson().toJsonTree(jsonbAnnotations)));
            diagnostics.add(diagnostic);
            return true;
        }
        return false;
    }
    
    private List<String> getJsonbAnnotationNames(IAnnotatable annotable) throws JavaModelException {
        IAnnotation annotations[] = annotable.getAnnotations();
        List<IAnnotation> annotationsList = Arrays.asList(annotations);
        List<String> annotationNames = annotationsList.stream().map(
                fieldAnnotation -> fieldAnnotation.getElementName()).collect(Collectors.toList());

        List<String> jsonbAnnotationNames = new ArrayList<String>();
        for (String annotation : annotationNames)
            if (JsonbConstants.JSONB_ANNOTATIONS.contains(annotation))
                jsonbAnnotationNames.add(annotation);
        return jsonbAnnotationNames;
    }
    
    private boolean hasJsonbAnnotationOtherThanTransient(List<String> jsonbAnnotations) throws JavaModelException {
        for (String annotationName : jsonbAnnotations)
            if (JsonbConstants.JSONB_ANNOTATIONS.contains(annotationName) 
                    && !annotationName.equals(JsonbConstants.JSONB_TRANSIENT))
                return true;
        return false;
    }
    
    private Diagnostic createDiagnosticBy(ICompilationUnit unit,
            IMember member, String diagnosticErrorMessage) throws JavaModelException {
        Diagnostic d = new Diagnostic();
        ISourceRange sourceRange = JDTUtils.getNameRange(member);
        Range range = JDTUtils.toRange(unit, sourceRange.getOffset(), sourceRange.getLength());
        d.setRange(range);
        d.setSeverity(DiagnosticSeverity.Error);
        d.setSource(JsonbConstants.DIAGNOSTIC_SOURCE);
        d.setMessage(diagnosticErrorMessage);

        if (diagnosticErrorMessage.equals(JsonbConstants.ERROR_MESSAGE_JSONB_CREATOR))
            d.setCode(JsonbConstants.DIAGNOSTIC_CODE_ANNOTATION);
        else if (diagnosticErrorMessage.equals(JsonbConstants.ERROR_MESSAGE_JSONB_TRANSIENT_ON_FIELD))
            d.setCode(JsonbConstants.DIAGNOSTIC_CODE_ANNOTATION_TRANSIENT_FIELD);
        else if (diagnosticErrorMessage.equals(JsonbConstants.ERROR_MESSAGE_JSONB_TRANSIENT_ON_ACCESSOR))
            d.setCode(JsonbConstants.DIAGNOSTIC_CODE_ANNOTATION_TRANSIENT_ACCESSOR);

        return d;
    }
}
