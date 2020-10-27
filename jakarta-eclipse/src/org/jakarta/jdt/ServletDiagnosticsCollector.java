package org.jakarta.jdt;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Range;
import org.jakarta.lsp4e.Activator;

import java.util.List;

public class ServletDiagnosticsCollector implements DiagnosticsCollector{
	
	
	
	public ServletDiagnosticsCollector() {
		
	}
	
	public void collectDiagnostics(ICompilationUnit unit, List<Diagnostic> diagnostics) {
		if (unit != null) {

			IType[] alltypes;
			IAnnotation[] allAnnotations;
		
			try {
				alltypes = unit.getAllTypes();
				for (IType type : alltypes) {
					allAnnotations = type.getAnnotations();
					
					
					boolean isWebServletAnnotated = false;
					boolean isHttpServletExtended = false;
					
					boolean isWebFilterAnnotated = false;
					boolean isFilterImplemented = false;
					
					boolean isWebListenerAnnotated = false;
					boolean isWebListenerInterfaceImplemented = false;
					
					for (IAnnotation annotation : allAnnotations) {
						if (annotation.getElementName().equals(ServletConstants.WEB_SERVLET)) {
							isWebServletAnnotated = true;
							break;
						}
						if (annotation.getElementName() == "WebFilter") {
							isWebFilterAnnotated = true;
						}
						if (annotation.getElementName() == "WebListener") {
							isWebListenerAnnotated = true;
						}
					}

					String typeExtension = type.getSuperclassName();
					if ((typeExtension != null) && typeExtension.equals(ServletConstants.HTTP_SERVLET)) {
						isHttpServletExtended = true;
					}
					
					String[] implementedInterfaces = type.getSuperInterfaceNames();
					
					for(String in: implementedInterfaces) {
						if (in.compareTo("Filter") == 0) {
							isFilterImplemented = true;
						}
						
						if (in.compareTo("Filter") == 0) {
							isFilterImplemented = true;
						}
						
						if (in.compareTo("ServletContextListener") == 0) {
							isWebListenerInterfaceImplemented = true;
						}
						if (in.compareTo("ServletContextAttributeListener") == 0) {
							isWebListenerInterfaceImplemented = true;
						}
						if (in.compareTo("ServletRequestListener") == 0) {
							isWebListenerInterfaceImplemented = true;
						}
						if (in.compareTo("ServletRequestAttributeListener") == 0) {
							isWebListenerInterfaceImplemented = true;
						}
						if (in.compareTo("HttpSessionAttributeListener") == 0) {
							isWebListenerInterfaceImplemented = true;
						}
						if (in.compareTo("HttpSessionAttributeListener") == 0) {
							isWebListenerInterfaceImplemented = true;
						}
						if (in.compareTo("HttpSessionIdListener") == 0) {
							isWebListenerInterfaceImplemented = true;
						}
					}


					if (isWebServletAnnotated && !isHttpServletExtended) {
						ISourceRange nameRange = JDTUtils.getNameRange(type);
						Range range = JDTUtils.toRange(unit, nameRange.getOffset(), nameRange.getLength());
						diagnostics.add(new Diagnostic(range, "Classes annotated with @WebServlet must extend the HttpServlet class."));
					}
					
					if (isWebFilterAnnotated && !isFilterImplemented) {
						ISourceRange nameRange = JDTUtils.getNameRange(type);
						Range range = JDTUtils.toRange(unit, nameRange.getOffset(), nameRange.getLength());
						diagnostics.add(new Diagnostic(range, "Classes annotated with @WebFilter must implement the Filter interface."));
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