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

package org.eclipse.lsp4jakarta.jdt.internal.servlet;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4jakarta.jdt.core.java.diagnostics.IJavaDiagnosticsParticipant;
import org.eclipse.lsp4jakarta.jdt.core.java.diagnostics.JavaDiagnosticsContext;
import org.eclipse.lsp4jakarta.jdt.core.utils.IJDTUtils;
import org.eclipse.lsp4jakarta.jdt.core.utils.PositionUtils;
import org.eclipse.lsp4jakarta.jdt.internal.DiagnosticUtils;
import org.eclipse.lsp4jakarta.jdt.internal.Messages;
import org.eclipse.lsp4jakarta.jdt.internal.core.ls.JDTUtilsLSImpl;

/**
 * Servlet and HTTP session listener diagnostic participant.
 */
public class ListenerDiagnosticsParticipant implements IJavaDiagnosticsParticipant {

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Diagnostic> collectDiagnostics(JavaDiagnosticsContext context, IProgressMonitor monitor) throws CoreException {
        String uri = context.getUri();
        IJDTUtils utils = JDTUtilsLSImpl.getInstance();
        ICompilationUnit unit = utils.resolveCompilationUnit(uri);
        List<Diagnostic> diagnostics = new ArrayList<>();

        if (unit == null) {
            return diagnostics;
        }

        IType[] alltypes;
        IAnnotation[] allAnnotations;

        alltypes = unit.getAllTypes();
        for (IType type : alltypes) {
            allAnnotations = type.getAnnotations();
            boolean isWebListenerAnnotated = false;
            for (IAnnotation annotation : allAnnotations) {
                if (DiagnosticUtils.isMatchedJavaElement(type, annotation.getElementName(),
                                                         Constants.WEB_LISTENER_FQ_NAME)) {
                    isWebListenerAnnotated = true;
                    break;
                }
            }

            String[] interfaces = { Constants.SERVLET_CONTEXT_LISTENER_FQ_NAME,
                                    Constants.SERVLET_CONTEXT_ATTRIBUTE_LISTENER_FQ_NAME,
                                    Constants.SERVLET_REQUEST_LISTENER_FQ_NAME,
                                    Constants.SERVLET_REQUEST_ATTRIBUTE_LISTENER_FQ_NAME,
                                    Constants.HTTP_SESSION_LISTENER_FQ_NAME,
                                    Constants.HTTP_SESSION_ATTRIBUTE_LISTENER_FQ_NAME,
                                    Constants.HTTP_SESSION_ID_LISTENER_FQ_NAME };
            boolean isImplemented = DiagnosticUtils.doesImplementInterfaces(type, interfaces);

            if (isWebListenerAnnotated && !isImplemented) {
                Range range = PositionUtils.toNameRange(type, context.getUtils());
                diagnostics.add(context.createDiagnostic(uri,
                                                         Messages.getMessage("AnnotatedWithWebListenerMustImplement"), range,
                                                         Constants.DIAGNOSTIC_SOURCE, null,
                                                         ErrorCode.WebFilterAnnotatedClassReqIfaceNoImpl, DiagnosticSeverity.Error));
            }
        }

        return diagnostics;
    }
}
