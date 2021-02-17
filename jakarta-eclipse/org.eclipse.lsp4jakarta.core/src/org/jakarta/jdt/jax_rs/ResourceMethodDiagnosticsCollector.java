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
import java.util.List;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.lsp4j.Diagnostic;
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
        diagnostic.setSeverity(Jax_RSConstants.SEVERITY);
    }

    @Override
    public void collectDiagnostics(ICompilationUnit unit, List<Diagnostic> diagnostics) {
        Diagnostic diagnostic;
        ArrayList<String> methodDesignators = Jax_RSConstants.METHOD_DESIGNATORS;
        
        
        
        if (unit != null) {
            IType[] alltypes;

            try {
                alltypes = unit.getAllTypes();
                for(IType type : alltypes) {

                    ISourceRange nameRange = JDTUtils.getNameRange(type);
                    Range range = JDTUtils.toRange(unit,  nameRange.getOffset(), nameRange.getLength());


                    for (IMethod method : type.getMethods()) {
                        IAnnotation[] methodAnnotations = method.getAnnotations();

                        ISourceRange methodNameRange = JDTUtils.getNameRange(method);
                        Range methodRange = JDTUtils.toRange(unit, methodNameRange.getOffset(), methodNameRange.getLength());

                        boolean isResourceMethod = false;
                        boolean isPublic = Flags.isPublic(method.getFlags());

                        for (IAnnotation annotation : methodAnnotations ) {
                            if (methodDesignators.contains(annotation.getElementName())) {
                                isResourceMethod = true;
                                break;
                            }
                        }

                        if (isResourceMethod && !isPublic) { 
                            diagnostic = new Diagnostic(methodRange, 
                                    "Only public methods may be exposed as resource methods");
                            diagnostic.setCode(Jax_RSConstants.DIAGNOSTIC_CODE_NON_PUBLIC);
                            completeDiagnostic(diagnostic);
                            diagnostics.add(diagnostic);
                        }	
                    }
                }

            } catch (JavaModelException e) {
                Activator.logException("Cannot calculate persistence diagnostics", e);
            }
        }

    }

}
