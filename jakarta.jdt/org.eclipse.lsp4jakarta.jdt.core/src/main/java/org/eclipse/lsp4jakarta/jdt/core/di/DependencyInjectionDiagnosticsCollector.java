/*******************************************************************************
* Copyright (c) 2021, 2022 IBM Corporation and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     IBM Corporation, Himanshu Chotwani - initial API and implementation
*     Ananya Rao - Diagnostic Collection for multiple constructors annotated with inject
*******************************************************************************/

package org.eclipse.lsp4jakarta.jdt.core.di;

import static org.eclipse.lsp4jakarta.jdt.core.di.DependencyInjectionConstants.DIAGNOSTIC_CODE_INJECT_ABSTRACT;
import static org.eclipse.lsp4jakarta.jdt.core.di.DependencyInjectionConstants.DIAGNOSTIC_CODE_INJECT_CONSTRUCTOR;
import static org.eclipse.lsp4jakarta.jdt.core.di.DependencyInjectionConstants.DIAGNOSTIC_CODE_INJECT_FINAL;
import static org.eclipse.lsp4jakarta.jdt.core.di.DependencyInjectionConstants.DIAGNOSTIC_CODE_INJECT_GENERIC;
import static org.eclipse.lsp4jakarta.jdt.core.di.DependencyInjectionConstants.DIAGNOSTIC_CODE_INJECT_STATIC;
import static org.eclipse.lsp4jakarta.jdt.core.di.DependencyInjectionConstants.DIAGNOSTIC_SOURCE;
import static org.eclipse.lsp4jakarta.jdt.core.di.DependencyInjectionConstants.INJECT;
import static org.eclipse.lsp4jakarta.jdt.core.di.DependencyInjectionConstants.INJECT_FQ_NAME;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4jakarta.jdt.core.AbstractDiagnosticsCollector;
import org.eclipse.lsp4jakarta.jdt.core.JakartaCorePlugin;
import org.eclipse.lsp4jakarta.jdt.core.Messages;

/**
 * 
 * jararta.annotation Diagnostics
 * 
 * <li>Diagnostic 1: @Inject fields cannot be final.</li>
 * <li>Diagnostic 2: @Inject methods cannot be final.</li>
 * <li>Diagnostic 3: @Inject methods cannot be abstract.</li>
 * <li>Diagnostic 4: @Inject methods cannot be static.</li>
 * <li>Diagnostic 5: @Inject methods cannot be generic.</li>
 * 
 * @see https://jakarta.ee/specifications/dependency-injection/2.0/jakarta-injection-spec-2.0.html
 *
 */

public class DependencyInjectionDiagnosticsCollector extends AbstractDiagnosticsCollector {
    
    public DependencyInjectionDiagnosticsCollector() {
        super();
    }
    
    @Override
    protected String getDiagnosticSource() {
        return DIAGNOSTIC_SOURCE;
    }

    @Override
    public void collectDiagnostics(ICompilationUnit unit, List<Diagnostic> diagnostics) {
        if (unit == null)
            return;

        IType[] alltypes;
        try {
            alltypes = unit.getAllTypes();
            for (IType type : alltypes) {
                IField[] allFields = type.getFields();
                for (IField field : allFields) {
                    if (Flags.isFinal(field.getFlags())
                            && containsAnnotation(type, field.getAnnotations(), INJECT_FQ_NAME)) {
                        String msg = Messages.getMessage("InjectNoFinalField");
                        diagnostics.add(createDiagnostic(field, unit, msg,
                                DIAGNOSTIC_CODE_INJECT_FINAL, field.getElementType(),
                                DiagnosticSeverity.Error));
                    }
                }

                List<IMethod> injectedConstructors = new ArrayList<IMethod>();
                IMethod[] allMethods = type.getMethods();
                for (IMethod method : allMethods) {
                    int methodFlag = method.getFlags();
                    boolean isFinal = Flags.isFinal(methodFlag);
                    boolean isAbstract = Flags.isAbstract(methodFlag);
                    boolean isStatic = Flags.isStatic(methodFlag);
                    boolean isGeneric = method.getTypeParameters().length != 0;

                    if (containsAnnotation(type, method.getAnnotations(), INJECT_FQ_NAME)) {
                        if (isConstructorMethod(method))
                            injectedConstructors.add(method);
                        if (isFinal) {
                            String msg = Messages.getMessage("InjectNoFinalMethod");
                            diagnostics.add(createDiagnostic(method, unit, msg,
                                    DIAGNOSTIC_CODE_INJECT_FINAL, method.getElementType(),
                                    DiagnosticSeverity.Error));
                        }
                        if (isAbstract) {
                            String msg = Messages.getMessage("InjectNoAbstractMethod");
                            diagnostics.add(createDiagnostic(method, unit, msg,
                                    DIAGNOSTIC_CODE_INJECT_ABSTRACT, method.getElementType(),
                                    DiagnosticSeverity.Error));
                        }
                        if (isStatic) {
                            String msg = Messages.getMessage("InjectNoStaticMethod");
                            diagnostics.add(createDiagnostic(method, unit, msg,
                                    DIAGNOSTIC_CODE_INJECT_STATIC, method.getElementType(),
                                    DiagnosticSeverity.Error));
                        }
    
                        if (isGeneric) {
                            String msg = Messages.getMessage("InjectNoGenericMethod");
                            diagnostics.add(createDiagnostic(method, unit, msg,
                                    DIAGNOSTIC_CODE_INJECT_GENERIC, method.getElementType(),
                                    DiagnosticSeverity.Error));
                        }
                    }
                }
                
                // if more than one 'inject' constructor, add diagnostic to all constructors
                if (injectedConstructors.size() > 1) {
                    String msg = Messages.getMessage("InjectMoreThanOneConstructor");
                    for (IMethod m : injectedConstructors) {
                        diagnostics.add(createDiagnostic(m, unit,msg,
                                DIAGNOSTIC_CODE_INJECT_CONSTRUCTOR, null, DiagnosticSeverity.Error));
                    }
                }
            }
        } catch (JavaModelException e) {
            JakartaCorePlugin.logException("Cannot calculate diagnostics", e);
        }
    }

    private boolean containsAnnotation(IType type, IAnnotation[] annotations, String annotationFQName) {
        return Stream.of(annotations).anyMatch(annotation -> {
            try {
                return isMatchedJavaElement(type, annotation.getElementName(), annotationFQName);
            } catch (JavaModelException e) {
                JakartaCorePlugin.logException("Cannot validate annotations", e);
                return false;
            }
        });
    }
}

