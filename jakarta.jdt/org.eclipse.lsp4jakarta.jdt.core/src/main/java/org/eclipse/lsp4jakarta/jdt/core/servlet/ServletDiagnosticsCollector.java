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

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IType;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4jakarta.jdt.core.AbstractDiagnosticsCollector;
import org.eclipse.lsp4jakarta.jdt.core.JakartaCorePlugin;
import org.eclipse.lsp4jakarta.jdt.core.TypeHierarchyUtils;

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
public class ServletDiagnosticsCollector extends AbstractDiagnosticsCollector {

    public ServletDiagnosticsCollector() {
    	super();
    }

    @Override
	protected String getDiagnosticSource() {
		return ServletConstants.DIAGNOSTIC_SOURCE;
	}

	@Override
    public void collectDiagnostics(ICompilationUnit unit, List<Diagnostic> diagnostics) {
        if (unit != null) {
            IType[] alltypes;
            IAnnotation[] allAnnotations;

            try {
                alltypes = unit.getAllTypes();
                for (IType type : alltypes) {
                    allAnnotations = type.getAnnotations();

                    IAnnotation webServletAnnotation = null;
                    for (IAnnotation annotation : allAnnotations) {
                        if (isMatchedJavaElement(type, annotation.getElementName(), ServletConstants.WEB_SERVLET_FQ_NAME)) {
                            webServletAnnotation = annotation;
                            break;	// get the first one, the annotation is not repeatable
                        }
                    }
                    
                    if (webServletAnnotation == null) {
                        continue;
                    }

                    // check if the class extends HttpServlet
                    int r = TypeHierarchyUtils.doesITypeHaveSuperType(type, ServletConstants.HTTP_SERVLET);
                    if (r == -1) {
                        diagnostics.add(createDiagnostic(type, unit, 
                        		"Annotated classes with @WebServlet must extend the HttpServlet class.", 
                        		ServletConstants.DIAGNOSTIC_CODE, null, ServletConstants.SEVERITY));
                    } else if (r == 0) {	// unknown super type
                        diagnostics.add(createDiagnostic(type, unit, 
                        		"Annotated classes with @WebServlet should extend the HttpServlet class.", 
                        		ServletConstants.DIAGNOSTIC_CODE, null, ServletConstants.WARNING));                    	
                    }

                    /* URL pattern diagnostic check */
                    if (webServletAnnotation != null) {
                        IMemberValuePair[] memberValues = webServletAnnotation.getMemberValuePairs();

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
                        if (!isUrlpatternSpecified && !isValueSpecified) {
                            diagnostics.add(createDiagnostic(webServletAnnotation, unit, 
                            		"The annotation @WebServlet must define the attribute 'urlPatterns' or 'value'.", 
                            		ServletConstants.DIAGNOSTIC_CODE_MISSING_ATTRIBUTE, null, ServletConstants.SEVERITY));
                        }
                        if (isUrlpatternSpecified && isValueSpecified) {
                            diagnostics.add(createDiagnostic(webServletAnnotation, unit, 
                            		"The annotation @WebServlet cannot have both 'value' and 'urlPatterns' attributes specified at once.", 
                            		ServletConstants.DIAGNOSTIC_CODE_DUPLICATE_ATTRIBUTES, null, ServletConstants.SEVERITY));
                        }
                    }
                }
            } catch (CoreException e) {
                JakartaCorePlugin.logException("Cannot check type hierarchy", e);
            }
        }
    }

}