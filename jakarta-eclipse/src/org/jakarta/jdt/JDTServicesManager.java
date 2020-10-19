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

import org.jakarta.jdt.DiagnosticsCollecter;
import org.jakarta.jdt.ServletDiagnosticsCollecter;

import io.microshed.jakartals.commons.JakartaDiagnosticsParams;

/**
 * JDT service manager.
 *
 */

public class JDTServicesManager {
	private List<DiagnosticsCollecter> diagnosticsCollectors = new ArrayList<>();
	public JDTServicesManager() {
		diagnosticsCollectors.add(new ServletDiagnosticsCollecter());
	}

	public List<PublishDiagnosticsParams> getJavaDiagnostics(JakartaDiagnosticsParams javaParams) {
		List<PublishDiagnosticsParams> publishDiagnostics = new ArrayList<PublishDiagnosticsParams>();
		List<Diagnostic> diagnostics = new ArrayList<>();
		List<String> uris = javaParams.getUris();
		
		for (String uri : uris) {
			
			URI u = JDTUtils.toURI(uri);

			ICompilationUnit unit = JDTUtils.resolveCompilationUnit(u);
			for (DiagnosticsCollecter d : diagnosticsCollectors) {
				d.collectDiagnostics(unit, diagnostics);
			}
			
			
			if (unit != null) {
				// System.out.println("--class name: " + unit.getElementName());
				IType[] alltypes;
				try {
					alltypes = unit.getAllTypes();
					for (IType type : alltypes) {

						IMethod[] methods = type.getMethods();
						for (IMethod method : methods) {
							// System.out.println("--Method name: " + method.getElementName());
							// nameRange only has offset and the length of method here
							ISourceRange nameRange = JDTUtils.getNameRange(method);
							// System.out.println("--MethodOffset: " + nameRange.getOffset());
							// System.out.println("--MethodLength: " + nameRange.getLength());
							Range range = JDTUtils.toRange(unit, nameRange.getOffset(), nameRange.getLength());
							Range diagRangeMe = range;
							diagnostics.add(new Diagnostic(diagRangeMe, "A Diagnostic message on every method"));
						}
					}
				} catch (JavaModelException e) {
					Activator.logException("Cannot calculate diagnostics", e);
				}
			}
			PublishDiagnosticsParams publishDiagnostic = new PublishDiagnosticsParams(uri, diagnostics);
			publishDiagnostics.add(publishDiagnostic);
		}
		return publishDiagnostics;
	}
}
