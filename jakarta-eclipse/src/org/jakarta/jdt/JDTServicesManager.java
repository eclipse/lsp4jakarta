package org.jakarta.jdt;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.Range;
import org.jakarta.jdt.JDTUtils;
import org.jakarta.lsp4e.Activator;

import org.jakarta.jdt.DiagnosticsCollector;
import org.jakarta.jdt.ServletDiagnosticsCollector;

import io.microshed.jakartals.commons.JakartaDiagnosticsParams;

/**
 * JDT service manager.
 *
 */

public class JDTServicesManager {
	private List<DiagnosticsCollector> diagnosticsCollectors = new ArrayList<>();
	public JDTServicesManager() {
		diagnosticsCollectors.add(new ServletDiagnosticsCollector());
	}

	public List<PublishDiagnosticsParams> getJavaDiagnostics(JakartaDiagnosticsParams javaParams) {
		List<PublishDiagnosticsParams> publishDiagnostics = new ArrayList<PublishDiagnosticsParams>();
		List<Diagnostic> diagnostics = new ArrayList<>();
		List<String> uris = javaParams.getUris();
		
		for (String uri : uris) {
			
			URI u = JDTUtils.toURI(uri);

			ICompilationUnit unit = JDTUtils.resolveCompilationUnit(u);
			for (DiagnosticsCollector d : diagnosticsCollectors) {
				d.collectDiagnostics(unit, diagnostics);
			}
			
			PublishDiagnosticsParams publishDiagnostic = new PublishDiagnosticsParams(uri, diagnostics);
			publishDiagnostics.add(publishDiagnostic);
		}
		return publishDiagnostics;
	}
}
