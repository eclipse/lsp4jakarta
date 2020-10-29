package org.jakarta.jdt;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.JavaProject;
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
	
	/**
 	 * @author ankushsharma
 	 * @brief Gets all snippet contexts that exist in the current project classpath
 	 * @param uri - String representing file from which to derive project classpath
 	 * @param snippetContext - get all the context fields from the snippets and check if they exist in this method
 	 * @return List<String>
 	 */
 	public List<String> getExistingContextsFromClassPath(String uri, List<String> snippetContexts) {
 		// Initialize the list that will hold the classpath
 		List<String> classpath = new ArrayList<>();
 		// Convert URI into a compilation unit
 		ICompilationUnit unit = JDTUtils.resolveCompilationUnit(JDTUtils.toURI(uri));
 		// Get Java Project
 		JavaProject project = (JavaProject) unit.getJavaProject();
 		// Get Java Project
 		if (project != null) {
 			snippetContexts.forEach(ctx -> {
 				IType classPathctx = null;
 				try {
 					classPathctx = project.findType(ctx);
 					if (classPathctx != null) {
 						classpath.add(ctx);
 					} else {
 						classpath.add(null);
 					}
 				} catch (JavaModelException e) {
 					Activator.logException("Failed to retrieve projectContext from JDT...", e);
 				}
 			});
 		} else {
 			// Populate the Array with nulls up to length of snippetContext
 			snippetContexts.forEach(ctx -> {
 				classpath.add(null);
 			});
 		}
 		return classpath;
 	}
}
