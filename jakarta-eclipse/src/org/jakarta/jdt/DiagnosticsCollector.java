package org.jakarta.jdt;

import java.util.List;

import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.ISourceRange;


public interface DiagnosticsCollector {
	public void collectDiagnostics(ICompilationUnit unit, List<Diagnostic> diagnostics);
}
