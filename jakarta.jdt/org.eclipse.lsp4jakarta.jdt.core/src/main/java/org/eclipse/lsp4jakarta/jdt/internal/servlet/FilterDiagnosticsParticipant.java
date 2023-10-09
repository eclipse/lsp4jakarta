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
import org.eclipse.jdt.core.IMemberValuePair;
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
 * @WebFilter annotation diagnostic participant.
 */
public class FilterDiagnosticsParticipant implements IJavaDiagnosticsParticipant {

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

        IAnnotation[] allAnnotations;

        IType[] alltypes = unit.getAllTypes();
        for (IType type : alltypes) {
            allAnnotations = type.getAnnotations();
            IAnnotation webFilterAnnotation = null;

            for (IAnnotation annotation : allAnnotations) {
                if (DiagnosticUtils.isMatchedJavaElement(type, annotation.getElementName(),
                                                         Constants.WEBFILTER_FQ_NAME)) {
                    webFilterAnnotation = annotation;
                }
            }

            String[] interfaces = { Constants.FILTER_FQ_NAME };
            boolean isFilterImplemented = DiagnosticUtils.doesImplementInterfaces(type, interfaces);

            if (webFilterAnnotation != null && !isFilterImplemented) {
                Range range = PositionUtils.toNameRange(type, context.getUtils());
                diagnostics.add(context.createDiagnostic(uri,
                                                         Messages.getMessage("WebFilterMustImplement"), range,
                                                         Constants.DIAGNOSTIC_SOURCE, null,
                                                         ErrorCode.ClassWebFilterAnnotatedNoFilterInterfaceImpl, DiagnosticSeverity.Error));
            }

            /* URL pattern diagnostic check */
            if (webFilterAnnotation != null) {
                IMemberValuePair[] memberValues = webFilterAnnotation.getMemberValuePairs();

                boolean isUrlpatternSpecified = false;
                boolean isServletNamesSpecified = false;
                boolean isValueSpecified = false;
                for (IMemberValuePair mv : memberValues) {
                    if (mv.getMemberName().equals(Constants.URL_PATTERNS)) {
                        isUrlpatternSpecified = true;
                        continue;
                    }
                    if (mv.getMemberName().equals(Constants.SERVLET_NAMES)) {
                        isServletNamesSpecified = true;
                        continue;
                    }
                    if (mv.getMemberName().equals(Constants.VALUE)) {
                        isValueSpecified = true;
                    }
                }
                if (!isUrlpatternSpecified && !isValueSpecified && !isServletNamesSpecified) {
                    Range range = PositionUtils.toNameRange(webFilterAnnotation, context.getUtils());
                    diagnostics.add(context.createDiagnostic(uri,
                                                             Messages.getMessage("WebFilterMustDefine"), range,
                                                             Constants.DIAGNOSTIC_SOURCE, null,
                                                             ErrorCode.WebFilterAnnotationMissingAttributes, DiagnosticSeverity.Error));
                }
                if (isUrlpatternSpecified && isValueSpecified) {
                    Range range = PositionUtils.toNameRange(webFilterAnnotation, context.getUtils());
                    diagnostics.add(context.createDiagnostic(uri,
                                                             Messages.getMessage("WebFilterCannotHaveBoth"), range,
                                                             Constants.DIAGNOSTIC_SOURCE, null,
                                                             ErrorCode.WebFilterAnnotationAttributeConflict, DiagnosticSeverity.Error));
                }
            }
        }

        return diagnostics;
    }
}
