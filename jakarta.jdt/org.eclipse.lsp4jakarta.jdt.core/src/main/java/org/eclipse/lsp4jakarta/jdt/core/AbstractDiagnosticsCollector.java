/*******************************************************************************
* Copyright (c) 2020 Red Hat Inc. and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
* which is available at https://www.apache.org/licenses/LICENSE-2.0.
*
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
*
* Contributors:
*     Red Hat Inc. - initial API and implementation
*******************************************************************************/
package org.eclipse.lsp4jakarta.jdt.core;

import java.util.List;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IImportContainer;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.ImportContainerInfo;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4jakarta.jdt.core.annotations.AnnotationConstants;

/**
 *
 * Abstract class for collecting Java diagnostics.
 *
 */
@SuppressWarnings("restriction")
public abstract class AbstractDiagnosticsCollector implements DiagnosticsCollector {
		
    /**
     * Constructor
     */
    public AbstractDiagnosticsCollector() {
		super();
	}

	public void completeDiagnostic(Diagnostic diagnostic) {
    	this.completeDiagnostic(diagnostic, null, AnnotationConstants.ERROR);
    }
	    
    public void completeDiagnostic(Diagnostic diagnostic, String code) {
    	this.completeDiagnostic(diagnostic, code, AnnotationConstants.ERROR);
    }
    
    public void completeDiagnostic(Diagnostic diagnostic, String code, DiagnosticSeverity severity) {
        diagnostic.setSource(AnnotationConstants.DIAGNOSTIC_SOURCE);
        if (code != null)
        	diagnostic.setCode(code);
        diagnostic.setSeverity(severity);
    }

    public void collectDiagnostics(ICompilationUnit unit, List<Diagnostic> diagnostics) {
    }
    
    protected static boolean isMatchedAnnotation(ICompilationUnit unit, IAnnotation annotation, String annotationFQName) throws JavaModelException {
    	String elementName = annotation.getElementName();
    	if (annotationFQName.endsWith(elementName) && unit != null) {
			// For performance reason, we check if the import of annotation name is
			// declared
	    	if (isImportedAnnotation(unit, annotation, annotationFQName) == true) {
	    		return true;
	    	}
	    	// only check fully qualified annotations 
	    	if (annotationFQName.equals(elementName)) {
	    		IJavaElement parent = annotation.getParent();
		        IType declaringType = (parent instanceof IType) ? (IType) parent : 
		        	((parent instanceof IMember) ? ((IMember) parent).getDeclaringType() : null) ;
		    	if (declaringType != null) { 
					String[][] fqName = declaringType.resolveType(elementName);	// the call could be expensive
					if (fqName != null && fqName.length == 1) {
						return annotationFQName.equals(JavaModelUtil.concatenateName(fqName[0][0], fqName[0][1]));
					}
		    	}
	    	}
    	}
    	return false;
    }
    
    private static boolean isImportedAnnotation(ICompilationUnit unit, IAnnotation annotation, String annotationFQName) throws JavaModelException {
		IImportContainer container = unit.getImportContainer();
		if (container == null) {
			return false;
		}
		
		// The following code uses JDT internal class and looks like
		// ICompilationUnit#getImports()
		// To avoid creating an array of IImportDeclaration, we do the following code:

		JavaModelManager manager = JavaModelManager.getJavaModelManager();
		Object info = manager.getInfo(container);
		if (info == null) {
			if (manager.getInfo(unit) != null) {
				// CU was opened, but no import container, then no imports
				// return NO_IMPORTS;
				return false;
			} else {
				try {
					unit.open(null);
				} catch (JavaModelException e) {
					e.printStackTrace();
				} // force opening of CU
				info = manager.getInfo(container);
				if (info == null)
					// after opening, if no import container, then no imports
					// return NO_IMPORTS;
					return false;
			}
		}
		IJavaElement[] elements = ((ImportContainerInfo) info).getChildren();
		for (IJavaElement child : elements) {
			IImportDeclaration importDeclaration = (IImportDeclaration) child;
			if (importDeclaration.isOnDemand()) {
				String fqn = importDeclaration.getElementName();
				String qualifier = fqn.substring(0, fqn.lastIndexOf('.'));
				if (qualifier.equals(annotationFQName.substring(0, annotationFQName.lastIndexOf('.')))) {
					return true;
				}
			} else if (importDeclaration.getElementName().equals(annotationFQName)) {
				return true;
			}
		}
		return false;
    }
}
