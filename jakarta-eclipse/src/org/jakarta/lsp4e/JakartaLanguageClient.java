package org.jakarta.lsp4e;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.lsp4e.LanguageClientImpl;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.HoverParams;
import org.eclipse.lsp4j.MarkedString;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.CancelChecker;
import org.eclipse.lsp4j.jsonrpc.CompletableFutures;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.jakarta.jdt.JDTUtils;

import io.microshed.jakartals.api.JakartaLanguageClientAPI;
import io.microshed.jakartals.commons.JakartaDiagnosticsParams;

public class JakartaLanguageClient extends LanguageClientImpl implements JakartaLanguageClientAPI {

	public JakartaLanguageClient() {
		// do nothing
	}

	private IProgressMonitor getProgressMonitor(CancelChecker cancelChecker) {
		IProgressMonitor monitor = new NullProgressMonitor() {
			public boolean isCanceled() {
				cancelChecker.checkCanceled();
				return false;
			};
		};
		return monitor;
	}

	@Override
	public CompletableFuture<Hover> getJavaHover(HoverParams params) {
		// return dummy test hover object
		Activator.log(new Status(IStatus.INFO, "hover request received", "hover request received"));
		return CompletableFutures.computeAsync((cancelChecker) -> {
			IProgressMonitor monitor = getProgressMonitor(cancelChecker);
			Hover testHover = new Hover();
			List<Either<String, MarkedString>> contents = new ArrayList<>();
			contents.add(Either.forLeft("this is test hover"));
			testHover.setContents(contents);
			return testHover;
		});
	}
	
	/**
	 * @author ankushsharma
	 * @param uri - String representing file from which to derive project classpath
	 * @param snippetContext - get all the context fields from the snippets and check if they exist in this method
	 * @return List<String>
	 */
	@Override
	public CompletableFuture<List<String>> getClassPathFromURI(String uri, List<String> snippetContexts) {
		return CompletableFutures.computeAsync((cancelChecker) -> {
			// Initialize the list that will hold the classpath with default null values
			List<String> classpath = new ArrayList<>();
			// Convert URI into a compilation unit
			ICompilationUnit unit = JDTUtils.resolveCompilationUnit(JDTUtils.toURI(uri));
			// Use compilation unit to get handle on the project
			JavaProject project = (JavaProject) unit.getJavaProject();
			if (project != null) {
				// Check if the contexts exist
				snippetContexts.forEach(context -> {
					IType classPathCtx = null;
					try {
						classPathCtx = project.findType(context);
						if (classPathCtx != null) {
							classpath.add(context);
						} else {
							classpath.add(null);
						}
					} catch (JavaModelException e) {
						Activator.logException("Failed to retrieve projectContext from JDT...", e);
					}
				});
			}
			return classpath;
		});
	}

	@Override
	public CompletableFuture<List<PublishDiagnosticsParams>> getJavaDiagnostics(JakartaDiagnosticsParams javaParams) {
		Activator.log(new Status(IStatus.INFO, "diagnostic request received", "diagnostic request receieved"));
		// creating a test diagnostic
		return CompletableFutures.computeAsync((cancelChecker) -> {
			IProgressMonitor monitor = getProgressMonitor(cancelChecker);
			List<PublishDiagnosticsParams> publishDiagnostics = new ArrayList<PublishDiagnosticsParams>();

			List<Diagnostic> diagnostics = new ArrayList<>();
			List<String> uris = javaParams.getUris();
			for (String uri : uris) {

				URI u = JDTUtils.toURI(uri);
				ICompilationUnit unit = JDTUtils.resolveCompilationUnit(u);
				
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
		});
	}
}
