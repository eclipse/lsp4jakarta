package org.jakarta.jdt;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMemberValuePair;
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

					String typeExtension = type.getSuperclassName();
					if ((typeExtension != null) && typeExtension.equals(ServletConstants.HTTP_SERVLET)) {
						isHttpServletExtended = true;
					}

					if (isWebServletAnnotated && !isHttpServletExtended) {
						diagnostics.add(new Diagnostic(range, "Classes annotated with @WebServlet must extend the HttpServlet class."));
					}
					
					/* URL pattern diagnostic check */
					if(WebServletAnnotation != null) {
						IMemberValuePair[] memberValues = WebServletAnnotation.getMemberValuePairs();
						
						boolean isUrlpatternSpecified = false;
						boolean isValueSpecified = false;
						for (IMemberValuePair mv : memberValues) {
							if(mv.getMemberName().equals(ServletConstants.URL_PATTERNS)) {
								isUrlpatternSpecified = true;
								continue;
							}
							if(mv.getMemberName().equals(ServletConstants.VALUE)) {
								isValueSpecified = true;
							}
							
						}
						
						if (!isUrlpatternSpecified && !isValueSpecified) {
							diagnostics.add(new Diagnostic(range, "The annotated servlet MUST specify at least one url pattern to be deployed."));
						}
						if (isUrlpatternSpecified && isValueSpecified) {
							diagnostics.add(new Diagnostic(range, "It is illegal to have both value and urlPatterns attribute used together on the same annotation."));
						}
						
					}
					
					
					
				}
			} catch (JavaModelException e) {
				Activator.logException("Cannot calculate diagnostics", e);
			}
		}
	}
}