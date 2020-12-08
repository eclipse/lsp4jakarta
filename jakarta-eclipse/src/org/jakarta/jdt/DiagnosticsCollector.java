package org.jakarta.jdt;

import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.lsp4j.Diagnostic;

public interface DiagnosticsCollector {
    public void completeDiagnostic(Diagnostic diagnostic);

    public void collectDiagnostics(ICompilationUnit unit, List<Diagnostic> diagnostics);
}
