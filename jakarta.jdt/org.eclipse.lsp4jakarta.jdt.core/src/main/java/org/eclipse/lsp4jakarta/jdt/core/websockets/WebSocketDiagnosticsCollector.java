package org.eclipse.lsp4jakarta.jdt.core.websockets;

import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4jakarta.jdt.core.DiagnosticsCollector;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.IMethod;



public class WebSocketDiagnosticsCollector implements DiagnosticsCollector {
	@Override
	public void completeDiagnostic(Diagnostic diagnostic) {
		diagnostic.setSource();
		diagnostic.setSeverity();
	}

	@Override
	public void collectDiagnostics(ICompilationUnit unit, List<Diagnostic> diagnostics) {
		if (unit != null) {
			IType[] alltypes;
			IField[] allFields;
            IAnnotation[] allFieldAnnotations;
			IMethod[] allMethods;
            IAnnotation[] allMethodAnnotations;

			try {
				alltypes = unit.getAllTypes();
				for (IType type : alltypes) {
					allFields = type.getFields();
				}
			} catch (JavaModelException e) {
				JakartaCorePlugin.logException("Cannot calculate diagnostics", e);
			}
		}
	}

	private void invalidParamsCheck(ICompilationUnit unit, List <Diagnostic> diagnostics, IType type) {
		
	}
	
}