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
 *******************************************************************************/

package org.eclipse.lsp4jakarta.jdt.core.websocket;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4jakarta.jdt.core.JDTUtils;
import org.eclipse.lsp4jakarta.jdt.core.DiagnosticsCollector;
import org.eclipse.lsp4jakarta.jdt.core.JakartaCorePlugin;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.lsp4jakarta.jdt.core.AnnotationUtil;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;

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
                    invalidParamsCheck(type, WebSocketConstants.ON_OPEN, WebSocketConstants.ON_OPEN_PARAM_OPT_TYPES, 
                    WebSocketConstants.RAW_ON_OPEN_PARAM_OPT_TYPES, unit, diagnostics);
                }
            }
        } catch (JavaModelException e) {
            JakartaCorePlugin.logException(WebSocketConstants.DIAGNOSTIC_ERR_MSG, e);
        }
    }

    private void invalidParamsCheck(IType type, String methodAnnotTarget, Set<String> specialParamTypes, Set<String> rawSpecialParamTypes, ICompilationUnit unit,
            List<Diagnostic> diagnostics) throws JavaModelException {

        IMethod[] allMethods = type.getMethods();

        for (IMethod method : allMethods) {
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
                        } else {
                            String simpleParamType = Signature.getSignatureSimpleName(signature);
                            isSpecialType = rawSpecialParamTypes.contains(simpleParamType);
                            isPrimWrapped = isWrapper(simpleParamType);
                        }

                        // check parameters valid types
                        if (!(isSpecialType || isPrimWrapped || isPrimitive)) {
                            Diagnostic diagnostic = createDiagnostic(param, unit,
                                    WebSocketConstants.DIAGNOSTIC_ON_OPEN_INVALID_PARAMS,
                                    WebSocketConstants.DIAGNOSTIC_CODE_ON_OPEN_INVALID_PARAMS);
                            diagnostics.add(diagnostic);
                            return;
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
                }
            }
        }
    }

    /**
    * Check if valueClass is a wrapper object for a primitive value
    * Based on https://github.com/eclipse/lsp4mp/blob/9789a1a996811fade43029605c014c7825e8f1da/microprofile.jdt/org.eclipse.lsp4mp.jdt.core/src/main/java/org/eclipse/lsp4mp/jdt/core/utils/JDTTypeUtils.java#L294-L298
    * 
    * @param valueClass the resolved type of valueClass in string or the simple type of valueClass 
    * @return if valueClass is a wrapper object
    */
    private boolean isWrapper(String valueClass) {
        return WebSocketConstants.WRAPPER_OBJS.contains(valueClass) || WebSocketConstants.RAW_WRAPPER_OBJS.contains(valueClass);
    }


    /**
    * Checks if type is a WebSocket endpoint by meeting one of the 2 conditions listed on 
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

        // Check that class follows https://jakarta.ee/specifications/websocket/2.0/websocket-spec-2.0.html#applications
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
}
