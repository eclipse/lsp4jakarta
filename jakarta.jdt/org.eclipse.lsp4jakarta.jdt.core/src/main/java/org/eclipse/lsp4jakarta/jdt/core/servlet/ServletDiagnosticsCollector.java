/*******************************************************************************
* Copyright (c) 2020 IBM Corporation, Pengyu Xiong and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0.
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     IBM Corporation, Pengyu Xiong - initial API and implementation
*******************************************************************************/

package org.eclipse.lsp4jakarta.jdt.core.servlet;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4jakarta.jdt.core.DiagnosticsCollector;
import org.eclipse.lsp4jakarta.jdt.core.JDTUtils;
import org.eclipse.lsp4jakarta.jdt.core.JakartaCorePlugin;
import org.eclipse.lsp4jakarta.jdt.core.TypeHierarchyUtils;

import java.util.List;

/**
 * 
 * jararta.annotation Diagnostics
 * 
 * <li>Diagnostic 1: Class annotated with @WebServlet does not extend the HttpServlet class.</li>
 * <li>Diagnostic 2: @WebServlet missing 'urlPatterns' and 'value' attribute (one must be specified).</li>
 * <li>Diagnostic 3: @WebServlet has both 'urlPatterns' and 'value' attributes specified.</li>
 *
 * @see https://jakarta.ee/specifications/servlet/5.0/jakarta-servlet-spec-5.0.html#webservlet
 *
 */
public class ServletDiagnosticsCollector implements DiagnosticsCollector {
    private ISourceRange nameRange;

    public ServletDiagnosticsCollector() {
    }

    @Override
    public void completeDiagnostic(Diagnostic diagnostic) {
        diagnostic.setSource(ServletConstants.DIAGNOSTIC_SOURCE);
        diagnostic.setSeverity(ServletConstants.SEVERITY);
    }

    @Override
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

                    boolean isWebServletAnnotated = false;
                    boolean isHttpServletExtended = false;

                    IAnnotation WebServletAnnotation = null;
                    for (IAnnotation annotation : allAnnotations) {
                        if (annotation.getElementName().equals(ServletConstants.WEB_SERVLET)) {
                            isWebServletAnnotated = true;
                            WebServletAnnotation = annotation;
                            break;
                        }
                    }
                    
                    if (!isWebServletAnnotated) {
                        continue;
                    }

                    try {
                        int r = TypeHierarchyUtils.doesITypeHaveSuperType(type, ServletConstants.HTTP_SERVLET);
                        // If the type hierarchy is complete, consider HttpServlet extended.
                        isHttpServletExtended = r >= 0;
                    } catch (CoreException e) {
                        JakartaCorePlugin.logException("Cannot check type hierarchy", e);
                    }

                    if (!isHttpServletExtended) {
                        diagnostic = new Diagnostic(range,
                                "Annotated classes with @WebServlet must extend the HttpServlet class.");
                        diagnostic.setCode(ServletConstants.DIAGNOSTIC_CODE);
                        diagnostics.add(diagnostic);
                    }

                    /* URL pattern diagnostic check */
                    if (WebServletAnnotation != null) {
                        IMemberValuePair[] memberValues = WebServletAnnotation.getMemberValuePairs();

                        boolean isUrlpatternSpecified = false;
                        boolean isValueSpecified = false;
                        for (IMemberValuePair mv : memberValues) {
                            if (mv.getMemberName().equals(ServletConstants.URL_PATTERNS)) {
                                isUrlpatternSpecified = true;
                                continue;
                            }
                            if (mv.getMemberName().equals(ServletConstants.VALUE)) {
                                isValueSpecified = true;
                            }

                        }
                        ISourceRange annotationNameRange = JDTUtils.getNameRange(WebServletAnnotation);
                        Range annotationrange = JDTUtils.toRange(unit, annotationNameRange.getOffset(),
                                annotationNameRange.getLength());

                        if (!isUrlpatternSpecified && !isValueSpecified) {
                            diagnostic = new Diagnostic(annotationrange,
                                    "The annotation @WebServlet must define the attribute urlPatterns or the attribute value.");
                            completeDiagnostic(diagnostic);
                            diagnostic.setCode(ServletConstants.DIAGNOSTIC_CODE_MISSING_ATTRIBUTE);
                            diagnostics.add(diagnostic);
                        }
                        if (isUrlpatternSpecified && isValueSpecified) {
                            diagnostic = new Diagnostic(annotationrange,
                                    "The annotation @WebServlet cannot have both the 'value' and 'urlPatterns' attributes specified at once.");
                            completeDiagnostic(diagnostic);
                            diagnostic.setCode(ServletConstants.DIAGNOSTIC_CODE_DUPLICATE_ATTRIBUTES);
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