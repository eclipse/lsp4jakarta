/*******************************************************************************
* Copyright (c) 2021, 2023 IBM Corporation and others.
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

package org.eclipse.lsp4jakarta.jdt.internal.jaxrs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4jakarta.jdt.core.java.diagnostics.IJavaDiagnosticsParticipant;
import org.eclipse.lsp4jakarta.jdt.core.java.diagnostics.JavaDiagnosticsContext;
import org.eclipse.lsp4jakarta.jdt.core.utils.PositionUtils;
import org.eclipse.lsp4jakarta.jdt.internal.DiagnosticUtils;
import org.eclipse.lsp4jakarta.jdt.internal.Messages;

/**
 * JAX-RS diagnostic participant that manages the use of constructors.
 */
public class ClassConstructorDiagnosticsParticipant implements IJavaDiagnosticsParticipant {

    @Override
    public List<Diagnostic> collectDiagnostics(JavaDiagnosticsContext context, IProgressMonitor monitor) throws CoreException {
        ITypeRoot typeRoot = context.getTypeRoot();
        String uri = context.getUri();
        IJavaElement[] elements = typeRoot.getChildren();
        List<Diagnostic> diagnostics = new ArrayList<>();

        for (IJavaElement element : elements) {
            if (monitor.isCanceled()) {
                return null;
            }
            if (element.getElementType() == IJavaElement.TYPE) {
                IType type = (IType) element;
                if (!type.isClass()) {
                    continue;
                }
                boolean isRootResource = false;
                boolean isProviderResource = false;
                IAnnotation[] annotationList = type.getAnnotations();

                for (IAnnotation annotation : annotationList) {
                    String matchedAnnotation = DiagnosticUtils.getMatchedJavaElementName(type,
                                                                                         annotation.getElementName(),
                                                                                         Constants.SET_OF_JAXRS_ANNOTATIONS1);
                    if (matchedAnnotation != null) {
                        if (Constants.PATH_ANNOTATION.equals(matchedAnnotation)) {
                            isRootResource = true;
                        } else if (Constants.PROVIDER_ANNOTATION.equals(matchedAnnotation)) {
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
                        if (DiagnosticUtils.isConstructorMethod(method)) {
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
                        String diagnosticMessage = isRootResource ? Messages.getMessage("RootResourceClasses") : Messages.getMessage("ProviderClasses");
                        for (IMethod constructor : nonPublicConstructors) {
                            Range methodRange = PositionUtils.toNameRange(constructor, context.getUtils());
                            diagnostics.add(context.createDiagnostic(uri, diagnosticMessage, methodRange,
                                                                     Constants.DIAGNOSTIC_SOURCE, ErrorCode.NoPublicConstructors,
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
                            Range methodRange = PositionUtils.toNameRange(method, context.getUtils());
                            diagnostics.add(context.createDiagnostic(uri, Messages.getMessage("ConstructorIsUnused"),
                                                                     methodRange, Constants.DIAGNOSTIC_SOURCE, ErrorCode.UnusedConstructor,
                                                                     DiagnosticSeverity.Warning));
                        }
                    }
                    if (equalMaxParamMethods.size() > 1) { // more than one
                        for (IMethod method : equalMaxParamMethods) {
                            Range methodRange = PositionUtils.toNameRange(method, context.getUtils());
                            diagnostics.add(context.createDiagnostic(uri,
                                                                     Messages.getMessage("MultipleConstructorsNumberOfParameters"), methodRange,
                                                                     Constants.DIAGNOSTIC_SOURCE, ErrorCode.AmbiguousConstructors,
                                                                     DiagnosticSeverity.Warning));
                        }
                    }
                }
            }
        }

        return diagnostics;
    }
}
