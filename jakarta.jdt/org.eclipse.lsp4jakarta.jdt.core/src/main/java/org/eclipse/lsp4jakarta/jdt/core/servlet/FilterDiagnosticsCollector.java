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

import java.util.List;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4jakarta.jdt.core.AbstractDiagnosticsCollector;
import org.eclipse.lsp4jakarta.jdt.core.JDTUtils;
import org.eclipse.lsp4jakarta.jdt.core.JakartaCorePlugin;

public class FilterDiagnosticsCollector extends AbstractDiagnosticsCollector {

    public FilterDiagnosticsCollector() {
    	super();
    }

    @Override
	protected String getDiagnosticSource() {
		return ServletConstants.DIAGNOSTIC_SOURCE;
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
                    IAnnotation webFilterAnnotation = null;

                    for (IAnnotation annotation : allAnnotations) {
                        if (isMatchedAnnotation(unit, annotation, ServletConstants.WEBFILTER_FQ_NAME)) {
                            webFilterAnnotation = annotation;
                        }
                    }

                    String[] interfaces = {ServletConstants.FILTER_FQ_NAME};
                    boolean isFilterImplemented = doesImplementInterfaces(type, interfaces);
                    
                    if (webFilterAnnotation != null && !isFilterImplemented) {
                        ISourceRange nameRange = JDTUtils.getNameRange(type);
                        Range range = JDTUtils.toRange(unit, nameRange.getOffset(), nameRange.getLength());                    	
                        diagnostic = new Diagnostic(range, "Annotated classes with @WebFilter must implement the Filter interface.");
                        completeDiagnostic(diagnostic, ServletConstants.DIAGNOSTIC_CODE_FILTER);
                        diagnostics.add(diagnostic);
                    }

                    /* URL pattern diagnostic check */
                    if (webFilterAnnotation != null) {
                        IMemberValuePair[] memberValues = webFilterAnnotation.getMemberValuePairs();

                        boolean isUrlpatternSpecified = false;
//                        boolean isServletNamesSpecified = false;
                        boolean isValueSpecified = false;
                        for (IMemberValuePair mv : memberValues) {
                            if (mv.getMemberName().equals(ServletConstants.URL_PATTERNS)) {
                                isUrlpatternSpecified = true;
                                continue;
                            }
//                            if (mv.getMemberName().equals(ServletConstants.SERVLET_NAMES)) {
//                                isServletNamesSpecified = true;
//                                continue;
//                            }
                            if (mv.getMemberName().equals(ServletConstants.VALUE)) {
                                isValueSpecified = true;
                            }
                        }
                        ISourceRange annotationNameRange = JDTUtils.getNameRange(webFilterAnnotation);
                        Range annotationrange = JDTUtils.toRange(unit, annotationNameRange.getOffset(),
                                annotationNameRange.getLength());

                        if (!isUrlpatternSpecified && !isValueSpecified/* && !isServletNamesSpecified*/) {
                            diagnostic = new Diagnostic(annotationrange, "The annotation @WebServlet must define the attribute 'urlPatterns' or 'value'.");
                            completeDiagnostic(diagnostic, ServletConstants.DIAGNOSTIC_CODE_FILTER_MISSING_ATTRIBUTE);
                            diagnostics.add(diagnostic);
                        }
                        if (isUrlpatternSpecified && isValueSpecified) {
                            diagnostic = new Diagnostic(annotationrange, "The annotation @WebFilter can not have both 'value' and 'urlPatterns' attributes specified at once.");
                            completeDiagnostic(diagnostic, ServletConstants.DIAGNOSTIC_CODE_FILTER_DUPLICATE_ATTRIBUTES);
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
