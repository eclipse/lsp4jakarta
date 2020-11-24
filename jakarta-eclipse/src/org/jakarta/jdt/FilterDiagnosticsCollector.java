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

public class FilterDiagnosticsCollector implements DiagnosticsCollector {
	
	
	
	public FilterDiagnosticsCollector() {
		
	}
	
	public void collectDiagnostics(ICompilationUnit unit, List<Diagnostic> diagnostics) {
		if (unit != null) {
			IType[] alltypes;
			IAnnotation[] allAnnotations;
		
			try {
				alltypes = unit.getAllTypes();
				for (IType type : alltypes) {
					allAnnotations = type.getAnnotations();
					
					boolean isWebFilterAnnotated = false;
					boolean isFilterImplemented = false;
				
					
					for (IAnnotation annotation : allAnnotations) {
						if (annotation.getElementName().equals(ServletConstants.WEBFILTER)) {
							isWebFilterAnnotated = true;
						}
					}

					String typeExtension = type.getSuperclassName();
					
					String[] implementedInterfaces = type.getSuperInterfaceNames();
					
					for(String in: implementedInterfaces) {
						if (in.equals(ServletConstants.FILTER)) {
							isFilterImplemented = true;
						}
					}


					
					if (isWebFilterAnnotated && !isFilterImplemented) {
						ISourceRange nameRange = JDTUtils.getNameRange(type);
						Range range = JDTUtils.toRange(unit, nameRange.getOffset(), nameRange.getLength());
						diagnostics.add(new Diagnostic(range, "Classes annotated with @WebFilter must implement the Filter interface."));
					}
					
				}
			} catch (JavaModelException e) {
				Activator.logException("Cannot calculate diagnostics", e);
			}
		}
	}

}
