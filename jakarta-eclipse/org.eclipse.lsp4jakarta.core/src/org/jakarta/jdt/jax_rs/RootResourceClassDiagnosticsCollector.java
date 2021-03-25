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

package org.jakarta.jdt.jax_rs;

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
import org.eclipse.lsp4j.Range;
import org.jakarta.jdt.DiagnosticsCollector;
import org.jakarta.jdt.JDTUtils;
import org.jakarta.lsp4e.Activator;

/**
 * 
 * @author Matthew Shocrylas
 *
 */
public class RootResourceClassDiagnosticsCollector implements DiagnosticsCollector {

    @Override
    public void completeDiagnostic(Diagnostic diagnostic) {
        diagnostic.setSource(Jax_RSConstants.DIAGNOSTIC_SOURCE);
        diagnostic.setSeverity(Jax_RSConstants.SEVERITY_WARNING);
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
                        
                        for (IMethod method : type.getMethods()) {
                            // if a method of a class has the same name as the class, it is a constructor
                            if ((method.getElementName().equals(className)) && (Flags.isPublic(method.getFlags()))) {
                                int numParams = method.getNumberOfParameters();
                                
                                if (numParams > maxParams) {
                                    maxParams = numParams;
                                }
                                constructorParamsMap.put(method, numParams);
                            }
                        }
                        
                        for (Map.Entry<IMethod, Integer> entry : constructorParamsMap.entrySet()) {
                            if (entry.getValue() < maxParams) {
                                IMethod method = entry.getKey();
                                ISourceRange methodNameRange = JDTUtils.getNameRange(method); // TODO: maybe change the area this diagnostic underlines
                                Range methodRange = JDTUtils.toRange(unit, methodNameRange.getOffset(), methodNameRange.getLength());
                                
                                diagnostic = new Diagnostic(methodRange, "This constructor is unused, as root resource classes will only use the constructor with the most parameters.");
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
