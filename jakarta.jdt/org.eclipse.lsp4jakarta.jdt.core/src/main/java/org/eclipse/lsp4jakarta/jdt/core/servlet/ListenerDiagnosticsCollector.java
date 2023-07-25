/*******************************************************************************
* Copyright (c) 2020, 2023 IBM Corporation, Reza Akhavan and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0.
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     IBM Corporation, Reza Akhavan - initial API and implementation
*******************************************************************************/

package org.eclipse.lsp4jakarta.jdt.core.servlet;

import java.util.List;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4jakarta.jdt.core.AbstractDiagnosticsCollector;
import org.eclipse.lsp4jakarta.jdt.core.JakartaCorePlugin;
import org.eclipse.lsp4jakarta.jdt.core.Messages;

public class ListenerDiagnosticsCollector extends AbstractDiagnosticsCollector {

    public ListenerDiagnosticsCollector() {
        super();
    }

    @Override
    protected String getDiagnosticSource() {
        return ServletConstants.DIAGNOSTIC_SOURCE;
    }

    public void collectDiagnostics(ICompilationUnit unit, List<Diagnostic> diagnostics) {
        if (unit != null) {
            IType[] alltypes;
            IAnnotation[] allAnnotations;

            try {
                alltypes = unit.getAllTypes();
                for (IType type : alltypes) {
                    allAnnotations = type.getAnnotations();
                    boolean isWebListenerAnnotated = false;
                    for (IAnnotation annotation : allAnnotations) {
                        if (isMatchedJavaElement(type, annotation.getElementName(),
                                ServletConstants.WEB_LISTENER_FQ_NAME)) {
                            isWebListenerAnnotated = true;
                            break;
                        }
                    }

                    String[] interfaces = { ServletConstants.SERVLET_CONTEXT_LISTENER_FQ_NAME,
                            ServletConstants.SERVLET_CONTEXT_ATTRIBUTE_LISTENER_FQ_NAME,
                            ServletConstants.SERVLET_REQUEST_LISTENER_FQ_NAME,
                            ServletConstants.SERVLET_REQUEST_ATTRIBUTE_LISTENER_FQ_NAME,
                            ServletConstants.HTTP_SESSION_LISTENER_FQ_NAME,
                            ServletConstants.HTTP_SESSION_ATTRIBUTE_LISTENER_FQ_NAME,
                            ServletConstants.HTTP_SESSION_ID_LISTENER_FQ_NAME };
                    boolean isImplemented = doesImplementInterfaces(type, interfaces);

                    if (isWebListenerAnnotated && !isImplemented) {
                        diagnostics.add(createDiagnostic(type, unit, Messages.getMessage("AnnotatedWithWebListenerMustImplement"),
                                ServletConstants.DIAGNOSTIC_CODE_LISTENER, null, DiagnosticSeverity.Error));
                    }
                }
            } catch (JavaModelException e) {
                JakartaCorePlugin.logException("Cannot calculate diagnostics", e);
            }
        }
    }

}
