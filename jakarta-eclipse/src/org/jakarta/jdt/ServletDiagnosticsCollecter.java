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

public class ServletDiagnosticsCollecter implements DiagnosticsCollecter{
	public ServletDiagnosticsCollecter() {
		
	}
	
	public void collectDiagnostics(ICompilationUnit unit, List<Diagnostic> diagnostics) {
		if (unit != null) {
			// System.out.println("--class name: " + unit.getElementName());
			IType[] alltypes;
			IAnnotation[] allAnnotations;
		
			try {
				alltypes = unit.getAllTypes();
				for (IType type : alltypes) {
					allAnnotations = type.getAnnotations();
					boolean isWebServletAnnotated = false;
					boolean isHttpServletExtended = false;
					for (IAnnotation annotation : allAnnotations) {
						if (annotation.getElementName() == "WebServlet") {
//							System.out.println("--Annotation name: " + annotation.getElementName());
							isWebServletAnnotated = true;
						}
					}

					String typeExtension = type.getSuperclassName();
//					System.out.println("--extension name: " + type.getSuperclassName());
					if ((typeExtension != null) && typeExtension.compareTo("HttpServlet") == 0) {
						isHttpServletExtended = true;
					}

					if (isWebServletAnnotated && !isHttpServletExtended) {
						ISourceRange nameRange = JDTUtils.getNameRange(type);
						Range range = JDTUtils.toRange(unit, nameRange.getOffset(), nameRange.getLength());
						diagnostics.add(new Diagnostic(range, "classes annotated with @WebServlet must extend the HttpServlet class."));
					}
				}
			}catch (JavaModelException e) {
				Activator.logException("Cannot calculate diagnostics", e);
			}
		}
	}
}