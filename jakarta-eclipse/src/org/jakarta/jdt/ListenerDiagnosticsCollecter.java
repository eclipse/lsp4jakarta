package org.jakarta.jdt;

import java.util.List;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Range;
import org.jakarta.lsp4e.Activator;

public class ListenerDiagnosticsCollecter implements DiagnosticsCollecter {
	public ListenerDiagnosticsCollecter() {
		
	}
	
	public void collectDiagnostics(ICompilationUnit unit, List<Diagnostic> diagnostics) {
		if (unit != null) {
			IType[] alltypes;
			IAnnotation[] allAnnotations;
		
			try {
				alltypes = unit.getAllTypes();
				for (IType type : alltypes) {
					allAnnotations = type.getAnnotations();
					
					boolean isWebListenerAnnotated = false;
					boolean isWebListenerInterfaceImplemented = false;
					
					for (IAnnotation annotation : allAnnotations) {
						if (annotation.getElementName() == "WebListener") {
							isWebListenerAnnotated = true;
						}
					}
					String typeExtension = type.getSuperclassName();
					
					String[] implementedInterfaces = type.getSuperInterfaceNames();
					
					for(String in: implementedInterfaces) {
						
						if (in.equals("ServletContextListener")) {
							isWebListenerInterfaceImplemented = true;
						}
						if (in.equals("ServletContextAttributeListener")) {
							isWebListenerInterfaceImplemented = true;
						}
						if (in.equals("ServletRequestListener")) {
							isWebListenerInterfaceImplemented = true;
						}
						if (in.equals("ServletRequestAttributeListener")) {
							isWebListenerInterfaceImplemented = true;
						}
						if (in.equals("HttpSessionAttributeListener")) {
							isWebListenerInterfaceImplemented = true;
						}
						if (in.equals("HttpSessionAttributeListener")) {
							isWebListenerInterfaceImplemented = true;
						}
						if (in.equals("HttpSessionIdListener")) {
							isWebListenerInterfaceImplemented = true;
						}
					}
					
					if (isWebListenerAnnotated && !isWebListenerInterfaceImplemented) {
						ISourceRange nameRange = JDTUtils.getNameRange(type);
						Range range = JDTUtils.toRange(unit, nameRange.getOffset(), nameRange.getLength());
						diagnostics.add(new Diagnostic(range, "Classes annotated with @WebListener must implement "
								+ "must implement one or more of the ServletContextListener, ServletContextAttributeListener,"
								+ " ServletRequestListener, ServletRequestAttributeListener, HttpSessionListener,"
								+ " HttpSessionAttributeListener, or HttpSessionIdListener interfaces."));
					}
				}
			} catch (JavaModelException e) {
				Activator.logException("Cannot calculate diagnostics", e);
			}
		}
	}

}
