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
 *     IBM Corporation, Matthew Shocrylas - initial API and implementation
 *******************************************************************************/

package org.eclipse.lsp4jakarta.jdt.core.jax_rs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4jakarta.jdt.core.AbstractDiagnosticsCollector;
import org.eclipse.lsp4jakarta.jdt.core.JakartaCorePlugin;

/**
 * Diagnostic collector for root resource classes with multiple constructors
 * 
 * @author Matthew Shocrylas
 *
 */
public class Jax_RSClassDiagnosticsCollector extends AbstractDiagnosticsCollector {

    public Jax_RSClassDiagnosticsCollector() {
        super();
    }

    @Override
    protected String getDiagnosticSource() {
        return Jax_RSConstants.DIAGNOSTIC_SOURCE;
    }

    @Override
    public void collectDiagnostics(ICompilationUnit unit, List<Diagnostic> diagnostics) {

        if (unit != null) {
            IType[] alltypes;

            try {
                alltypes = unit.getAllTypes();
                for (IType type : alltypes) {
                    boolean isRootResource = false;
                    boolean isProviderResource = false;
                    IAnnotation[] annotationList = type.getAnnotations();

                    for (IAnnotation annotation : annotationList) {
                        String matchedAnnotation = getMatchedJavaElementName(type, annotation.getElementName(),
                                Jax_RSConstants.SET_OF_JAXRS_ANNOTATIONS1);
                        if (matchedAnnotation != null) {
                            if (Jax_RSConstants.PATH_ANNOTATION.equals(matchedAnnotation)) {
                                isRootResource = true;
                            } else if (Jax_RSConstants.PROVIDER_ANNOTATION.equals(matchedAnnotation)) {
                                isProviderResource = true;
                            }
                        }
                    }

                    if (isRootResource || isProviderResource) { // annotated class
                        List<IMethod> nonPublicConstructors = new ArrayList<IMethod>();
                        boolean hasPublicConstructor = false;
                        int maxParams = 0;
                        Map<IMethod, Integer> constructorParamsMap = new HashMap<IMethod, Integer>();
                        IMethod[] methods = type.getMethods();
                        for (IMethod method : methods) {
                            if (isConstructorMethod(method)) {
                                if (Flags.isPublic(method.getFlags())) {
                                    hasPublicConstructor = true;
                                    nonPublicConstructors.clear(); // ignore all non-public constructors
                                    if (isRootResource) {
                                        int numParams = method.getNumberOfParameters();
                                        if (numParams > maxParams) {
                                            maxParams = numParams;
                                        }
                                        constructorParamsMap.put(method, numParams);
                                    }
                                } else if (!hasPublicConstructor) {
                                    nonPublicConstructors.add(method);
                                }
                            }
                        }
                        // no public constructor defined
                        if (nonPublicConstructors.size() > 0) {
                            String diagnosticMessage = isRootResource
                                    ? "Root resource classes are instantiated by the JAX-RS runtime and MUST have a public constructor"
                                    : "Provider classes are instantiated by the JAX-RS runtime and MUST have a public constructor";
                            for (IMethod constructor : nonPublicConstructors) {
                                diagnostics.add(createDiagnostic(constructor, unit, diagnosticMessage,
                                        Jax_RSConstants.DIAGNOSTIC_CODE_NO_PUBLIC_CONSTRUCTORS, null,
                                        DiagnosticSeverity.Error));
                            }
                        }
                        // check public constructors' parameters
                        ArrayList<IMethod> equalMaxParamMethods = new ArrayList<IMethod>();
                        for (Map.Entry<IMethod, Integer> entry : constructorParamsMap.entrySet()) {
                            if (entry.getValue() == maxParams) {
                                equalMaxParamMethods.add(entry.getKey());
                            } else if (entry.getValue() < maxParams) {
                                IMethod method = entry.getKey();
                                diagnostics.add(createDiagnostic(method, unit,
                                        "This constructor is unused, as root resource classes will only use the constructor with the most parameters.",
                                        Jax_RSConstants.DIAGNOSTIC_CODE_UNUSED_CONSTRUCTOR, null,
                                        DiagnosticSeverity.Warning));
                            }
                        }
                        if (equalMaxParamMethods.size() > 1) { // more than one
                            for (IMethod method : equalMaxParamMethods) {
                                diagnostics.add(createDiagnostic(method, unit,
                                        "Multiple constructors have the same number of parameters, it may be ambiguous which constructor is used.",
                                        Jax_RSConstants.DIAGNOSTIC_CODE_AMBIGUOUS_CONSTRUCTORS, null,
                                        DiagnosticSeverity.Warning));
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
