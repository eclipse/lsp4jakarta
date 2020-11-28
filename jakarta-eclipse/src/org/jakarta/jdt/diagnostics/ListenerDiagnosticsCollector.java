package org.jakarta.jdt.diagnostics;

import java.util.List;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Range;
import org.jakarta.jdt.JDTUtils;
import org.jakarta.jdt.ServletConstants;
import org.jakarta.lsp4e.Activator;

public class ListenerDiagnosticsCollector implements DiagnosticsCollector {
	
	
	public ListenerDiagnosticsCollector() {

	}

	public void completeDiagnostic(Diagnostic diagnostic) {
		diagnostic.setSource(ServletConstants.DIAGNOSTIC_SOURCE);
		diagnostic.setCode(ServletConstants.DIAGNOSTIC_CODE_LISTENER);
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

					boolean isWebListenerAnnotated = false;
					boolean isWebListenerInterfaceImplemented = false;

					for (IAnnotation annotation : allAnnotations) {
						if (annotation.getElementName().equals(ServletConstants.WEB_LISTENER)) {
							isWebListenerAnnotated = true;
							break;
						}
					}
					String typeExtension = type.getSuperclassName();

					String[] implementedInterfaces = type.getSuperInterfaceNames();

					for (String in : implementedInterfaces) {

						if (in.equals(ServletConstants.SERVLET_CONTEXT_LISTENER)
								|| in.equals(ServletConstants.SERVLET_CONTEXT_ATTRIBUTE_LISTENER)
								|| in.equals(ServletConstants.SERVLET_REQUEST_LISTENER)
								|| in.equals(ServletConstants.SERVLET_REQUEST_ATTRIBUTE_LISTENER)
								|| in.equals(ServletConstants.HTTP_SESSION_LISTENER)
								|| in.equals(ServletConstants.HTTP_SESSION_ATTRIBUTE_LISTENER)
								|| in.equals(ServletConstants.HTTP_SESSION_ID_LISTENER)) {

							isWebListenerInterfaceImplemented = true;
							break;
						}
					}

					if (isWebListenerAnnotated && !isWebListenerInterfaceImplemented) {
						ISourceRange nameRange = JDTUtils.getNameRange(type);
						Range range = JDTUtils.toRange(unit, nameRange.getOffset(), nameRange.getLength());
						diagnostic = new Diagnostic(range, "Classes annotated with @WebListener must implement "
								+ "must implement one or more of the ServletContextListener, ServletContextAttributeListener,"
								+ " ServletRequestListener, ServletRequestAttributeListener, HttpSessionListener,"
								+ " HttpSessionAttributeListener, or HttpSessionIdListener interfaces.");
						completeDiagnostic(diagnostic);
						diagnostics.add(diagnostic);
					}
				}
			} catch (JavaModelException e) {
				Activator.logException("Cannot calculate diagnostics", e);
			}
		}
	}

}
