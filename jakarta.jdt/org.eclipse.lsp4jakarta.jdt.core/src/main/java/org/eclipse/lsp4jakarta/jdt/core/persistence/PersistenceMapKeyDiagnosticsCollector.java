/*******************************************************************************
* Copyright (c) 2020, 2022 IBM Corporation, Ankush Sharma and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0.
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     IBM Corporation, Ankush Sharma - initial API and implementation
*******************************************************************************/

package org.eclipse.lsp4jakarta.jdt.core.persistence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4jakarta.jdt.core.AbstractDiagnosticsCollector;
import org.eclipse.lsp4jakarta.jdt.core.JakartaCorePlugin;
import org.eclipse.lsp4jakarta.jdt.core.Messages;

public class PersistenceMapKeyDiagnosticsCollector extends AbstractDiagnosticsCollector {

    public PersistenceMapKeyDiagnosticsCollector() {
        super();
    }

    @Override
    protected String getDiagnosticSource() {
        return PersistenceConstants.DIAGNOSTIC_SOURCE;
    }

    @Override
    public void collectDiagnostics(ICompilationUnit unit, List<Diagnostic> diagnostics) {
        if (unit != null) {
            IType[] alltypes;
            IAnnotation[] allAnnotations;

            try {
                alltypes = unit.getAllTypes();
                IMethod[] methods;
                IField[] fields;

                for (IType type : alltypes) {
                    methods = type.getMethods();
                    for (IMethod method : methods) {
                        List<IAnnotation> mapKeyJoinCols = new ArrayList<IAnnotation>();
                        boolean hasMapKeyAnnotation = false;
                        boolean hasMapKeyClassAnnotation = false;
                        allAnnotations = method.getAnnotations();
                        for (IAnnotation annotation : allAnnotations) {
                            String matchedAnnotation = getMatchedJavaElementName(type, annotation.getElementName(),
                                    PersistenceConstants.SET_OF_PERSISTENCE_ANNOTATIONS);
                            if (matchedAnnotation != null) {
                                if (PersistenceConstants.MAPKEY.equals(matchedAnnotation))
                                    hasMapKeyAnnotation = true;
                                else if (PersistenceConstants.MAPKEYCLASS.equals(matchedAnnotation))
                                    hasMapKeyClassAnnotation = true;
                                else if (PersistenceConstants.MAPKEYJOINCOLUMN.equals(matchedAnnotation)) {
                                    mapKeyJoinCols.add(annotation);
                                }
                            }
                        }
                        if (hasMapKeyAnnotation && hasMapKeyClassAnnotation) {
                            // A single field cannot have the same
                            diagnostics.add(createDiagnostic(method, unit,
                            		Messages.getMessage("MapKeyAnnotationsNotOnSameField"),
                                    PersistenceConstants.DIAGNOSTIC_CODE_INVALID_ANNOTATION, null,
                                    DiagnosticSeverity.Error));
                        }
                        // If we have multiple MapKeyJoinColumn annotations on a single method we must
                        // ensure each has a name and referencedColumnName
                        if (mapKeyJoinCols.size() > 1) {
                            validateMapKeyJoinColumnAnnotations(mapKeyJoinCols, method, unit, diagnostics);
                        }
                    }

                    // Go through each field to ensure they do not have both MapKey and MapKeyColumn
                    // Annotations
                    fields = type.getFields();
                    for (IField field : fields) {
                        List<IAnnotation> mapKeyJoinCols = new ArrayList<IAnnotation>();
                        boolean hasMapKeyAnnotation = false;
                        boolean hasMapKeyClassAnnotation = false;
                        allAnnotations = field.getAnnotations();
                        for (IAnnotation annotation : allAnnotations) {
                            String matchedAnnotation = getMatchedJavaElementName(type, annotation.getElementName(),
                                    PersistenceConstants.SET_OF_PERSISTENCE_ANNOTATIONS);
                            if (matchedAnnotation != null) {
                                if (PersistenceConstants.MAPKEY.equals(matchedAnnotation))
                                    hasMapKeyAnnotation = true;
                                else if (PersistenceConstants.MAPKEYCLASS.equals(matchedAnnotation))
                                    hasMapKeyClassAnnotation = true;
                                else if (PersistenceConstants.MAPKEYJOINCOLUMN.equals(matchedAnnotation)) {
                                    mapKeyJoinCols.add(annotation);
                                }
                            }
                        }
                        if (hasMapKeyAnnotation && hasMapKeyClassAnnotation) {
                            // A single field cannot have the same
                            diagnostics.add(createDiagnostic(field, unit,
                            		Messages.getMessage("MapKeyAnnotationsNotOnSameField"),
                                    PersistenceConstants.DIAGNOSTIC_CODE_INVALID_ANNOTATION, null,
                                    DiagnosticSeverity.Error));
                        }
                        if (mapKeyJoinCols.size() > 1) {
                            validateMapKeyJoinColumnAnnotations(mapKeyJoinCols, field, unit, diagnostics);
                        }
                    }
                }
            } catch (JavaModelException e) {
                JakartaCorePlugin.logException("Cannot calculate diagnostics", e);
            }
        }
    }

    private void validateMapKeyJoinColumnAnnotations(List<IAnnotation> annotations, IMember element,
            ICompilationUnit unit, List<Diagnostic> diagnostics) {
        String message = (element instanceof IMethod) ? 
                Messages.getMessage("MultipleMapKeyJoinColumnMethod") : 
                Messages.getMessage("MultipleMapKeyJoinColumnField");
        annotations.forEach(annotation -> {
            boolean allNamesSpecified, allReferencedColumnNameSpecified;
            try {
                List<IMemberValuePair> memberValues = Arrays.asList(annotation.getMemberValuePairs());
                allNamesSpecified = memberValues.stream()
                        .anyMatch((mv) -> mv.getMemberName().equals(PersistenceConstants.NAME));
                allReferencedColumnNameSpecified = memberValues.stream()
                        .anyMatch((mv) -> mv.getMemberName().equals(PersistenceConstants.REFERENCEDCOLUMNNAME));
                if (!allNamesSpecified || !allReferencedColumnNameSpecified) {
                    diagnostics.add(createDiagnostic(element, unit, message,
                            PersistenceConstants.DIAGNOSTIC_CODE_MISSING_ATTRIBUTES, null, DiagnosticSeverity.Error));
                }
            } catch (JavaModelException e) {
                JakartaCorePlugin.logException("Error while retrieving member values of @MapKeyJoinColumn Annotation",
                        e);
            }
        });
    }
}
