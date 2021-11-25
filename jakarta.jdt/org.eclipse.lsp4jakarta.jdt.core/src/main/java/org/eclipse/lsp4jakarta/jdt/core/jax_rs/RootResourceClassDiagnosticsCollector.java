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
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4jakarta.jdt.core.DiagnosticsCollector;
import org.eclipse.lsp4jakarta.jdt.core.JDTUtils;
import org.eclipse.lsp4jakarta.jdt.core.JakartaCorePlugin;

/**
 * Diagnostic collector for root resource classes with multiple constructors
 * 
 * @author Matthew Shocrylas
 *
 */
public class RootResourceClassDiagnosticsCollector implements DiagnosticsCollector {

    @Override
    public void completeDiagnostic(Diagnostic diagnostic) {
        diagnostic.setSource(Jax_RSConstants.DIAGNOSTIC_SOURCE);
        diagnostic.setSeverity(DiagnosticSeverity.Warning);
    }

    @Override
    public void collectDiagnostics(ICompilationUnit unit, List<Diagnostic> diagnostics) {
        Diagnostic diagnostic;

        if (unit != null) {
            IType[] alltypes;

            try {
                alltypes = unit.getAllTypes();
                for (IType type : alltypes) {
                    boolean isRootResource = false;
                    
                    IAnnotation[] annotationList = type.getAnnotations();

                    for (IAnnotation annotation : annotationList) {
                        if (annotation.getElementName().startsWith("Path")) {
                            isRootResource = true;
                        }
                    }
                    
                    if (isRootResource) {

                        int maxParams = 0;
                        String className = type.getElementName();
                        Map<IMethod, Integer> constructorParamsMap = new HashMap<IMethod, Integer>();
                        boolean hasPublicConstructor = false;
                        boolean hasPrivateOrProtectedConstructor = false;
                        for (IMethod method : type.getMethods()) {
                            // if a method of a class has the same name as the class, it is a constructor
                            if ((method.getElementName().equals(className)) && (Flags.isPublic(method.getFlags()))) {
                                int numParams = method.getNumberOfParameters();
                                hasPublicConstructor = true;
                                
                                if (numParams > maxParams) {
                                    maxParams = numParams;
                                }
                                constructorParamsMap.put(method, numParams);
                            }  
                            if ((method.getElementName().equals(className)) && (Flags.isPrivate(method.getFlags()) || Flags.isProtected(method.getFlags()))) {
                            	hasPrivateOrProtectedConstructor = true;
                            }
                            if (method.getElementName().equals(className) && hasPrivateOrProtectedConstructor && !hasPublicConstructor) {
                                ISourceRange methodNameRange = JDTUtils.getNameRange(method); 
                                Range methodRange = JDTUtils.toRange(unit, methodNameRange.getOffset(), methodNameRange.getLength());
                                
                                diagnostic = new Diagnostic(methodRange, "Root resource classes are instantiated by the JAX-RS runtime and MUST have a public constructor");
                                diagnostic.setCode(Jax_RSConstants.DIAGNOSTIC_CODE_NO_PUBLIC_CONSTRUCTORS);
                                completeDiagnostic(diagnostic);
                                diagnostic.setSeverity(DiagnosticSeverity.Error);
                                diagnostics.add(diagnostic);
                            }
                        }
                        
                        ArrayList<IMethod> equalMaxParamMethods = new ArrayList<IMethod>();
                        
                        for (Map.Entry<IMethod, Integer> entry : constructorParamsMap.entrySet()) {
                            if (entry.getValue() == maxParams) {
                                equalMaxParamMethods.add(entry.getKey());
                            }
                            else if (entry.getValue() < maxParams) {
                                IMethod method = entry.getKey();
                                ISourceRange methodNameRange = JDTUtils.getNameRange(method); // TODO: maybe change the area this diagnostic underlines
                                Range methodRange = JDTUtils.toRange(unit, methodNameRange.getOffset(), methodNameRange.getLength());
                                
                                diagnostic = new Diagnostic(methodRange, "This constructor is unused, as root resource classes will only use the constructor with the most parameters.");
                                diagnostic.setCode(Jax_RSConstants.DIAGNOSTIC_CODE_UNUSED_CONSTRUCTOR);
                                completeDiagnostic(diagnostic);
                                diagnostics.add(diagnostic);
                            }
                            
                        }
                        if (equalMaxParamMethods.size() > 1) {
                            for (IMethod method : equalMaxParamMethods) {
                                ISourceRange methodNameRange = JDTUtils.getNameRange(method); // TODO: maybe change the area this diagnostic underlines
                                Range methodRange = JDTUtils.toRange(unit, methodNameRange.getOffset(), methodNameRange.getLength());
                                
                                diagnostic = new Diagnostic(methodRange, "Multiple constructors have the same number of parameters, it may be ambiguous which constructor is used.");
                                diagnostic.setCode(Jax_RSConstants.DIAGNOSTIC_CODE_AMBIGUOUS_CONSTRUCTORS);
                                completeDiagnostic(diagnostic);
                                diagnostics.add(diagnostic);
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
