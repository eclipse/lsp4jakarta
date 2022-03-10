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
 *******************************************************************************/

package org.eclipse.lsp4jakarta.jdt.core.websocket;

import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4jakarta.jdt.core.DiagnosticsCollector;
import org.eclipse.lsp4jakarta.jdt.core.JDTUtils;
import org.eclipse.lsp4jakarta.jdt.core.JakartaCorePlugin;
import org.eclipse.lsp4jakarta.jdt.core.websocket.WebSocketConstants;
import org.eclipse.jdt.core.*;

import java.util.ArrayList;
import java.util.List;

public class WebSocketDiagnosticsCollector implements DiagnosticsCollector {
    public WebSocketDiagnosticsCollector() {
    }

    @Override
    public void completeDiagnostic(Diagnostic diagnostic) {
        diagnostic.setSource(WebSocketConstants.DIAGNOSTIC_SOURCE);
        diagnostic.setSeverity(WebSocketConstants.SEVERITY);
    }

    @Override
    public void collectDiagnostics(ICompilationUnit unit, List<Diagnostic> diagnostics) {
        if (unit != null) {
            try {
                IType[] alltypes = unit.getAllTypes();
                for (IType type : alltypes) {
                    /* @PathParam Value Mismatch Warning */
                    List<String> endpointPathVars = findAndProcessEndpointURI(type);
                    /*
                     * WebSocket endpoint annotations must be attached to a class, and thus is
                     * guaranteed to be processed before any of the member method annotations
                     */
                    if (endpointPathVars == null) {
                        continue;
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
            } catch (JavaModelException e) {
                JakartaCorePlugin.logException(WebSocketConstants.DIAGNOSTIC_ERR_MSG, e);
            }
        }
    }

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

    private List<String> findAndProcessEndpointURI(IType type) throws JavaModelException {
        String endpointURI = null;
        IAnnotation[] typeAnnotations = type.getAnnotations();
        for (IAnnotation annotation : typeAnnotations) {
            if (annotation.getElementName().equals(WebSocketConstants.WEBSOCKET_SERVER_ANNOTATION)
                    || annotation.getElementName().equals(WebSocketConstants.WEBSOCKET_CLIENT_ANNOTATION)) {
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
}
