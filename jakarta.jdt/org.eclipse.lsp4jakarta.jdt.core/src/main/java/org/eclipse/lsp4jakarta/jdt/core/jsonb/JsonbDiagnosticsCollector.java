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
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IAnnotatable;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4jakarta.jdt.core.AbstractDiagnosticsCollector;
import org.eclipse.lsp4jakarta.jdt.core.JDTUtils;
import org.eclipse.lsp4jakarta.jdt.core.JakartaCorePlugin;
import org.eclipse.lsp4jakarta.jdt.core.Messages;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

/**
 * This class contains logic for Jsonb diagnostics:
 * 1) Multiple JsonbCreator annotations on constructors will cause a diagnostic.
 * 2) JsonbTransient not being a mutually exclusive Jsonb annotation will cause a diagnostic. 
 */
public class JsonbDiagnosticsCollector extends AbstractDiagnosticsCollector {

    public JsonbDiagnosticsCollector() {
        super();
    }

    @Override
    protected String getDiagnosticSource() {
        return JsonbConstants.DIAGNOSTIC_SOURCE;
    }

    @Override
    public void collectDiagnostics(ICompilationUnit unit, List<Diagnostic> diagnostics) {
        if (unit == null)
            return;
        try {
            IType[] types = unit.getAllTypes();
            IMethod[] methods;
            IAnnotation[] allAnnotations;

            for (IType type : types) {
                methods = type.getMethods();
                List<IMethod> jonbMethods = new ArrayList<IMethod>();
                // methods
                for (IMethod method : type.getMethods()) {
                    if (isConstructorMethod(method) || Flags.isStatic(method.getFlags())) {
                        allAnnotations = method.getAnnotations();
                        for (IAnnotation annotation : allAnnotations) {
                            if (isMatchedJavaElement(type, annotation.getElementName(), JsonbConstants.JSONB_CREATOR))
                                jonbMethods.add(method);
                        }
                    }
                }
                if (jonbMethods.size() > JsonbConstants.MAX_METHOD_WITH_JSONBCREATOR) {
                    for (IMethod method : methods) {
                        diagnostics.add(createDiagnostic(method, unit, Messages.getMessage("ErrorMessageJsonbCreator"),
                                JsonbConstants.DIAGNOSTIC_CODE_ANNOTATION, null, DiagnosticSeverity.Error));
                    }
                }
                // fields
                for (IField field : type.getFields()) {
                    collectJsonbTransientFieldDiagnostics(unit, type, diagnostics, field);
                    collectJsonbTransientAccessorDiagnostics(unit, type, diagnostics, field);
                }
            }
        } catch (JavaModelException e) {
            JakartaCorePlugin.logException("Cannot calculate jakarta-jsonb diagnostics", e);
        }
    }

    private void collectJsonbTransientFieldDiagnostics(ICompilationUnit unit, IType type, List<Diagnostic> diagnostics, IField field) throws JavaModelException {
        List<String> jsonbAnnotationsForField = getJsonbAnnotationNames(type, field);
        if (jsonbAnnotationsForField.contains(JsonbConstants.JSONB_TRANSIENT_FQ_NAME)) {
            boolean hasAccessorConflict = false;
            // Diagnostics on the accessors of the field are created when they are
            // annotated with Jsonb annotations other than JsonbTransient.
            List<IMethod> accessors = JDTUtils.getFieldAccessors(unit, field);
            for (IMethod accessor : accessors) {
                List<String> jsonbAnnotationsForAccessor = getJsonbAnnotationNames(type, accessor);
                if (hasJsonbAnnotationOtherThanTransient(jsonbAnnotationsForAccessor)) {
                    createJsonbTransientDiagnostic(unit, diagnostics, accessor, jsonbAnnotationsForAccessor,
                    		JsonbConstants.DIAGNOSTIC_CODE_ANNOTATION_TRANSIENT_FIELD);
                    hasAccessorConflict = true;
                }
            }
            // Diagnostic is created on the field if @JsonbTransient is not mutually
            // exclusive or
            // accessor has annotations other than JsonbTransient
            if (hasAccessorConflict || hasJsonbAnnotationOtherThanTransient(jsonbAnnotationsForField))
                createJsonbTransientDiagnostic(unit, diagnostics, field, jsonbAnnotationsForField,
                		JsonbConstants.DIAGNOSTIC_CODE_ANNOTATION_TRANSIENT_FIELD);
        }
    }

    private void collectJsonbTransientAccessorDiagnostics(ICompilationUnit unit, IType type, List<Diagnostic> diagnostics, IField field) throws JavaModelException {
        boolean createDiagnosticForField = false;
        List<String> jsonbAnnotationsForField = getJsonbAnnotationNames(type, field);
        List<IMethod> accessors = JDTUtils.getFieldAccessors(unit, field);
        for (IMethod accessor : accessors) {
            List<String> jsonbAnnotationsForAccessor = getJsonbAnnotationNames(type, accessor);
            boolean hasFieldConflict = false;
            if (jsonbAnnotationsForAccessor.contains(JsonbConstants.JSONB_TRANSIENT_FQ_NAME)) {
                // Diagnostic is created if the field of this accessor has a annotation other
                // then JsonbTransient
                if (hasJsonbAnnotationOtherThanTransient(jsonbAnnotationsForField)) {
                    createDiagnosticForField = true;
                    hasFieldConflict = true;
                }

                // Diagnostic is created on the accessor if field has annotation other than
                // JsonbTransient
                // or if @JsonbTransient is not mutually exclusive
                if (hasFieldConflict || hasJsonbAnnotationOtherThanTransient(jsonbAnnotationsForAccessor))
                    createJsonbTransientDiagnostic(unit, diagnostics, accessor, jsonbAnnotationsForAccessor,
                    		JsonbConstants.DIAGNOSTIC_CODE_ANNOTATION_TRANSIENT_ACCESSOR);

            }
        }
        if (createDiagnosticForField)
            createJsonbTransientDiagnostic(unit, diagnostics, field, jsonbAnnotationsForField,
            		JsonbConstants.DIAGNOSTIC_CODE_ANNOTATION_TRANSIENT_ACCESSOR);
    }

    private boolean createJsonbTransientDiagnostic(ICompilationUnit unit, List<Diagnostic> diagnostics, IMember member,
            List<String> jsonbAnnotations, String code) throws JavaModelException {
        String diagnosticErrorMessage = null;
        if (code.equals(JsonbConstants.DIAGNOSTIC_CODE_ANNOTATION_TRANSIENT_FIELD))
            diagnosticErrorMessage = Messages.getMessage("ErrorMessageJsonbTransientOnField");
        else if (code.equals(JsonbConstants.DIAGNOSTIC_CODE_ANNOTATION_TRANSIENT_ACCESSOR))
            diagnosticErrorMessage = Messages.getMessage("ErrorMessageJsonbTransientOnAccessor");
        // convert to simple name for current tests
        List<String> diagnosticData = jsonbAnnotations.stream().map(annotation -> getSimpleName(annotation))
                .collect(Collectors.toList());
        diagnostics.add(createDiagnostic(member, unit, diagnosticErrorMessage, code, 
                (JsonArray) (new Gson().toJsonTree(diagnosticData)), DiagnosticSeverity.Error));
        return true;
    }

    private List<String> getJsonbAnnotationNames(IType type, IAnnotatable annotable) throws JavaModelException {
        List<String> jsonbAnnotationNames = new ArrayList<String>();
        IAnnotation annotations[] = annotable.getAnnotations();
        for (IAnnotation annotation : annotations) {
            String matchedAnnotation = getMatchedJavaElementName(type, annotation.getElementName(), JsonbConstants.JSONB_ANNOTATIONS.toArray(String[]::new));
            if (matchedAnnotation != null) {
                jsonbAnnotationNames.add(matchedAnnotation);
            }
        }
        return jsonbAnnotationNames;
    }

    private boolean hasJsonbAnnotationOtherThanTransient(List<String> jsonbAnnotations) throws JavaModelException {
        for (String annotationName : jsonbAnnotations)
            if (JsonbConstants.JSONB_ANNOTATIONS.contains(annotationName)
                    && !annotationName.equals(JsonbConstants.JSONB_TRANSIENT_FQ_NAME))
                return true;
        return false;
    }
}