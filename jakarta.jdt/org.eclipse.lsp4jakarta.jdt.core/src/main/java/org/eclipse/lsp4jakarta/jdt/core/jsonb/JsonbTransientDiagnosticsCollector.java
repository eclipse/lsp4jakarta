/*******************************************************************************
* Copyright (c) 2022 IBM Corporation, Adit Rada and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0.
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     IBM Corporation, Adit Rada - initial API and implementation
*******************************************************************************/

package org.eclipse.lsp4jakarta.jdt.core.jsonb;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4jakarta.jdt.core.DiagnosticsCollector;
import org.eclipse.lsp4jakarta.jdt.core.JDTUtils;
import org.eclipse.lsp4jakarta.jdt.core.JakartaCorePlugin;


public class JsonbTransientDiagnosticsCollector implements DiagnosticsCollector {

    @Override
    public void completeDiagnostic(Diagnostic diagnostic) {
        diagnostic.setSource(JsonbConstants.DIAGNOSTIC_SOURCE);
        diagnostic.setSeverity(DiagnosticSeverity.Error);
    }
    
    @Override
    public void collectDiagnostics(ICompilationUnit unit, List<Diagnostic> diagnostics) {
        if (Objects.isNull(unit)) {
            return;
        }
        
        try {
        	
        	for (IType type : unit.getAllTypes()) {
        		
        		for (IField field : type.getFields()) {
        			
        			boolean hasJsonbTransient = false;
        			boolean hasOtherJsonbAnnotation = false;
        			
        			for (IAnnotation annotation : field.getAnnotations()) {
        				String annotation_name = annotation.getElementName();
        				if (annotation_name.equals(JsonbConstants.JSONB_TRANSIENT)) {	
        					hasJsonbTransient = true;	
        				} else if (annotation_name.startsWith("Jsonb")) {
        					hasOtherJsonbAnnotation = true;	
        				}
        			}
        			if (hasJsonbTransient && hasOtherJsonbAnnotation) {
        				Diagnostic diagnostic = createDiagnosticBy(unit, field);
        				diagnostics.add(diagnostic);
        			}
        		}
        	}
        	
        } catch (JavaModelException e) {
        	JakartaCorePlugin.logException("Cannot calculate jakarta-jsonb diagnostics", e);
        }
    }

	private Diagnostic createDiagnosticBy(ICompilationUnit unit, IField field) {
		try {
	        ISourceRange sourceRange = JDTUtils.getNameRange(field);
	        Range range = JDTUtils.toRange(unit, sourceRange.getOffset(), sourceRange.getLength());
	        String message = JsonbConstants.ERROR_MESSAGE_JSONB_TRANSIENT;
	        DiagnosticSeverity severity = DiagnosticSeverity.Error; 
	        String source = JsonbConstants.DIAGNOSTIC_SOURCE;
	        String code = JsonbConstants.DIAGNOSTIC_CODE_ANNOTATION;
	        
	        return new Diagnostic(range, message, severity, source, code);
		} catch (JavaModelException e) {
        	JakartaCorePlugin.logException("Cannot create jakarta-jsonb diagnostics", e);
        }
		return null;
	}
}
