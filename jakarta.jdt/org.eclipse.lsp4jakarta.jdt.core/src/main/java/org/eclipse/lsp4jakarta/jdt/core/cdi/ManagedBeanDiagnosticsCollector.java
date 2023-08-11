/*******************************************************************************
 * Copyright (c) 2021, 2023 IBM Corporation.
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

import static org.eclipse.lsp4jakarta.jdt.core.cdi.ManagedBeanConstants.CONSTRUCTOR_DIAGNOSTIC_CODE;
import static org.eclipse.lsp4jakarta.jdt.core.cdi.ManagedBeanConstants.DEPENDENT_FQ_NAME;
import static org.eclipse.lsp4jakarta.jdt.core.cdi.ManagedBeanConstants.DIAGNOSTIC_CODE;
import static org.eclipse.lsp4jakarta.jdt.core.cdi.ManagedBeanConstants.DIAGNOSTIC_CODE_SCOPEDECL;
import static org.eclipse.lsp4jakarta.jdt.core.cdi.ManagedBeanConstants.DIAGNOSTIC_SOURCE;
import static org.eclipse.lsp4jakarta.jdt.core.cdi.ManagedBeanConstants.DISPOSES_FQ_NAME;
import static org.eclipse.lsp4jakarta.jdt.core.cdi.ManagedBeanConstants.INJECT_FQ_NAME;
import static org.eclipse.lsp4jakarta.jdt.core.cdi.ManagedBeanConstants.INVALID_INJECT_PARAMS_FQ;
import static org.eclipse.lsp4jakarta.jdt.core.cdi.ManagedBeanConstants.OBSERVES_ASYNC_FQ_NAME;
import static org.eclipse.lsp4jakarta.jdt.core.cdi.ManagedBeanConstants.OBSERVES_FQ_NAME;
import static org.eclipse.lsp4jakarta.jdt.core.cdi.ManagedBeanConstants.PRODUCES;
import static org.eclipse.lsp4jakarta.jdt.core.cdi.ManagedBeanConstants.PRODUCES_FQ_NAME;
import static org.eclipse.lsp4jakarta.jdt.core.cdi.ManagedBeanConstants.SCOPE_FQ_NAMES;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4jakarta.jdt.core.AbstractDiagnosticsCollector;
import org.eclipse.lsp4jakarta.jdt.core.JakartaCorePlugin;
import org.eclipse.lsp4jakarta.jdt.core.Messages;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

public class ManagedBeanDiagnosticsCollector extends AbstractDiagnosticsCollector {

    public ManagedBeanDiagnosticsCollector() {
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

        try {
            IType[] types = unit.getAllTypes();
            String[] scopeFQNames = SCOPE_FQ_NAMES.toArray(String[]::new);
            for (IType type : types) {
                List<String> managedBeanAnnotations = getMatchedJavaElementNames(type,
                        Stream.of(type.getAnnotations()).map(annotation -> annotation.getElementName()).toArray(String[]::new),
                        scopeFQNames);
                boolean isManagedBean = managedBeanAnnotations.size() > 0;

                if (managedBeanAnnotations.size() > 1) {
                    // convert to simple name
                    List<String> diagnosticData = managedBeanAnnotations.stream()
                            .map(annotation -> getSimpleName(annotation)).collect(Collectors.toList());
                    diagnostics.add(createDiagnostic(type, unit,
                            Messages.getMessage("ScopeTypeAnnotationsManagedBean"),
                            DIAGNOSTIC_CODE_SCOPEDECL, (JsonArray) (new Gson().toJsonTree(diagnosticData)),
                            DiagnosticSeverity.Error));
                }

                String[] injectAnnotations = { PRODUCES_FQ_NAME, INJECT_FQ_NAME };
                IField fields[] = type.getFields();
                for (IField field : fields) {
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
                            && !managedBeanAnnotations.contains(DEPENDENT_FQ_NAME)) {
                        diagnostics.add(createDiagnostic(field, unit,
                                Messages.getMessage("ManagedBeanWithNonStaticPublicField"), DIAGNOSTIC_CODE, null, DiagnosticSeverity.Error));
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
                    String[] annotationNames = Stream.of(field.getAnnotations())
                            .map(annotation -> annotation.getElementName()).toArray(String[]::new);
                    List<String> fieldScopes = getMatchedJavaElementNames(type, annotationNames, scopeFQNames);
                    List<String> fieldInjects = getMatchedJavaElementNames(type, annotationNames, injectAnnotations);
                    boolean isProducerField = false, isInjectField = false;
                    for (String annotation : fieldInjects) {
                        if (PRODUCES_FQ_NAME.equals(annotation))
                            isProducerField = true;
                        else if (INJECT_FQ_NAME.equals(annotation))
                            isInjectField = true;
                    }
                    if (isProducerField && fieldScopes.size() > 1) {
                        List<String> diagnosticData = fieldScopes.stream().map(annotation -> getSimpleName(annotation))
                                .collect(Collectors.toList()); // convert to simple name
                        diagnosticData.add(PRODUCES);
                        diagnostics.add(createDiagnostic(field, unit,
                                Messages.getMessage("ScopeTypeAnnotationsProducerField"),
                                DIAGNOSTIC_CODE_SCOPEDECL, (JsonArray) (new Gson().toJsonTree(diagnosticData)),
                                DiagnosticSeverity.Error));
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
                                Messages.getMessage("ManagedBeanProducesAndInject"),
                                ManagedBeanConstants.DIAGNOSTIC_CODE_PRODUCES_INJECT, null, DiagnosticSeverity.Error));
                    }

                }

                IMethod[] methods = type.getMethods();
                List<IMethod> constructorMethods = new ArrayList<IMethod>();
                for (IMethod method : methods) {

                    // Find all methods on the type that are constructors.
                    if (isConstructorMethod(method))
                        constructorMethods.add(method);

                    /**
                     * https://jakarta.ee/specifications/cdi/3.0/jakarta-cdi-spec-3.0.html#declaring_bean_scope
                     * A bean class or producer method or field may specify at most one scope type
                     * annotation. If a bean class or producer method or field specifies multiple
                     * scope type annotations, the container automatically detects the problem and
                     * treats it as a definition error.
                     * 
                     * Here we only look at the methods.
                     */
                    String[] annotationNames = Stream.of(method.getAnnotations())
                            .map(annotation -> annotation.getElementName()).toArray(String[]::new);
                    List<String> methodScopes = getMatchedJavaElementNames(type, annotationNames, scopeFQNames);
                    List<String> methodInjects = getMatchedJavaElementNames(type, annotationNames, injectAnnotations);
                    boolean isProducerMethod = false, isInjectMethod = false;
                    for (String annotation : methodInjects) {
                        if (PRODUCES_FQ_NAME.equals(annotation))
                            isProducerMethod = true;
                        else if (INJECT_FQ_NAME.equals(annotation))
                            isInjectMethod = true;
                    }

                    if (isProducerMethod && methodScopes.size() > 1) {
                        List<String> diagnosticData = methodScopes.stream().map(annotation -> getSimpleName(annotation))
                                .collect(Collectors.toList()); // convert to simple name
                        diagnosticData.add(PRODUCES);
                        diagnostics.add(createDiagnostic(method, unit,
                                Messages.getMessage("ScopeTypeAnnotationsProducerMethod"),
                                DIAGNOSTIC_CODE_SCOPEDECL, (JsonArray) (new Gson().toJsonTree(diagnosticData)),
                                DiagnosticSeverity.Error));
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
                                Messages.getMessage("ManagedBeanProducesAndInject"),
                                ManagedBeanConstants.DIAGNOSTIC_CODE_PRODUCES_INJECT, null, DiagnosticSeverity.Error));
                    }

                }

                if (isManagedBean && constructorMethods.size() > 0) {
                    /**
                     * If the managed bean does not have a constructor that takes no parameters, it
                     * must have a constructor annotated @Inject. No additional special annotations
                     * are required.
                     */

                    // If there are no constructor methods, there is an implicit empty constructor
                    // generated by the compiler.
                    List<IMethod> methodsNeedingDiagnostics = new ArrayList<IMethod>();
                    for (IMethod m : constructorMethods) {
                        if (m.getNumberOfParameters() == 0) {
                            methodsNeedingDiagnostics.clear();
                            break;
                        }
                        IAnnotation[] annotations = m.getAnnotations();
                        boolean hasParameterizedInjectConstructor = false;
                        // look up '@Inject' annotation
                        for (IAnnotation annotation : annotations) {
                            if (isMatchedJavaElement(type, annotation.getElementName(), INJECT_FQ_NAME)) {
                                hasParameterizedInjectConstructor = true;
                                break;
                            }
                        }
                        if (hasParameterizedInjectConstructor) {
                            methodsNeedingDiagnostics.clear();
                            break;
                        } else
                            methodsNeedingDiagnostics.add(m);
                    }

                    // Deliver a diagnostic on all parameterized constructors that they must add an
                    // @Inject annotation
                    for (IMethod m : methodsNeedingDiagnostics) {
                        diagnostics.add(createDiagnostic(m, unit, Messages.getMessage("ManagedBeanConstructorWithParameters"),
                                CONSTRUCTOR_DIAGNOSTIC_CODE, null, DiagnosticSeverity.Error));
                    }
                }

                /**
                 * If a managed bean class is of generic type, it must be annotated with @Dependent
                 */
                if (isManagedBean) {
                    boolean isClassGeneric = type.getTypeParameters().length != 0;
                    boolean isDependent = managedBeanAnnotations.stream()
                            .anyMatch(annotation -> DEPENDENT_FQ_NAME.equals(annotation));

                    if (isClassGeneric && !isDependent) {
                        diagnostics.add(createDiagnostic(type, unit, Messages.getMessage("ManagedBeanGenericType"),
                                DIAGNOSTIC_CODE, null, DiagnosticSeverity.Error));
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
                invalidParamsCheck(unit, diagnostics, type, INJECT_FQ_NAME,
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
                    invalidParamsCheck(unit, diagnostics, type, PRODUCES_FQ_NAME,
                            ManagedBeanConstants.DIAGNOSTIC_CODE_INVALID_PRODUCES_PARAM);

                    for (IMethod method : methods) {
                        int numDisposes = 0;
                        Set<String> invalidAnnotations = new TreeSet<>();
                        ILocalVariable[] params = method.getParameters();

                        for (ILocalVariable param : params) {
                            IAnnotation[] annotations = param.getAnnotations();
                            for (IAnnotation annotation : annotations) {
                                String matchedAnnotation = getMatchedJavaElementName(type, annotation.getElementName(),
                                        INVALID_INJECT_PARAMS_FQ);
                                if (DISPOSES_FQ_NAME.equals(matchedAnnotation)) {
                                    numDisposes++;
                                } else if (OBSERVES_FQ_NAME.equals(matchedAnnotation)
                                        || OBSERVES_ASYNC_FQ_NAME.equals(matchedAnnotation)) {
                                    invalidAnnotations.add("@" + annotation.getElementName());
                                }
                            }
                        }

                        if (numDisposes == 0) {
                            continue;
                        }

                        if (numDisposes > 1) {
                            diagnostics.add(createDiagnostic(method, unit,
                                    Messages.getMessage("ManagedBeanDisposeOneParameter"),
                                    ManagedBeanConstants.DIAGNOSTIC_CODE_REDUNDANT_DISPOSES, null,
                                    DiagnosticSeverity.Error));
                        }

                        if (!invalidAnnotations.isEmpty()) {
                            diagnostics.add(createDiagnostic(method, unit, createInvalidDisposesLabel(invalidAnnotations),
                                    ManagedBeanConstants.DIAGNOSTIC_CODE_INVALID_DISPOSES_PARAM, null,
                                    DiagnosticSeverity.Error));
                        }
                    }
                }
            }

        } catch (JavaModelException e) {
            JakartaCorePlugin.logException("Cannot calculate diagnostics", e);
        }
    }

    private void invalidParamsCheck(ICompilationUnit unit, List<Diagnostic> diagnostics, IType type, String target, String diagnosticCode)
            throws JavaModelException {
        for (IMethod method : type.getMethods()) {
            IAnnotation targetAnnotation = null;

            for (IAnnotation annotation : method.getAnnotations()) {
                if (isMatchedJavaElement(type, annotation.getElementName(), target)) {
                    targetAnnotation = annotation;
                    break;
                }
            }

            if (targetAnnotation == null)
                continue;

            Set<String> invalidAnnotations = new TreeSet<>();
            ILocalVariable[] params = method.getParameters();
            for (ILocalVariable param : params) {
                List<String> paramScopes = getMatchedJavaElementNames(type, Stream.of(param.getAnnotations())
                        .map(annotation -> annotation.getElementName()).toArray(String[]::new),
                        INVALID_INJECT_PARAMS_FQ);
                for (String annotation : paramScopes) {
                    invalidAnnotations.add("@" + getSimpleName(annotation));
                }
            }

            if (!invalidAnnotations.isEmpty()) {
                String label = PRODUCES_FQ_NAME.equals(target) ?
                        createInvalidProducesLabel(invalidAnnotations) :
                        createInvalidInjectLabel(invalidAnnotations);
                diagnostics.add(createDiagnostic(method, unit, label, diagnosticCode, null, DiagnosticSeverity.Error));
            }

        }
    }

    private String createInvalidInjectLabel(Set<String> invalidAnnotations) {
        return Messages.getMessage("ManagedBeanInvalidInject", String.join(", ", invalidAnnotations)); // assuming comma delimited list is ok
    }
    
    private String createInvalidProducesLabel(Set<String> invalidAnnotations) {
        return Messages.getMessage("ManagedBeanInvalidProduces", String.join(", ", invalidAnnotations)); // assuming comma delimited list is ok
    }
    
    private String createInvalidDisposesLabel(Set<String> invalidAnnotations) {
        return Messages.getMessage("ManagedBeanInvalidDisposer", String.join(", ", invalidAnnotations)); // assuming comma delimited list is ok
    }
}
