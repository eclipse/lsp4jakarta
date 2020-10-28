package org.jakarta.jdt;

import java.util.List;

import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.jdt.core.ICompilationUnit;


public interface DiagnosticsCollector {
	public void collectDiagnostics(ICompilationUnit unit, List<Diagnostic> diagnostics);
}
