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
*     IBM Corporation, Matheus Cruz - initial API and implementation
*******************************************************************************/

package org.eclipse.lsp4jakarta.jdt.core.jsonb;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.eclipse.jdt.core.Flags;
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
                boolean hasJsonbTransient = false, hasOtherJsonbAnnotation = false;
                ArrayList<String> jsonbAnnotationForTheField = new ArrayList<String>();

                for (IAnnotation annotation : field.getAnnotations()) {
                    String annotation_name = annotation.getElementName();
                    
                    if (annotation_name.equals(JsonbConstants.JSONB_TRANSIENT)) {
                        hasJsonbTransient = true;
                        jsonbAnnotationForTheField.add(annotation_name);
                    } else if (annotation_name.startsWith(JsonbConstants.JSONB_PREFIX)) {
                        hasOtherJsonbAnnotation = true;
                        jsonbAnnotationForTheField.add(annotation_name);
                    }
                }
                if (hasJsonbTransient && hasOtherJsonbAnnotation) {
                    Diagnostic diagnostic = createDiagnosticBy(unit, field, JsonbConstants.JSONB_TRANSIENT);
                    diagnostic.setData((JsonArray)(new Gson().toJsonTree(jsonbAnnotationForTheField)));
                    diagnostics.add(diagnostic);
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
}
