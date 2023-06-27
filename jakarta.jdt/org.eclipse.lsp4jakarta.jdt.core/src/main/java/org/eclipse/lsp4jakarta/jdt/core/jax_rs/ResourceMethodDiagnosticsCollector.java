/*******************************************************************************
 * Copyright (c) 2021, 2022 IBM Corporation, Matthew Shocrylas and others.
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

package org.eclipse.lsp4jakarta.jdt.core.jax_rs;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4jakarta.jdt.core.AbstractDiagnosticsCollector;
import org.eclipse.lsp4jakarta.jdt.core.JakartaCorePlugin;
import org.eclipse.lsp4jakarta.jdt.core.Messages;

public class ResourceMethodDiagnosticsCollector extends AbstractDiagnosticsCollector {

    public ResourceMethodDiagnosticsCollector() {
        super();
    }

    @Override
    protected String getDiagnosticSource() {
        return Jax_RSConstants.DIAGNOSTIC_SOURCE;
    }

    @Override
    public void collectDiagnostics(ICompilationUnit unit, List<Diagnostic> diagnostics) {

        if (unit != null) {
            String[] methodDesignators = ArrayUtils.addAll(Jax_RSConstants.SET_OF_METHOD_DESIGNATORS_ANNOTATIONS,
                    Jax_RSConstants.PATH_ANNOTATION);
            IType[] alltypes;
            IMethod[] methods;

            try {
                alltypes = unit.getAllTypes();
                for (IType type : alltypes) {
                    methods = type.getMethods();
                    for (IMethod method : methods) {
                        IAnnotation[] methodAnnotations = method.getAnnotations();
                        boolean isResourceMethod = false;
                        boolean isValid = true;
                        boolean isPublic = Flags.isPublic(method.getFlags());

                        for (IAnnotation annotation : methodAnnotations) {
                            String matchedAnnotation = getMatchedJavaElementName(type, annotation.getElementName(),
                                    methodDesignators);
                            if (matchedAnnotation != null) {
                                if (isValid && !isPublic)
                                    isValid = false;
                                if (!Jax_RSConstants.PATH_ANNOTATION.equals(matchedAnnotation)) {
                                    isResourceMethod = true;
                                    break;
                                }
                            }
                        }
                        if (!isValid) {
                            diagnostics.add(createDiagnostic(method, unit,
                                    Messages.getMessage("OnlyPublicMethods"),
                                    Jax_RSConstants.DIAGNOSTIC_CODE_NON_PUBLIC, null, DiagnosticSeverity.Error));
                        }
                        if (isResourceMethod) {
                            int numEntityParams = 0;
                            ILocalVariable[] parameters = method.getParameters();
                            for (ILocalVariable param : parameters) {
                                boolean isEntityParam = true;
                                IAnnotation[] annotations = param.getAnnotations();
                                for (IAnnotation annotation : annotations) {
                                    String matchedAnnotation = getMatchedJavaElementName(type,
                                            annotation.getElementName(),
                                            Jax_RSConstants.SET_OF_NON_ENTITY_PARAM_ANNOTATIONS);
                                    if (matchedAnnotation != null) {
                                        isEntityParam = false;
                                        break;
                                    }
                                }
                                if (isEntityParam)
                                    numEntityParams++;
                            }
                            if (numEntityParams > 1) {
                                diagnostics.add(createDiagnostic(method, unit,
                                        Messages.getMessage("ResourceMethodsEntityParameter"),
                                        Jax_RSConstants.DIAGNOSTIC_CODE_MULTIPLE_ENTITY_PARAMS, null,
                                        DiagnosticSeverity.Error));
                            }
                        }
                    }
                }
            } catch (JavaModelException e) {
                JakartaCorePlugin.logException("Cannot calculate JAX-RS diagnostics", e);
            }
        }
    }
}
