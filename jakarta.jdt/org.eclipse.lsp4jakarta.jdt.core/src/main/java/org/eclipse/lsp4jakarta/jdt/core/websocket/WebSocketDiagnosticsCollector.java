/******************************************************************************* 
 * Copyright (c) 2022 IBM Corporation and others. 
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v. 2.0 which is available at 
 * http://www.eclipse.org/legal/epl-2.0. 
 * 
 * SPDX-License-Identifier: EPL-2.0 
 * 
 * Contributors: 
 *     Giancarlo Pernudi Segura - initial API and implementation
 *     Lidia Ataupillco Ramos
 *     Aviral Saxena
 *******************************************************************************/

package org.eclipse.lsp4jakarta.jdt.core.websocket;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4jakarta.jdt.core.DiagnosticsCollector;
import org.eclipse.lsp4jakarta.jdt.core.JDTUtils;
import org.eclipse.lsp4jakarta.jdt.core.JakartaCorePlugin;
import org.eclipse.lsp4jakarta.jdt.core.websocket.WebSocketConstants;
import org.eclipse.jdt.core.*;

import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.lsp4jakarta.jdt.core.AnnotationUtil;

import static org.eclipse.lsp4jakarta.jdt.core.TypeHierarchyUtils.doesITypeHaveSuperType;

public class WebSocketDiagnosticsCollector implements DiagnosticsCollector {
    public WebSocketDiagnosticsCollector() {
    }

    private Diagnostic createDiagnostic(IJavaElement el, ICompilationUnit unit, String msg, String code) {
        try {
            ISourceRange nameRange = JDTUtils.getNameRange(el);
            Range range = JDTUtils.toRange(unit, nameRange.getOffset(), nameRange.getLength());
            Diagnostic diagnostic = new Diagnostic(range, msg);
            diagnostic.setCode(code);
            completeDiagnostic(diagnostic);
            return diagnostic;
        } catch (JavaModelException e) {
            JakartaCorePlugin.logException(WebSocketConstants.DIAGNOSTIC_ERR_MSG, e);
        }
        return null;
    }

    @Override
    public void completeDiagnostic(Diagnostic diagnostic) {
        diagnostic.setSource(WebSocketConstants.DIAGNOSTIC_SOURCE);
        diagnostic.setSeverity(WebSocketConstants.ERROR);
    }

    @Override
    public void collectDiagnostics(ICompilationUnit unit, List<Diagnostic> diagnostics) {
        if (unit == null) {
            return;
        }

        IType[] alltypes;

        HashMap<String, Boolean> checkWSEnd = null;

        try {
            alltypes = unit.getAllTypes();
            for (IType type : alltypes) {
                checkWSEnd = isWSEndpoint(type);
                // checks if the class uses annotation to create a WebSocket endpoint
                if (checkWSEnd.get(WebSocketConstants.IS_ANNOTATION)) {
                    // WebSocket Invalid Parameters Diagnostic
                    invalidParamsCheck(type, WebSocketConstants.ON_OPEN, WebSocketConstants.ON_OPEN_PARAM_OPT_TYPES, 
                            WebSocketConstants.RAW_ON_OPEN_PARAM_OPT_TYPES, WebSocketConstants.DIAGNOSTIC_CODE_ON_OPEN_INVALID_PARAMS, 
                            Collections.emptySet(), Collections.emptySet(), unit, diagnostics);
                    invalidParamsCheck(type, WebSocketConstants.ON_CLOSE, WebSocketConstants.ON_CLOSE_PARAM_OPT_TYPES, 
                            WebSocketConstants.RAW_ON_CLOSE_PARAM_OPT_TYPES, WebSocketConstants.DIAGNOSTIC_CODE_ON_CLOSE_INVALID_PARAMS, 
                            Collections.emptySet(), Collections.emptySet(), unit, diagnostics);
                    invalidParamsCheck(type, WebSocketConstants.ON_ERROR, WebSocketConstants.ON_ERROR_PARAM_OPT_TYPES, 
                            WebSocketConstants.RAW_ON_ERROR_PARAM_OPT_TYPES, WebSocketConstants.DIAGNOSTIC_CODE_ON_ERROR_INVALID_PARAMS,
                            WebSocketConstants.ON_ERROR_PARAM_MAND_TYPES, WebSocketConstants.RAW_ON_ERROR_PARAM_MAND_TYPES, unit, diagnostics);
                    
                    // PathParam URI Mismatch Warning Diagnostic
                    uriMismatchWarningCheck(type, diagnostics, unit);
                }
            }
        } catch (JavaModelException e) {
            JakartaCorePlugin.logException(WebSocketConstants.DIAGNOSTIC_ERR_MSG, e);
        }
    }

    private HashMap<String, Boolean> intializeHashMap(Set<String> types) {
        HashMap<String, Boolean> numTypes = new HashMap<>();
        for (String type : types) {
            numTypes.put(type, false);
        }
        return numTypes;
    }

    private void invalidParamsCheck(IType type, String methodAnnotTarget, Set<String> specialParamTypes, Set<String> rawSpecialParamTypes, String diagnosticCode,
            Set<String> mandParamTypes, Set<String> rawMandParamTypes,  ICompilationUnit unit, List<Diagnostic> diagnostics) throws JavaModelException {

        IMethod[] allMethods = type.getMethods();

        for (IMethod method : allMethods) {
            HashMap<String, Boolean> mandTypeCounter = intializeHashMap(mandParamTypes);
            IAnnotation[] allAnnotations = method.getAnnotations();

            for (IAnnotation annotation : allAnnotations) {
                if (annotation.getElementName().equals(methodAnnotTarget)) {
                    ILocalVariable[] allParams = method.getParameters();

                    for (ILocalVariable param : allParams) {
                        String signature = param.getTypeSignature();
                        String formatSignature = signature.replace("/", ".");
                        String resolvedTypeName = JavaModelUtil.getResolvedTypeName(formatSignature, type);

                        boolean isPrimitive = JavaModelUtil.isPrimitive(formatSignature);
                        boolean isSpecialType;
                        boolean isPrimWrapped;

                        if (resolvedTypeName != null) {
                            isSpecialType = specialParamTypes.contains(resolvedTypeName);
                            isPrimWrapped = isWrapper(resolvedTypeName);
                            boolean isMandParam = mandParamTypes.contains(resolvedTypeName);
                            if (isMandParam) {
                                if (mandTypeCounter.get(resolvedTypeName) == true) {
                                    // TODO: throw diagnostic issue
                                    System.out.println("Duplicate Parameter");
                                    continue;
                                } else {
                                    mandTypeCounter.put(resolvedTypeName, true);
                                }
                            }
                        } else {
                            // TODO: fixed the kind of hashmap we're using
                            String simpleParamType = Signature.getSignatureSimpleName(signature);
                            isSpecialType = rawSpecialParamTypes.contains(simpleParamType);
                            isPrimWrapped = isWrapper(simpleParamType);
                            boolean isMandParam = rawMandParamTypes.contains(simpleParamType);
                            if (isMandParam) {
                                if (mandTypeCounter.get(simpleParamType) == true) {
                                    // TODO: throw diagnostic issue
                                    System.out.println("Duplicate Parameter");
                                    continue;
                                } else {
                                    mandTypeCounter.put(simpleParamType, true);
                                }
                            }
                        }

                        // check parameters valid types
                        if (!(isSpecialType || isPrimWrapped || isPrimitive)) {
                            Diagnostic diagnostic = createDiagnostic(param, unit,
                                    createParamTypeDiagMsg(specialParamTypes, methodAnnotTarget),
                                    diagnosticCode);
                            diagnostics.add(diagnostic);
                            continue;
                        }

                        if (!isSpecialType) {
                            // check that if parameter is not a specialType, it has a @PathParam annotation
                            IAnnotation[] param_annotations = param.getAnnotations();
                            boolean hasPathParamAnnot = Arrays.asList(param_annotations).stream().anyMatch(
                                    annot -> annot.getElementName().equals(WebSocketConstants.PATH_PARAM_ANNOTATION));

                            if (!hasPathParamAnnot) {
                                Diagnostic diagnostic = createDiagnostic(param, unit,
                                        WebSocketConstants.DIAGNOSTIC_PATH_PARAMS_ANNOT_MISSING,
                                        WebSocketConstants.DIAGNOSTIC_CODE_PATH_PARMS_ANNOT);
                                diagnostics.add(diagnostic);
                            }
                        }
                    }

                    // check that all mandatory parameters are present
                    for (HashMap.Entry<String, Boolean> entry : mandTypeCounter.entrySet()) {
                        if (entry.getValue() == false) {
                            // TODO: create a method for mandatory param diagnostic createMandParamDiagMsg(mandParamTypes, methodAnnotTarget)
                            Diagnostic diagnostic = createDiagnostic(method, unit,
                                    WebSocketConstants.DIAGNOSTIC_ON_ERROR_MAND_PARAMS_MISSING,
                                    WebSocketConstants.DIAGNOSTIC_CODE_ON_ERROR_MAND_PARAMS_MISS);
                            diagnostics.add(diagnostic);
                        }
                    }
                }
            }
        }
    }

    /**
     * Creates a warning diagnostic if a PathParam annotation does not match any
     * variable parameters of the WebSocket EndPoint URI associated with the class
     * in which the method is contained
     * 
     * @param type representing the class list of diagnostics for this class
     *             compilation unit with which the type is associated
     */
    private void uriMismatchWarningCheck(IType type, List<Diagnostic> diagnostics, ICompilationUnit unit)
            throws JavaModelException {

        /* @PathParam Value Mismatch Warning */
        List<String> endpointPathVars = findAndProcessEndpointURI(type);
        /*
         * WebSocket endpoint annotations must be attached to a class, and thus is
         * guaranteed to be processed before any of the member method annotations
         */
        if (endpointPathVars == null) {
            return;
        }
        IMethod[] typeMethods = type.getMethods();
        for (IMethod method : typeMethods) {
            ILocalVariable[] methodParams = method.getParameters();
            for (ILocalVariable param : methodParams) {
                IAnnotation[] paramAnnotations = param.getAnnotations();
                for (IAnnotation annotation : paramAnnotations) {
                    if (annotation.getElementName() == WebSocketConstants.PATHPARAM_ANNOTATION) {
                        IMemberValuePair[] valuePairs = annotation.getMemberValuePairs();
                        for (IMemberValuePair pair : valuePairs) {
                            if (pair.getMemberName().equals(WebSocketConstants.ANNOTATION_VALUE)
                                    && pair.getValueKind() == IMemberValuePair.K_STRING) {
                                String pathValue = (String) pair.getValue();
                                if (!endpointPathVars.contains(pathValue)) {
                                    Diagnostic d = createPathParamWarningDiagnostic(annotation, unit);
                                    diagnostics.add(d);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Creates a PathParam URI Mismatch Warning Diagnostic given its components
     * 
     * @param the annotation onto which the diagnostic needs to be displayed the
     *            compilation unit with which said annotation is associated
     * @return the final Diagnostic with its attributes set as needed
     */
    private Diagnostic createPathParamWarningDiagnostic(IJavaElement annotation, IOpenable unit)
            throws JavaModelException {
        ISourceRange nameRange = JDTUtils.getNameRange(annotation);
        Range range = JDTUtils.toRange(unit, nameRange.getOffset(), nameRange.getLength());
        Diagnostic diagnostic = new Diagnostic(range, WebSocketConstants.PATHPARAM_VALUE_WARN_MSG);
        diagnostic.setSource(WebSocketConstants.DIAGNOSTIC_SOURCE);
        diagnostic.setSeverity(WebSocketConstants.WARNING);
        diagnostic.setCode(WebSocketConstants.PATHPARAM_DIAGNOSTIC_CODE);
        return diagnostic;
    }

    /**
     * Finds a WebSocket EndPoint annotation and extracts all variable parameters in
     * the EndPoint URI
     * 
     * @param type representing the class
     * @return List of variable parameters in the EndPoint URI if one exists, null
     *         otherwise
     */
    private List<String> findAndProcessEndpointURI(IType type) throws JavaModelException {
        String endpointURI = null;
        IAnnotation[] typeAnnotations = type.getAnnotations();
        for (IAnnotation annotation : typeAnnotations) {
            if (annotation.getElementName().equals(WebSocketConstants.SERVER_ENDPOINT_ANNOTATION)
                    || annotation.getElementName().equals(WebSocketConstants.CLIENT_ENDPOINT_ANNOTATION)) {
                IMemberValuePair[] valuePairs = annotation.getMemberValuePairs();
                for (IMemberValuePair pair : valuePairs) {
                    if (pair.getMemberName().equals(WebSocketConstants.ANNOTATION_VALUE)
                            && pair.getValueKind() == IMemberValuePair.K_STRING) {
                        endpointURI = (String) pair.getValue();
                    }
                }
            }
        }
        if (endpointURI == null) {
            return null;
        }
        List<String> endpointPathVars = new ArrayList<String>();
        String[] endpointParts = endpointURI.split(WebSocketConstants.URI_SEPARATOR);
        for (String part : endpointParts) {
            if (part.startsWith(WebSocketConstants.CURLY_BRACE_START)
                    && part.endsWith(WebSocketConstants.CURLY_BRACE_END)) {
                endpointPathVars.add(part.substring(1, part.length() - 1));
            }
        }
        return endpointPathVars;
    }

    /**
     * Check if valueClass is a wrapper object for a primitive value Based on
     * https://github.com/eclipse/lsp4mp/blob/9789a1a996811fade43029605c014c7825e8f1da/microprofile.jdt/org.eclipse.lsp4mp.jdt.core/src/main/java/org/eclipse/lsp4mp/jdt/core/utils/JDTTypeUtils.java#L294-L298
     * 
     * @param valueClass the resolved type of valueClass in string or the simple
     *                   type of valueClass
     * @return if valueClass is a wrapper object
     */
    private boolean isWrapper(String valueClass) {
        return WebSocketConstants.WRAPPER_OBJS.contains(valueClass)
                || WebSocketConstants.RAW_WRAPPER_OBJS.contains(valueClass);
    }

    /**
     * Checks if type is a WebSocket endpoint by meeting one of the 2 conditions
     * listed on
     * https://jakarta.ee/specifications/websocket/2.0/websocket-spec-2.0.html#applications
     * are met: class is annotated or class implements Endpoint class
     * 
     * @param type the type representing the class
     * @return the conditions for a class to be a WebSocket endpoint
     * @throws JavaModelException
     */
    private HashMap<String, Boolean> isWSEndpoint(IType type) throws JavaModelException {
        HashMap<String, Boolean> wsEndpoint = new HashMap<>();

        // check trivial case
        if (!type.isClass()) {
            wsEndpoint.put(WebSocketConstants.IS_ANNOTATION, false);
            wsEndpoint.put(WebSocketConstants.IS_SUPERCLASS, false);
            return wsEndpoint;
        }

        // Check that class follows
        // https://jakarta.ee/specifications/websocket/2.0/websocket-spec-2.0.html#applications
        List<String> scopes = AnnotationUtil.getScopeAnnotations(type, WebSocketConstants.WS_ANNOTATION_CLASS);
        boolean useAnnotation = scopes.size() > 0;

        boolean useSuperclass = false;

        String superclass = type.getSuperclassName();
        try {
            int r = doesITypeHaveSuperType(type, WebSocketConstants.ENDPOINT_SUPERCLASS);
            useSuperclass = (r >= 0);
        } catch (CoreException e) {
            JakartaCorePlugin.logException(WebSocketConstants.DIAGNOSTIC_ERR_MSG, e);
        }

        wsEndpoint.put(WebSocketConstants.IS_ANNOTATION, useAnnotation);
        wsEndpoint.put(WebSocketConstants.IS_SUPERCLASS, useSuperclass);

        return wsEndpoint;
    }


    private String createParamTypeDiagMsg(Set<String> methodParamOptTypes, String methodAnnotTarget) {
        String paramMessage = String.join("\n- ", methodParamOptTypes);
        return String.format(WebSocketConstants.PARAM_TYPE_DIAG_MSG, "@" + methodAnnotTarget, paramMessage);
    }
}
