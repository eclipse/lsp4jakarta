/*******************************************************************************
* Copyright (c) 2020, 2022 IBM Corporation, Reza Akhavan and others.
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
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4jakarta.jdt.core.AbstractDiagnosticsCollector;
import org.eclipse.lsp4jakarta.jdt.core.JakartaCorePlugin;
import org.eclipse.lsp4jakarta.jdt.core.Messages;

public class FilterDiagnosticsCollector extends AbstractDiagnosticsCollector {

    public FilterDiagnosticsCollector() {
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
                    IAnnotation webFilterAnnotation = null;

                    for (IAnnotation annotation : allAnnotations) {
                        if (isMatchedJavaElement(type, annotation.getElementName(),
                                ServletConstants.WEBFILTER_FQ_NAME)) {
                            webFilterAnnotation = annotation;
                        }
                    }

                    String[] interfaces = { ServletConstants.FILTER_FQ_NAME };
                    boolean isFilterImplemented = doesImplementInterfaces(type, interfaces);

                    if (webFilterAnnotation != null && !isFilterImplemented) {
                        diagnostics.add(createDiagnostic(type, unit,
                                Messages.getMessage("WebFilterMustImplement"),
                                ServletConstants.DIAGNOSTIC_CODE_FILTER, null, DiagnosticSeverity.Error));
                    }

                    /* URL pattern diagnostic check */
                    if (webFilterAnnotation != null) {
                        IMemberValuePair[] memberValues = webFilterAnnotation.getMemberValuePairs();

                        boolean isUrlpatternSpecified = false;
                        boolean isServletNamesSpecified = false;
                        boolean isValueSpecified = false;
                        for (IMemberValuePair mv : memberValues) {
                            if (mv.getMemberName().equals(ServletConstants.URL_PATTERNS)) {
                                isUrlpatternSpecified = true;
                                continue;
                            }
                            if (mv.getMemberName().equals(ServletConstants.SERVLET_NAMES)) {
                                isServletNamesSpecified = true;
                                continue;
                            }
                            if (mv.getMemberName().equals(ServletConstants.VALUE)) {
                                isValueSpecified = true;
                            }
                        }
                        if (!isUrlpatternSpecified && !isValueSpecified && !isServletNamesSpecified) {
                            diagnostics.add(createDiagnostic(webFilterAnnotation, unit,
                                    Messages.getMessage("WebFilterMustDefine"),
                                    ServletConstants.DIAGNOSTIC_CODE_FILTER_MISSING_ATTRIBUTE, null,
                                    DiagnosticSeverity.Error));
                        }
                        if (isUrlpatternSpecified && isValueSpecified) {
                            diagnostics.add(createDiagnostic(webFilterAnnotation, unit,
                                    Messages.getMessage("WebFilterCannotHaveBoth"),
                                    ServletConstants.DIAGNOSTIC_CODE_FILTER_DUPLICATE_ATTRIBUTES, null,
                                    DiagnosticSeverity.Error));
                        }
                    }
                }
            } catch (JavaModelException e) {
                JakartaCorePlugin.logException("Cannot calculate diagnostics", e);
            }
        }
    }
}
