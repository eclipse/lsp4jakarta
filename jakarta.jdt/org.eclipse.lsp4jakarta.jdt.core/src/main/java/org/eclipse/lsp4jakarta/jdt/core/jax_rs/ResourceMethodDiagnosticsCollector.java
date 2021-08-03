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
 *     IBM Corporation, Matthew Shocrylas - initial API and implementation, Bera Sogut
 *******************************************************************************/

package org.jakarta.jdt.jax_rs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Range;
import org.jakarta.jdt.DiagnosticsCollector;
import org.jakarta.jdt.JDTUtils;
import org.jakarta.lsp4e.Activator;

public class ResourceMethodDiagnosticsCollector implements DiagnosticsCollector {

    public ResourceMethodDiagnosticsCollector() {
    }

    @Override
    public void completeDiagnostic(Diagnostic diagnostic) {
        diagnostic.setSource(Jax_RSConstants.DIAGNOSTIC_SOURCE);
        diagnostic.setSeverity(DiagnosticSeverity.Error);
    }

    @Override
    public void collectDiagnostics(ICompilationUnit unit, List<Diagnostic> diagnostics) {
        Diagnostic diagnostic;
        ArrayList<String> methodDesignators = Jax_RSConstants.METHOD_DESIGNATORS;

        String pathAnnotation = Jax_RSConstants.PATH_ANNOTATION;

        if (unit != null) {
            IType[] alltypes;

            try {
                alltypes = unit.getAllTypes();
                for (IType type : alltypes) {

                    ISourceRange nameRange = JDTUtils.getNameRange(type);
                    Range range = JDTUtils.toRange(unit, nameRange.getOffset(), nameRange.getLength());

                    for (IMethod method : type.getMethods()) {
                        IAnnotation[] methodAnnotations = method.getAnnotations();

                        ISourceRange methodNameRange = JDTUtils.getNameRange(method);
                        Range methodRange = JDTUtils.toRange(unit, methodNameRange.getOffset(),
                                methodNameRange.getLength());

                        boolean isResourceMethod = false;
                        boolean isAnnotatedWithPath = false;
                        boolean isPublic = Flags.isPublic(method.getFlags());

                        for (IAnnotation annotation : methodAnnotations) {
                            if (!isResourceMethod && methodDesignators.contains(annotation.getElementName())) {
                                isResourceMethod = true;
                            }
                            if (!isAnnotatedWithPath && annotation.getElementName().equals(pathAnnotation)) {
                                isAnnotatedWithPath = true;
                            }
                        }

                        if ((isResourceMethod || isAnnotatedWithPath) && !isPublic) {
                            diagnostic = new Diagnostic(methodRange,
                                    "Only public methods may be exposed as resource methods");
                            diagnostic.setCode(Jax_RSConstants.DIAGNOSTIC_CODE_NON_PUBLIC);
                            completeDiagnostic(diagnostic);
                            diagnostics.add(diagnostic);
                        }
                        if (isResourceMethod) {
                            int numEntityParams = 0;
                            ArrayList<String> nonEntityParamAnnotations = Jax_RSConstants.NON_ENTITY_PARAM_ANNOTATIONS;

                            for (ILocalVariable param : method.getParameters()) {
                                boolean isEntityParam = true;
                                for (IAnnotation annotation : param.getAnnotations()) {
                                    if (nonEntityParamAnnotations.contains(annotation.getElementName())) {
                                        isEntityParam = false;
                                        break;
                                    }
                                }
                                if (isEntityParam)
                                    numEntityParams++;
                            }

                            if (numEntityParams > 1) {
                                diagnostic = new Diagnostic(methodRange,
                                        "Resource methods cannot have more than one entity parameter");
                                diagnostic.setCode(Jax_RSConstants.DIAGNOSTIC_CODE_MULTIPLE_ENTITY_PARAMS);
                                completeDiagnostic(diagnostic);
                                diagnostics.add(diagnostic);
                            }
                        }
                    }
                }

            } catch (JavaModelException e) {
                Activator.logException("Cannot calculate JAX-RS diagnostics", e);
            }
        }

    }

}
