
/*******************************************************************************
* Copyright (c) 2021 IBM Corporation and others.
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

import static org.eclipse.lsp4jakarta.jdt.core.di.DependencyInjectionConstants.DIAGNOSTIC_SOURCE;
import static org.eclipse.lsp4jakarta.jdt.core.di.DependencyInjectionConstants.SEVERITY;
import static org.eclipse.lsp4jakarta.jdt.core.di.DependencyInjectionConstants.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4jakarta.jdt.core.DiagnosticsCollector;
import org.eclipse.lsp4jakarta.jdt.core.JDTUtils;
import org.eclipse.lsp4jakarta.jdt.core.JakartaCorePlugin;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;

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
public class DependencyInjectionDiagnosticsCollector implements DiagnosticsCollector {

    private Diagnostic createDiagnostic(IJavaElement el, ICompilationUnit unit, String msg, String code) {
        try {
            ISourceRange nameRange = JDTUtils.getNameRange(el);
            Range range = JDTUtils.toRange(unit, nameRange.getOffset(), nameRange.getLength());
            Diagnostic diagnostic = new Diagnostic(range, msg);
            diagnostic.setCode(code);
            completeDiagnostic(diagnostic);
            return diagnostic;
        } catch (JavaModelException e) {
            JakartaCorePlugin.logException("Cannot calculate diagnostics", e);
        }
        return null;
    }

    @Override
    public void completeDiagnostic(Diagnostic diagnostic) {
        diagnostic.setSource(DIAGNOSTIC_SOURCE);
        diagnostic.setSeverity(SEVERITY);
    }

    // checks if a method is a constructor
    private boolean isConstructorMethod(IMethod m) {
        try {
            return m.isConstructor();
        } catch (JavaModelException e) {
            return false;
        }
    }

    @Override
    public void collectDiagnostics(ICompilationUnit unit, List<Diagnostic> diagnostics) {
        if (unit == null)
            return;

        Diagnostic diagnostic;
        IType[] alltypes;
        IAnnotation[] allAnnotations;
        try {
            alltypes = unit.getAllTypes();
            for (IType type : alltypes) {
                allAnnotations = type.getAnnotations();

                IField[] allFields = type.getFields();
                for (IField field : allFields) {
                    int fieldFlags = field.getFlags();
                    List<IAnnotation> fieldAnnotations = Arrays.asList(field.getAnnotations());

                    boolean isInjectField = fieldAnnotations.stream().anyMatch(
                            annotation -> annotation.getElementName().equals(DependencyInjectionConstants.INJECT));

                    boolean isFinal = Flags.isFinal(fieldFlags);

                    if (isFinal && isInjectField) {
                        String msg = "Injectable fields cannot be final";
                        diagnostic = createDiagnostic(field, unit, msg,
                                DependencyInjectionConstants.DIAGNOSTIC_CODE_INJECT_FINAL);
                        diagnostic.setData(field.getElementType());
                        diagnostics.add(diagnostic);
                    }
                }

                IMethod[] allMethods = type.getMethods();
                for (IMethod method : allMethods) {
                    List<IAnnotation> methodAnnotations = Arrays.asList(method.getAnnotations());

                    boolean isInjectMethod = methodAnnotations.stream().anyMatch(
                            annotation -> annotation.getElementName().equals(DependencyInjectionConstants.INJECT));

                    int methodFlag = method.getFlags();
                    boolean isFinal = Flags.isFinal(methodFlag);
                    boolean isAbstract = Flags.isAbstract(methodFlag);
                    boolean isStatic = Flags.isStatic(methodFlag);
                    boolean isGeneric = method.getTypeParameters().length != 0;

                    if (isFinal && isInjectMethod) {
                        String msg = "Injectable methods cannot be final";
                        diagnostic = createDiagnostic(method, unit, msg,
                                DependencyInjectionConstants.DIAGNOSTIC_CODE_INJECT_FINAL);
                        diagnostic.setData(method.getElementType());
                        diagnostics.add(diagnostic);
                    }

                    if (isAbstract && isInjectMethod) {
                        String msg = "Injectable methods cannot be abstract";
                        diagnostic = createDiagnostic(method, unit, msg,
                                DependencyInjectionConstants.DIAGNOSTIC_CODE_INJECT_ABSTRACT);
                        diagnostic.setData(method.getElementType());
                        diagnostics.add(diagnostic);
                    }

                    if (isStatic && isInjectMethod) {
                        String msg = "Injectable methods cannot be static";
                        diagnostic = createDiagnostic(method, unit, msg,
                                DependencyInjectionConstants.DIAGNOSTIC_CODE_INJECT_STATIC);
                        diagnostic.setData(method.getElementType());
                        diagnostics.add(diagnostic);
                    }

                    if (isGeneric && isInjectMethod) {
                        String msg = "Injectable methods cannot be generic";
                        diagnostic = createDiagnostic(method, unit, msg,
                                DependencyInjectionConstants.DIAGNOSTIC_CODE_INJECT_GENERIC);
                        diagnostic.setData(method.getElementType());
                        diagnostics.add(diagnostic);
                    }
                }
            }

            for (IType type : alltypes) {
                List<IMethod> constructorMethods = Arrays.stream(type.getMethods()).filter(this::isConstructorMethod)
                        .collect(Collectors.toList());

                // there are no constructors
                if (constructorMethods.size() == 0)
                    return;
                boolean hasInjectConstructor = false;
                boolean multipleInjectConstructor = false;
                int numInjectedConstructors = 0;
                List<IMethod> injectedConstructors = new ArrayList<IMethod>();
                for (IMethod m : constructorMethods) {
                    hasInjectConstructor = Arrays.stream(m.getAnnotations())
                            .map(annotation -> annotation.getElementName())
                            .anyMatch(annotation -> annotation.equals("Inject"));
                    if (hasInjectConstructor) {
                        injectedConstructors.add(m);
                        numInjectedConstructors++;
                        if (numInjectedConstructors > 1) {
                            multipleInjectConstructor = true; // if more than one constructor, add diagnostic to all
                                                              // constructors
                        }
                    }
                }

                if (multipleInjectConstructor) {
                    for (IMethod m : injectedConstructors) {
                        diagnostics.add(createDiagnostic(m, unit, "Inject cannot be used with multiple constructors",
                                DIAGNOSTIC_CODE_INJECT_CONSTRUCTOR));
                    }
                }
            }
        } catch (JavaModelException e) {
            JakartaCorePlugin.logException("Cannot calculate diagnostics", e);
        }
    }
}
