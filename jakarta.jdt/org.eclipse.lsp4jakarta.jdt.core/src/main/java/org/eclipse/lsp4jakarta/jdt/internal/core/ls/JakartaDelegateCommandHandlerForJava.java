package org.eclipse.lsp4jakarta.jdt.internal.core.ls;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ls.core.internal.IDelegateCommandHandler;
import org.eclipse.jdt.ls.core.internal.JavaLanguageServerPlugin;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.jsonrpc.CompletableFutures;
import org.eclipse.lsp4jakarta.commons.JakartaClasspathParams;
import org.eclipse.lsp4jakarta.jdt.core.JDTServicesManager;

public class JakartaDelegateCommandHandlerForJava implements IDelegateCommandHandler {

	private static final String JAVA_CODEACTION_COMMAND_ID = "jakarta/java/codeaction";
	private static final String JAVA_COMPLETION_COMMAND_ID = "jakarta/java/classpath";
	private static final String JAVA_DIAGNOSTICS_COMMAND_ID = "jakarta/java/diagnostics";

	public JakartaDelegateCommandHandlerForJava() {
	}

	@Override
	public Object executeCommand(String commandId, List<Object> arguments, IProgressMonitor progress) throws Exception {
	    JavaLanguageServerPlugin.logInfo("executeCommand: " + commandId);
	    switch (commandId) {
		case JAVA_CODEACTION_COMMAND_ID:
			return null;
			// return getCodeActionForJava(arguments, commandId, progress);
		case JAVA_COMPLETION_COMMAND_ID:
		    return getContextBasedFilter(arguments, progress).get();
		case JAVA_DIAGNOSTICS_COMMAND_ID:
			return getDiagnosticsForJava(arguments, commandId, progress);
		default:
			throw new UnsupportedOperationException(String.format("Unsupported command '%s'!", commandId));
		}
	}
	// /**
	//  * Return the completion items for the given arguments
	//  *
	//  * @param arguments
	//  * @param commandId
	//  * @param monitor
	//  * @return the completion items for the given arguments
	//  * @throws JavaModelException
	//  * @throws CoreException
	//  */
	public CompletableFuture<Object> getContextBasedFilter(List<Object> arguments, IProgressMonitor progress) throws Exception {
	    
	    Map<String, Object> obj = getFirst(arguments); // TODO null check
	    String uri = getString(obj, "uri");
	    List<String> snippetCtx = getStringList(obj, "snippetCtx");
	    JavaLanguageServerPlugin.logInfo("gettingContextBasedFilter...");
		return CompletableFutures.computeAsync((cancelChecker) -> {
			return JDTServicesManager.getInstance().getExistingContextsFromClassPath(uri, snippetCtx);
		});
	}

	/**
	 * Returns the publish diagnostics list for a given java file URIs.
	 *
	 * @param arguments
	 * @param commandId
	 * @param monitor
	 * @return the publish diagnostics list for a given java file URIs.
	 */
	private CompletableFuture<List<PublishDiagnosticsParams>> getDiagnosticsForJava(List<Object> arguments, String commandId,
			IProgressMonitor monitor) {
	    Map<String, Object> obj = getFirst(arguments); // TODO null check
        List<String> uri = getStringList(obj, "uris");
        JavaLanguageServerPlugin.logInfo("getDiagnosticsForJava: " + uri);
//        List<String> snippetCtx = getStringList(obj, "snippetCtx");
		return CompletableFutures.computeAsync((cancelChecker) -> {
//            IProgressMonitor monitor = getProgressMonitor(cancelChecker);
            List<PublishDiagnosticsParams> publishDiagnostics = new ArrayList<PublishDiagnosticsParams>();
            publishDiagnostics = JDTServicesManager.getInstance().getJavaDiagnostics(uri, monitor);
            return publishDiagnostics;
        });
	}

	   public static Map<String, Object> getFirst(List<Object> arguments) {
	        return arguments.isEmpty() ? null : (Map<String, Object>) arguments.get(0);
	    }

	    public static String getString(Map<String, Object> obj, String key) {
	        return (String) obj.get(key);
	    }

	    @SuppressWarnings("unchecked")
	    public static List<String> getStringList(Map<String, Object> obj, String key) {
	        return (List<String>) obj.get(key);
	    }

	// /**
	//  * Returns the java diagnostics parameters from the given arguments map.
	//  *
	//  * @param arguments
	//  * @param commandId
	//  *
	//  * @return the java diagnostics parameters
	//  */
	// private static MicroProfileJavaDiagnosticsParams createMicroProfileJavaDiagnosticsParams(List<Object> arguments,
	// 		String commandId) {
	// 	Map<String, Object> obj = getFirst(arguments);
	// 	if (obj == null) {
	// 		throw new UnsupportedOperationException(String.format(
	// 				"Command '%s' must be called with one MicroProfileJavaDiagnosticsParams argument!", commandId));
	// 	}
	// 	List<String> javaFileUri = getStringList(obj, "uris");
	// 	if (javaFileUri == null) {
	// 		throw new UnsupportedOperationException(String.format(
	// 				"Command '%s' must be called with required MicroProfileJavaDiagnosticsParams.uri (java URIs)!",
	// 				commandId));
	// 	}
	// 	MicroProfileJavaDiagnosticsSettings settings = null;
	// 	Map<String, Object> settingsObj = getObject(obj, "settings");
	// 	if (settingsObj != null) {
	// 		List<String> patterns = getStringList(settingsObj, "patterns");
	// 		settings = new MicroProfileJavaDiagnosticsSettings(patterns);
	// 	}
	// 	return new MicroProfileJavaDiagnosticsParams(javaFileUri, settings);
	// }

	// /**
	//  * Returns the <code>Hover</code> for the hover described in
	//  * <code>arguments</code>
	//  *
	//  * @param arguments
	//  * @param commandId
	//  * @param monitor
	//  * @return
	//  * @throws JavaModelException
	//  * @throws CoreException
	//  */
	// private static Hover getHoverForJava(List<Object> arguments, String commandId, IProgressMonitor monitor)
	// 		throws JavaModelException, CoreException {
	// 	// Create java hover parameter
	// 	MicroProfileJavaHoverParams params = createMicroProfileJavaHoverParams(arguments, commandId);
	// 	// Return hover info from hover parameter
	// 	return PropertiesManagerForJava.getInstance().hover(params, JDTUtilsLSImpl.getInstance(), monitor);
	// }

	// /**
	//  * Returns the java hover parameters from the given arguments map.
	//  *
	//  * @param arguments
	//  * @param commandId
	//  *
	//  * @return the java hover parameters
	//  */
	// private static MicroProfileJavaHoverParams createMicroProfileJavaHoverParams(List<Object> arguments,
	// 		String commandId) {
	// 	Map<String, Object> obj = getFirst(arguments);
	// 	if (obj == null) {
	// 		throw new UnsupportedOperationException(String
	// 				.format("Command '%s' must be called with one MicroProfileJavaHoverParams argument!", commandId));
	// 	}
	// 	String javaFileUri = getString(obj, "uri");
	// 	if (javaFileUri == null) {
	// 		throw new UnsupportedOperationException(String.format(
	// 				"Command '%s' must be called with required MicroProfileJavaHoverParams.uri (java URI)!",
	// 				commandId));
	// 	}

	// 	Position hoverPosition = getPosition(obj, "position");
	// 	DocumentFormat documentFormat = DocumentFormat.PlainText;
	// 	Number documentFormatIndex = (Number) obj.get("documentFormat");
	// 	if (documentFormatIndex != null) {
	// 		documentFormat = DocumentFormat.forValue(documentFormatIndex.intValue());
	// 	}
	// 	boolean surroundEqualsWithSpaces = ((Boolean) obj.get("surroundEqualsWithSpaces")).booleanValue();
	// 	return new MicroProfileJavaHoverParams(javaFileUri, hoverPosition, documentFormat, surroundEqualsWithSpaces);
	// }
}
