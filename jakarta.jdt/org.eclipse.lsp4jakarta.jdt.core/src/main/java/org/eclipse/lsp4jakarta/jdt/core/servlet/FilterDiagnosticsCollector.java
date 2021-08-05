/*******************************************************************************
* Copyright (c) 2020 IBM Corporation, Reza Akhavan and others.
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

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4jakarta.jdt.core.DiagnosticsCollector;
import org.eclipse.lsp4jakarta.jdt.core.JDTUtils;
import org.eclipse.lsp4jakarta.jdt.core.JakartaCorePlugin;

import java.util.List;

public class FilterDiagnosticsCollector implements DiagnosticsCollector {

    public FilterDiagnosticsCollector() {

    }

    public void completeDiagnostic(Diagnostic diagnostic) {
        diagnostic.setSource(ServletConstants.DIAGNOSTIC_SOURCE);
        diagnostic.setSeverity(ServletConstants.SEVERITY);
    }

    public void collectDiagnostics(ICompilationUnit unit, List<Diagnostic> diagnostics) {
        Diagnostic diagnostic;
        if (unit != null) {
            IType[] alltypes;
            IAnnotation[] allAnnotations;

            try {
                alltypes = unit.getAllTypes();
                for (IType type : alltypes) {
                    allAnnotations = type.getAnnotations();

                    ISourceRange nameRange = JDTUtils.getNameRange(type);
                    Range range = JDTUtils.toRange(unit, nameRange.getOffset(), nameRange.getLength());

                    boolean isWebFilterAnnotated = false;
                    boolean isFilterImplemented = false;

                    IAnnotation WebFilterAnnotation = null;
                    for (IAnnotation annotation : allAnnotations) {
                        if (annotation.getElementName().equals(ServletConstants.WEBFILTER)) {
                            isWebFilterAnnotated = true;
                            WebFilterAnnotation = annotation;
                        }
                    }

                    String typeExtension = type.getSuperclassName();

                    String[] implementedInterfaces = type.getSuperInterfaceNames();

                    for (String in : implementedInterfaces) {
                        if (in.equals(ServletConstants.FILTER)) {
                            isFilterImplemented = true;
                        }
                    }

                    if (isWebFilterAnnotated && !isFilterImplemented) {
                        diagnostic = new Diagnostic(range,
                                "Classes annotated with @WebFilter must implement the Filter interface.");
                        completeDiagnostic(diagnostic);
                        diagnostic.setCode(ServletConstants.DIAGNOSTIC_CODE_FILTER);
                        diagnostics.add(diagnostic);
                    }

                    /* URL pattern diagnostic check */
                    if (WebFilterAnnotation != null) {
                        IMemberValuePair[] memberValues = WebFilterAnnotation.getMemberValuePairs();

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
                        ISourceRange annotationNameRange = JDTUtils.getNameRange(WebFilterAnnotation);
                        Range annotationrange = JDTUtils.toRange(unit, annotationNameRange.getOffset(),
                                annotationNameRange.getLength());

                        if (!isUrlpatternSpecified && !isValueSpecified && !isServletNamesSpecified) {
                            diagnostic = new Diagnostic(annotationrange,
                                    "The 'urlPatterns' attribute, 'servletNames' attribute or the 'value' attribute of the WebFilter annotation MUST be specified.");
                            completeDiagnostic(diagnostic);
                            diagnostic.setCode(ServletConstants.DIAGNOSTIC_CODE_FILTER_MISSING_ATTRIBUTE);
                            diagnostics.add(diagnostic);
                        }
                        if (isUrlpatternSpecified && isValueSpecified) {
                            diagnostic = new Diagnostic(annotationrange,
                                    "The WebFilter annotation cannot have both the 'value' and 'urlPatterns' attributes specified at once.");
                            completeDiagnostic(diagnostic);
                            diagnostic.setCode(ServletConstants.DIAGNOSTIC_CODE_FILTER_DUPLICATE_ATTRIBUTES);
                            diagnostics.add(diagnostic);
                        }

                    }
                }
            } catch (JavaModelException e) {
            	JakartaCorePlugin.logException("Cannot calculate diagnostics", e);
            }
        }
    }

}
