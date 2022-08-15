/******************************************************************************* 
 * Copyright (c) 2022 IBM Corporation and others. 
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v. 2.0 which is available at 
 * http://www.eclipse.org/legal/epl-2.0. 
 * 
 * SPDX-License-Identifier: EPL-2.0 
 * 
 * Contributors: 
 *     IBM Corporation - initial API and implementation 
 *******************************************************************************/ 
package org.eclipse.lsp4jakarta.jdt.core;

import java.util.List;
import java.util.stream.Stream;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IImportContainer;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.ImportContainerInfo;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Range;

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

    protected String getDiagnosticSource() {
    	return null;
    }
    
	public void completeDiagnostic(Diagnostic diagnostic) {
    	this.completeDiagnostic(diagnostic, null, DiagnosticSeverity.Error);
    }
	    
    public void completeDiagnostic(Diagnostic diagnostic, String code) {
    	this.completeDiagnostic(diagnostic, code, DiagnosticSeverity.Error);
    }
    
    public void completeDiagnostic(Diagnostic diagnostic, String code, DiagnosticSeverity severity) {
    	String source = getDiagnosticSource();
    	if (source != null)
    		diagnostic.setSource(source);
        if (code != null)
        	diagnostic.setCode(code);
        diagnostic.setSeverity(severity);
    }
    
	/**
	 * Creates and returns a new diagnostic.
	 *
	 * @param el 			Java element
	 * @param unit 			compilation unit of Java class
	 * @param msg	 		diagnostic message
	 * @param code	 		diagnostic code
	 * @param data	 		diagnostic data
	 * @param severity		diagnostic severity
	 * @return new Diagnostic object.
	 */    
    protected Diagnostic createDiagnostic(IJavaElement el, ICompilationUnit unit, String msg, String code, Object data, DiagnosticSeverity severity) throws JavaModelException {
        ISourceRange nameRange = JDTUtils.getNameRange(el);
        Range range = JDTUtils.toRange(unit, nameRange.getOffset(), nameRange.getLength());
        Diagnostic diagnostic = new Diagnostic(range, msg);
        if (data != null)
        	diagnostic.setData(data);
    	String source = getDiagnosticSource();
    	if (source != null)
    		diagnostic.setSource(source);
        if (code != null)
        	diagnostic.setCode(code);
        diagnostic.setSeverity(severity);
        return diagnostic;
    }    
    
	/**
	 * Returns diagnostics for the given compilation unit.
	 *
	 * @param unit 			compilation unit of Java class
	 * @param diagnostics 	diagnostics for the given compilation unit to return
	 */
    public void collectDiagnostics(ICompilationUnit unit, List<Diagnostic> diagnostics) {
    }
    
	/**
	 * Returns true if the given annotation matches the given annotation name and
	 * false otherwise.
	 *
	 * @param unit    	 		compilation unit of Java class.
	 * @param annotation    	given annotation object.
	 * @param annotationFQName	the fully qualified annotation name.
	 * @return true if the given annotation matches the given annotation name and false otherwise.
	 */
    protected static boolean isMatchedAnnotation(ICompilationUnit unit, IAnnotation annotation, String annotationFQName) throws JavaModelException {
    	String elementName = annotation.getElementName();
    	if (annotationFQName.endsWith(elementName) && unit != null) {
			// For performance reason, we check if the import of annotation name is
			// declared
	    	if (isImportedJavaElement(unit, annotationFQName) == true)
	    		return true;
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
    
	/**
	 * Returns true if the java element name matches the given fully qualified java element name and
	 * false otherwise.
	 *
	 * @param unit    	 		compilation unit of Java class.
	 * @param annotation    	given annotation object.
	 * @param annotationFQName	the fully qualified annotation name.
	 * @return true if the java element name matches the given fully qualified java element name and false otherwise.
	 */
    protected static boolean isMatchedJavaElement(IType type, String javaElementName, String javaElementFQName) throws JavaModelException {    	
    	if (javaElementFQName.endsWith(javaElementName)) {
			// For performance reason, we check if the import of annotation name is
			// declared
	    	if (isImportedJavaElement(type.getCompilationUnit(), javaElementFQName) == true)
	    		return true;
	    	// only check fully qualified java element 
	    	if (javaElementFQName.equals(javaElementName)) {
				String[][] fqName = type.resolveType(javaElementName);	// the call could be expensive
				if (fqName != null && fqName.length == 1) {
					return javaElementFQName.equals(JavaModelUtil.concatenateName(fqName[0][0], fqName[0][1]));
				}
	    	}
    	}
    	return false;
    }    
        
	/**
	 * Returns true if the given Java class imports the given Java element and
	 * false otherwise.
	 *
	 * @param type    	 			Java class.
	 * @param javaElementFQName		given Java element fully qualified name.
	 * @return true if the Java class imports the given Java element and false otherwise.
	 */
    protected static boolean isImportedJavaElement(ICompilationUnit unit, String javaElementFQName) throws JavaModelException {
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
				if (qualifier.equals(javaElementFQName.substring(0, javaElementFQName.lastIndexOf('.')))) {
					return true;
				}
			} else if (importDeclaration.getElementName().equals(javaElementFQName)) {
				return true;
			}
		}
		return false;
    }
    
	/**
	 * Returns true if the given Java class imports one of the given Java elements and
	 * false otherwise.
	 *
	 * @param type    	 			Java class.
	 * @param javaElementFQName		given Java element fully qualified names.
	 * @return true if the Java class imports one of the given Java elements and false otherwise.
	 */
    protected static boolean isImportedJavaElement(ICompilationUnit unit, String[] javaElementFQNames) throws JavaModelException {
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
				boolean imports = Stream.of(javaElementFQNames).anyMatch(elementFQName -> {
					return qualifier.equals(elementFQName.substring(0, elementFQName.lastIndexOf('.')));
				});				
				if (imports == true) {
					return true;
				}
			} else {
				String importName = importDeclaration.getElementName(); 
				if (Stream.of(javaElementFQNames).anyMatch(elementFQName -> importName.equals(elementFQName)) == true)
					return true;
			}
		}
		return false;
    }
    
	/**
	 * Returns true if the given Java class implements one of the given interfaces and
	 * false otherwise.
	 *
	 * @param type    	 		Java class.
	 * @param interfaceFQNames 	given interfaces with fully qualified name.
	 * @return true if the Java class implements one of the given interfaces and false otherwise.
	 */
    protected static boolean doesImplementInterfaces(IType type, String[] interfaceFQNames) throws JavaModelException {
    	String[] interfaceNames = type.getSuperInterfaceNames();

    	// should check import statements first for the performance?
  
    	// check super hierarchy
    	if (interfaceNames.length > 0) {	// the type implements interface(s)
	        ITypeHierarchy typeHierarchy = type.newSupertypeHierarchy(new NullProgressMonitor());
	        IType[] interfaces = typeHierarchy.getAllInterfaces();
	        for (IType interfase : interfaces) {
				String fqName = interfase.getFullyQualifiedName(); 
				if (Stream.of(interfaceFQNames).anyMatch(name -> fqName.equals(name)) == true)
					return true;	        	
	        }    			
    	}
        return false;
    }
}
