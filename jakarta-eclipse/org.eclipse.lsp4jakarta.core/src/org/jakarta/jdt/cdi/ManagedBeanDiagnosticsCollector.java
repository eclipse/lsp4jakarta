/*******************************************************************************
 * Copyright (c) 2021 IBM Corporation.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Hani Damlaj, Jianing Xu
 *******************************************************************************/

package org.jakarta.jdt.cdi;

import java.util.List;
import java.util.Set;
import java.util.Arrays;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Range;
import org.jakarta.jdt.DiagnosticsCollector;
import org.jakarta.jdt.JDTUtils;
import org.jakarta.lsp4e.Activator;

import static org.jakarta.jdt.cdi.ManagedBeanConstants.*;
import static org.jakarta.jdt.cdi.Utils.getManagedBeanAnnotations;

public class ManagedBeanDiagnosticsCollector implements DiagnosticsCollector {

    private Diagnostic createDiagnostic(IJavaElement el, ICompilationUnit unit, String msg, String code) {
        try {
            ISourceRange nameRange = JDTUtils.getNameRange(el);
            Range range = JDTUtils.toRange(unit, nameRange.getOffset(), nameRange.getLength());
            Diagnostic diagnostic = new Diagnostic(range, msg);
            diagnostic.setCode(code);
            completeDiagnostic(diagnostic);
            return diagnostic;
        } catch (JavaModelException e) {
            Activator.logException("Cannot calculate diagnostics", e);
        }
        return null;
    }

    @Override
    public void completeDiagnostic(Diagnostic diagnostic) {
        diagnostic.setSource(DIAGNOSTIC_SOURCE);
        diagnostic.setSeverity(SEVERITY);
    }

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

        try {
            for (IType type : unit.getAllTypes()) {
                List<String> managedBeanAnnotations = getManagedBeanAnnotations(type);
                IAnnotation[] allAnnotations = type.getAnnotations();
                boolean isManagedBean = managedBeanAnnotations.size() > 0;

                ISourceRange nameRange = JDTUtils.getNameRange(type);
                Range range = JDTUtils.toRange(unit, nameRange.getOffset(), nameRange.getLength());

                for (IField field : type.getFields()) {
                    int fieldFlags = field.getFlags();

                    /**
                     * If a managed bean has a non-static public field, it must have
                     * scope @Dependent. If a managed bean with a non-static public field declares
                     * any scope other than @Dependent, the container automatically detects the
                     * problem and treats it as a definition error.
                     * 
                     * https://jakarta.ee/specifications/cdi/3.0/jakarta-cdi-spec-3.0.html#managed_beans
                     */
                    if (isManagedBean && Flags.isPublic(fieldFlags) && !Flags.isStatic(fieldFlags)
                            && managedBeanAnnotations.stream()
                                    .anyMatch(annotation -> !annotation.equals("Dependent"))) {
                        Diagnostic diagnostic = createDiagnostic(field, unit,
                                "A managed bean with a non-static public field must not declare any scope other than @Dependent",
                                DIAGNOSTIC_CODE);
                        diagnostics.add(diagnostic);
                    }
                }

                /* ========= Produces and Inject Annotations Checks ========= */
                /*
                 * go through each field and method to make sure @Produces and @Inject are not
                 * used together
                 * 
                 * see: 
                 * https://jakarta.ee/specifications/cdi/3.0/jakarta-cdi-spec-3.0.html#declaring_producer_field
                 * https://jakarta.ee/specifications/cdi/3.0/jakarta-cdi-spec-3.0.html#declaring_producer_method
                 * https://jakarta.ee/specifications/cdi/3.0/jakarta-cdi-spec-3.0.html#declaring_injected_field
                 * https://jakarta.ee/specifications/cdi/3.0/jakarta-cdi-spec-3.0.html#declaring_initializer
                 * 
                 */
                for (IMethod method : type.getMethods()) {
                    IAnnotation ProducesAnnotation = null;
                    IAnnotation InjectAnnotation = null;
                    for (IAnnotation annotation : method.getAnnotations()) {
                        if (annotation.getElementName().equals(ManagedBeanConstants.PRODUCES))
                            ProducesAnnotation = annotation;
                        if (annotation.getElementName().equals(ManagedBeanConstants.INJECT))
                            InjectAnnotation = annotation;
                    }
                    if (ProducesAnnotation != null && InjectAnnotation != null) {
                        // A single method cannot have the same
                        diagnostics.add(createDiagnostic(method, unit,
                                "@Produces and @Inject annotations cannot be used on the same field or property",
                                ManagedBeanConstants.DIAGNOSTIC_CODE_PRODUCES_INJECT));
                    }
                }

                for (IField field : type.getFields()) {
                    IAnnotation ProducesAnnotation = null;
                    IAnnotation InjectAnnotation = null;
                    for (IAnnotation annotation : field.getAnnotations()) {
                        if (annotation.getElementName().equals(ManagedBeanConstants.PRODUCES))
                            ProducesAnnotation = annotation;
                        if (annotation.getElementName().equals(ManagedBeanConstants.INJECT))
                            InjectAnnotation = annotation;
                    }
                    if (ProducesAnnotation != null && InjectAnnotation != null) {
                        // A single field cannot have the same
                        diagnostics.add(createDiagnostic(field, unit,
                                "@Produces and @Inject annotations cannot be used on the same field or property",
                                ManagedBeanConstants.DIAGNOSTIC_CODE_PRODUCES_INJECT));
                    }
                }

                if (isManagedBean) {
                    /**
                     * If the managed bean does not have a constructor that takes no parameters, it
                     * must have a constructor annotated @Inject. No additional special annotations
                     * are required.
                     */

                    // Find all methods on the type that are constructors.
                    List<IMethod> constructorMethods = Arrays.stream(type.getMethods())
                            .filter(this::isConstructorMethod).collect(Collectors.toList());

                    // If there are no constructor methods, there is an implicit empty constructor
                    // generated by the compiler.
                    boolean hasEmptyConstructor = constructorMethods.size() == 0;
                    boolean hasParameterizedInjectConstructor = false;

                    for (IMethod m : constructorMethods) {
                        if (m.getNumberOfParameters() == 0)
                            hasEmptyConstructor = true;

                        else if (!hasParameterizedInjectConstructor)
                            hasParameterizedInjectConstructor = Arrays.stream(m.getAnnotations())
                                    .map(annotation -> annotation.getElementName())
                                    .anyMatch(annotation -> annotation.equals("Inject"));
                    }

                    if (!hasEmptyConstructor && !hasParameterizedInjectConstructor) {
                        // Deliver a diagnostic on all parameterized constructors that they must add an
                        // @Inject annotation
                        List<IMethod> methodsNeedingDiagnostics = constructorMethods.stream()
                                .filter(m -> m.getNumberOfParameters() > 0).collect(Collectors.toList());

                        for (IMethod m : methodsNeedingDiagnostics) {
                            Diagnostic diagnostic = createDiagnostic(m, unit,
                                    "If a managed bean does not have a constructor that takes no parameters, it must have a constructor annotated @Inject",
                                    CONSTRUCTOR_DIAGNOSTIC_CODE);
                            diagnostics.add(diagnostic);
                        }

                    }
                }

                /* ========= Inject and Disposes, Observes, ObservesAsync Annotations Checks========= */
                /*
                 * go through each method to make sure @Inject
                 * and @Disposes, @Observes, @ObservesAsync are not used together
                 * 
                 * see: 
                 * https://jakarta.ee/specifications/cdi/3.0/jakarta-cdi-spec-3.0.html#declaring_bean_constructor
                 * https://jakarta.ee/specifications/cdi/3.0/jakarta-cdi-spec-3.0.html#declaring_initializer
                 * 
                 */
                invalidParamsCheck(unit, diagnostics, type, ManagedBeanConstants.INJECT, ManagedBeanConstants.DIAGNOSTIC_CODE_INVALID_INJECT_PARAM);

                if(isManagedBean) {
                    /* ========= Produces and Disposes, Observes, ObservesAsync Annotations Checks========= */
                    /*
                     * go through each method to make sure @Produces
                     * and @Disposes, @Observes, @ObservesAsync are not used together
                     * 
                     * see: 
                     * https://jakarta.ee/specifications/cdi/3.0/jakarta-cdi-spec-3.0.html#declaring_producer_method
                     * 
                     * note:
                     * we need to check for bean defining annotations first to make sure the managed bean is discovered.
                     * 
                     */
                    invalidParamsCheck(unit, diagnostics, type, ManagedBeanConstants.PRODUCES, ManagedBeanConstants.DIAGNOSTIC_CODE_INVALID_PRODUCES_PARAM); 
                }
            }

        } catch (JavaModelException e) {
            Activator.logException("Cannot calculate diagnostics", e);
        }
    }
    
    
    private void invalidParamsCheck(ICompilationUnit unit, List<Diagnostic> diagnostics, IType type, String target, String diagnostic) throws JavaModelException {
        for (IMethod method : type.getMethods()) {
            IAnnotation targetAnnotation = null;

            for (IAnnotation annotation : method.getAnnotations()) {
                if (annotation.getElementName().equals(target))
                    targetAnnotation = annotation;
            }

            if (targetAnnotation == null)
                continue;

            Set<String> invalidAnnotations = new TreeSet<>();
            for (ILocalVariable param : method.getParameters()) {
                for (IAnnotation annotation : param.getAnnotations()) {
                    if (ManagedBeanConstants.INVALID_INJECT_PARAMS.contains(annotation.getElementName())) {
                        invalidAnnotations.add("@" + annotation.getElementName());
                    }
                }
            }

            if(!invalidAnnotations.isEmpty()) {
                String label = createInvalidInjectLabel(target, invalidAnnotations);
                diagnostics.add(createDiagnostic(method, unit, label, diagnostic));
            }

        }
    }
    
    private String createInvalidInjectLabel(String annotation, Set<String> invalidAnnotations) {
        String label = "A bean constructor or a method annotated with @" + annotation + " cannot have parameter(s) annotated with ";
        label += String.join(", ", invalidAnnotations);
        return label;
    }
}
