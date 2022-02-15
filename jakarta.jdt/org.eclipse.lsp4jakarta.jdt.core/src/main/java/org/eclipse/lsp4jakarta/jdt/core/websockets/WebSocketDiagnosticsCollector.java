package org.eclipse.lsp4jakarta.jdt.core.websockets;

import java.util.List;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4jakarta.jdt.core.DiagnosticsCollector;
import org.eclipse.lsp4jakarta.jdt.core.JakartaCorePlugin;



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
		// check methods annotations, then check their params, check that their type is any of the valid options, if it's a string, check that it has the @Params annotation
		// If so, set up the severity to Error, and add a code?
	}
	
	private boolean isWebSocketClass() {
		return false;
	}
}