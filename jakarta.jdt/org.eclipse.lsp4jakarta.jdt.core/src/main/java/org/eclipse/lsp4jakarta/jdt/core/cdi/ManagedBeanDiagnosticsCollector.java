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

package org.eclipse.lsp4jakarta.jdt.core.cdi;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;
import java.util.stream.Collectors;

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
import org.eclipse.lsp4jakarta.jdt.core.DiagnosticsCollector;
import org.eclipse.lsp4jakarta.jdt.core.JDTUtils;
import org.eclipse.lsp4jakarta.jdt.core.JakartaCorePlugin;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import static org.eclipse.lsp4jakarta.jdt.core.cdi.ManagedBeanConstants.*;
import static org.eclipse.lsp4jakarta.jdt.core.cdi.Utils.getScopeAnnotations;

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
        	JakartaCorePlugin.logException("Cannot calculate diagnostics", e);
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
                List<String> managedBeanAnnotations = getScopeAnnotations(type);
                boolean isManagedBean = managedBeanAnnotations.size() > 0;

                if (managedBeanAnnotations.size() > 1) {
                    Diagnostic diagnostic = createDiagnostic(type, unit,
                            "A managed bean class may specify at most one scope type annotation.",
                            DIAGNOSTIC_CODE_SCOPEDECL);
                    
                    diagnostic.setData((JsonArray)(new Gson().toJsonTree(managedBeanAnnotations)));
                    diagnostics.add(diagnostic);
                }

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

                    /**
                     * https://jakarta.ee/specifications/cdi/3.0/jakarta-cdi-spec-3.0.html#declaring_bean_scope
                     * A bean class or producer method or field may specify at most one scope type
                     * annotation. If a bean class or producer method or field specifies multiple
                     * scope type annotations, the container automatically detects the problem and
                     * treats it as a definition error.
                     * 
                     * Here we only look at the fields.
                     */
                    List<IAnnotation> fieldAnnotations = Arrays.asList(field.getAnnotations());
                    List<String> fieldScopes = getScopeAnnotations(field);

                    boolean isProducerField = fieldAnnotations.stream()
                            .anyMatch(annotation -> annotation.getElementName().equals(ManagedBeanConstants.PRODUCES));

                    boolean isInjectField = fieldAnnotations.stream()
                            .anyMatch(annotation -> annotation.getElementName().equals(ManagedBeanConstants.INJECT));

                    if (isProducerField && fieldScopes.size() > 1) {
                        Diagnostic diagnostic = createDiagnostic(field, unit,
                                "A producer field may specify at most one scope type annotation.", DIAGNOSTIC_CODE_SCOPEDECL);
                        List<String> diagnosticData = new ArrayList<>(fieldScopes);
                        diagnosticData.add(ManagedBeanConstants.PRODUCES);
                        
                        diagnostic.setData((JsonArray)(new Gson().toJsonTree(diagnosticData)));
                        diagnostics.add(diagnostic);
                    }

                    if (isProducerField && isInjectField) {
                        /*
                         * ========= Produces and Inject Annotations Checks ========= 
                         * 
                         * go through each field and method to make sure @Produces and @Inject are not used together
                         * 
                         * see: https://jakarta.ee/specifications/cdi/3.0/jakarta-cdi-spec-3.0.html#declaring_producer_field
                         * https://jakarta.ee/specifications/cdi/3.0/jakarta-cdi-spec-3.0.html#declaring_producer_method
                         * https://jakarta.ee/specifications/cdi/3.0/jakarta-cdi-spec-3.0.html#declaring_injected_field
                         * https://jakarta.ee/specifications/cdi/3.0/jakarta-cdi-spec-3.0.html#declaring_initializer
                         */

                        // A single field cannot have the same
                        diagnostics.add(createDiagnostic(field, unit,
                                "@Produces and @Inject annotations cannot be used on the same field or property",
                                ManagedBeanConstants.DIAGNOSTIC_CODE_PRODUCES_INJECT));
                    }

                }

                for (IMethod method : type.getMethods()) {
                    /**
                     * https://jakarta.ee/specifications/cdi/3.0/jakarta-cdi-spec-3.0.html#declaring_bean_scope
                     * A bean class or producer method or field may specify at most one scope type
                     * annotation. If a bean class or producer method or field specifies multiple
                     * scope type annotations, the container automatically detects the problem and
                     * treats it as a definition error.
                     * 
                     * Here we only look at the methods.
                     */
                    List<IAnnotation> methodAnnotations = Arrays.asList(method.getAnnotations());
                    List<String> methodScopes = getScopeAnnotations(method);

                    boolean isProducerMethod = methodAnnotations.stream()
                            .anyMatch(annotation -> annotation.getElementName().equals(ManagedBeanConstants.PRODUCES));

                    boolean isInjectMethod = methodAnnotations.stream()
                            .anyMatch(annotation -> annotation.getElementName().equals(ManagedBeanConstants.INJECT));

                    if (isProducerMethod && methodScopes.size() > 1) {
                        Diagnostic diagnostic = createDiagnostic(method, unit,
                                "A producer method may specify at most one scope type annotation.", DIAGNOSTIC_CODE_SCOPEDECL);
                        List<String> diagnosticData = new ArrayList<>(methodScopes);
                        diagnosticData.add(ManagedBeanConstants.PRODUCES);
                        
                        diagnostic.setData((JsonArray)(new Gson().toJsonTree(diagnosticData)));
                        diagnostics.add(diagnostic);
                    }

                    if (isProducerMethod && isInjectMethod) {
                        /*
                         * ========= Produces and Inject Annotations Checks ========= 
                         * 
                         * go through each field and method to make sure @Produces and @Inject are not used together
                         * 
                         * see: https://jakarta.ee/specifications/cdi/3.0/jakarta-cdi-spec-3.0.html#declaring_producer_field
                         * https://jakarta.ee/specifications/cdi/3.0/jakarta-cdi-spec-3.0.html#declaring_producer_method
                         * https://jakarta.ee/specifications/cdi/3.0/jakarta-cdi-spec-3.0.html#declaring_injected_field
                         * https://jakarta.ee/specifications/cdi/3.0/jakarta-cdi-spec-3.0.html#declaring_initializer
                         */

                        // A single method cannot have the same
                        diagnostics.add(createDiagnostic(method, unit,
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
                
                /**
                 * If a managed bean class is of generic type, it must be annotated with @Dependent
                 */
                if (isManagedBean) {
                	boolean isClassGeneric = type.getTypeParameters().length != 0;
                    
                    if (isClassGeneric && managedBeanAnnotations.stream()
                            .anyMatch(annotation -> !annotation.equals("Dependent"))) {
                    	diagnostics.add(createDiagnostic(type, unit, "Managed bean class of generic type must have scope @dependent.",
                    			DIAGNOSTIC_CODE));
                    }
                }

                /*
                 * ========= Inject and Disposes, Observes, ObservesAsync Annotations Checks=========
                 */
                /*
                 * go through each method to make sure @Inject
                 * and @Disposes, @Observes, @ObservesAsync are not used together
                 * 
                 * see: https://jakarta.ee/specifications/cdi/3.0/jakarta-cdi-spec-3.0.html#declaring_bean_constructor
                 * https://jakarta.ee/specifications/cdi/3.0/jakarta-cdi-spec-3.0.html#declaring_initializer
                 * 
                 */
                invalidParamsCheck(unit, diagnostics, type, ManagedBeanConstants.INJECT,
                        ManagedBeanConstants.DIAGNOSTIC_CODE_INVALID_INJECT_PARAM);

                if (isManagedBean) {
                    /*
                     * ========= Produces and Disposes, Observes, ObservesAsync Annotations Checks=========
                     */
                    /*
                     * go through each method to make sure @Produces
                     * and @Disposes, @Observes, @ObservesAsync are not used together
                     * 
                     * see: https://jakarta.ee/specifications/cdi/3.0/jakarta-cdi-spec-3.0.html#declaring_producer_method
                     * 
                     * note: 
                     * we need to check for bean defining annotations first to make sure the managed bean is discovered.
                     * 
                     */
                    invalidParamsCheck(unit, diagnostics, type, ManagedBeanConstants.PRODUCES,
                            ManagedBeanConstants.DIAGNOSTIC_CODE_INVALID_PRODUCES_PARAM);
                    
                    for (IMethod method : type.getMethods()) {
                        int numDisposes = 0;
                        Set<String> invalidAnnotations = new TreeSet<>();
                        
                        for (ILocalVariable param : method.getParameters()) {
                            for (IAnnotation annotation : param.getAnnotations()) {
                                if (annotation.getElementName().equals(ManagedBeanConstants.DISPOSES)) {
                                    numDisposes++;
                                } else if(annotation.getElementName().equals(ManagedBeanConstants.OBSERVES)
                                        || annotation.getElementName().equals(ManagedBeanConstants.OBSERVES_ASYNC)) {
                                    invalidAnnotations.add("@" + annotation.getElementName());
                                }
                            }
                        }
                        
                        if(numDisposes == 0) continue;
                        if(numDisposes > 1) {
                            diagnostics.add(createDiagnostic(method, unit,
                                    "A method cannot have more than one parameter annotated @Disposes",
                                    ManagedBeanConstants.DIAGNOSTIC_CODE_REDUNDANT_DISPOSES));
                        }
                        
                        if(!invalidAnnotations.isEmpty()) {
                            diagnostics.add(createDiagnostic(method, unit,
                                    createInvalidDisposesLabel(invalidAnnotations),
                                    ManagedBeanConstants.DIAGNOSTIC_CODE_INVALID_DISPOSES_PARAM));
                        }
                    }
                }
            }

        } catch (JavaModelException e) {
        	JakartaCorePlugin.logException("Cannot calculate diagnostics", e);
        }
    }

    private void invalidParamsCheck(ICompilationUnit unit, List<Diagnostic> diagnostics, IType type, String target,
            String diagnosticCode) throws JavaModelException {
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

            if (!invalidAnnotations.isEmpty()) {
                String label = target.equals("Produces") ? createInvalidProducesLabel(invalidAnnotations) : createInvalidInjectLabel(invalidAnnotations);
                diagnostics.add(createDiagnostic(method, unit, label, diagnosticCode));
            }

        }
    }

    private String createInvalidInjectLabel(Set<String> invalidAnnotations) {
        String label = "A bean constructor or a method annotated with @Inject cannot have parameter(s) annotated with ";
        label += String.join(", ", invalidAnnotations);
        return label;
    }
    
    private String createInvalidProducesLabel(Set<String> invalidAnnotations) {
        String label = "A producer method cannot have parameter(s) annotated with ";
        label += String.join(", ", invalidAnnotations);
        return label;
    }
    
    private String createInvalidDisposesLabel(Set<String> invalidAnnotations) {
        String label = "A disposer method cannot have parameter(s) annotated with ";
        label += String.join(", ", invalidAnnotations);
        return label;
    }
}
