package org.eclipse.lsp4jakarta.jdt.internal.core.ls;

import java.util.List;
import java.util.ArrayList;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.lsp4j.jsonrpc.CompletableFutures;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4jakarta.jdt.core.JDTServicesManager;

public class JakartaDelegateCommandHandlerForJava implements IDelegateCommandHandler {

	private static final String JAVA_CODEACTION_COMMAND_ID = "jakarta/java/codeaction";
	private static final String JAVA_COMPLETION_COMMAND_ID = "jakarta/java/classpath";
	private static final String JAVA_DIAGNOSTICS_COMMAND_ID = "jakarta/java/diagnostics";

	public JakartaDelegateCommandHandlerForJava() {
	}

	@Override
	public Object executeCommand(String commandId, List<Object> arguments, IProgressMonitor progress) throws Exception {
		switch (commandId) {
		case JAVA_CODEACTION_COMMAND_ID:
			return null;
			// return getCodeActionForJava(arguments, commandId, progress);
		case JAVA_COMPLETION_COMMAND_ID:
			// System.out.println("Registered command " + JAVA_COMPLETION_COMMAND_ID);
			return getContextBasedFilter(arguments, commandId, progress);
		case JAVA_DIAGNOSTICS_COMMAND_ID:
			return null;
			// return getDiagnosticsForJava(arguments, commandId, progress);
		default:
			throw new UnsupportedOperationException(String.format("Unsupported command '%s'!", commandId));
		}
	}

	/**
	 * Returns the code action for the given Java file.
	 *
	 * @param arguments
	 * @param commandId
	 * @param monitor
	 * @return the code action for the given Java file.
	 * @throws CoreException
	 * @throws JavaModelException
	 */
	// private static List<? extends CodeAction> getCodeActionForJava(List<Object> arguments, String commandId,
	// 		IProgressMonitor monitor) throws JavaModelException, CoreException {
	// 	return null;
	// 	// // Create java code action parameter
	// 	// MicroProfileJavaCodeActionParams params = createMicroProfileJavaCodeActionParams(arguments, commandId);
	// 	// // Return code action from the code action parameter
	// 	// return PropertiesManagerForJava.getInstance().codeAction(params, JDTUtilsLSImpl.getInstance(), monitor);
	// }

	// /**
	//  * Create java code action parameter from the given arguments map.
	//  *
	//  * @param arguments
	//  * @param commandId
	//  *
	//  * @return java code action parameter
	//  */
	// private static MicroProfileJavaCodeActionParams createMicroProfileJavaCodeActionParams(List<Object> arguments,
	// 		String commandId) {
	// 	Map<String, Object> obj = getFirst(arguments);
	// 	if (obj == null) {
	// 		throw new UnsupportedOperationException(String.format(
	// 				"Command '%s' must be called with one MicroProfileJavaCodeActionParams argument!", commandId));
	// 	}
	// 	TextDocumentIdentifier texdDocumentIdentifier = getTextDocumentIdentifier(obj, "textDocument");
	// 	if (texdDocumentIdentifier == null) {
	// 		throw new UnsupportedOperationException(String.format(
	// 				"Command '%s' must be called with required MicroProfileJavaCodeActionParams.texdDocumentIdentifier",
	// 				commandId));
	// 	}
	// 	Range range = getRange(obj, "range");
	// 	CodeActionContext context = getCodeActionContext(obj, "context");
	// 	boolean resourceOperationSupported = getBoolean(obj, "resourceOperationSupported");
	// 	boolean commandConfigurationUpdateSupported = getBoolean(obj, "commandConfigurationUpdateSupported");
	// 	MicroProfileJavaCodeActionParams params = new MicroProfileJavaCodeActionParams();
	// 	params.setTextDocument(texdDocumentIdentifier);
	// 	params.setRange(range);
	// 	params.setContext(context);
	// 	params.setResourceOperationSupported(resourceOperationSupported);
	// 	params.setCommandConfigurationUpdateSupported(commandConfigurationUpdateSupported);
	// 	return params;
	// }

	// /**
	//  * Returns the code lenses for the given Java file.
	//  *
	//  * @param arguments
	//  * @param commandId
	//  * @param monitor
	//  * @return the code lenses for the given Java file.
	//  * @throws CoreException
	//  * @throws JavaModelException
	//  */
	// private static List<? extends CodeLens> getCodeLensForJava(List<Object> arguments, String commandId,
	// 		IProgressMonitor monitor) throws JavaModelException, CoreException {
	// 	// Create java code lens parameter
	// 	MicroProfileJavaCodeLensParams params = createMicroProfileJavaCodeLensParams(arguments, commandId);
	// 	// Return code lenses from the lens parameter
	// 	return PropertiesManagerForJava.getInstance().codeLens(params, JDTUtilsLSImpl.getInstance(), monitor);
	// }

	// /**
	//  * Create java code lens parameter from the given arguments map.
	//  *
	//  * @param arguments
	//  * @param commandId
	//  *
	//  * @return java code lens parameter
	//  */
	// private static MicroProfileJavaCodeLensParams createMicroProfileJavaCodeLensParams(List<Object> arguments,
	// 		String commandId) {
	// 	Map<String, Object> obj = getFirst(arguments);
	// 	if (obj == null) {
	// 		throw new UnsupportedOperationException(String.format(
	// 				"Command '%s' must be called with one MicroProfileJavaCodeLensParams argument!", commandId));
	// 	}
	// 	String javaFileUri = getString(obj, "uri");
	// 	if (javaFileUri == null) {
	// 		throw new UnsupportedOperationException(String.format(
	// 				"Command '%s' must be called with required MicroProfileJavaCodeLensParams.uri (java URI)!",
	// 				commandId));
	// 	}
	// 	MicroProfileJavaCodeLensParams params = new MicroProfileJavaCodeLensParams(javaFileUri);
	// 	params.setUrlCodeLensEnabled(getBoolean(obj, "urlCodeLensEnabled"));
	// 	params.setCheckServerAvailable(getBoolean(obj, "checkServerAvailable"));
	// 	params.setOpenURICommand(getString(obj, "openURICommand"));
	// 	params.setLocalServerPort(getInt(obj, "localServerPort"));
	// 	return params;
	// }

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
	public String getContextBasedFilter(List<Object> arguments, String commandId, IProgressMonitor progress) throws Exception {
		return "Hello, World";
		// return CompletableFutures.computeAsync((cancelChecker) -> {
		// 	return JDTServicesManager.getInstance().getExistingContextsFromClassPath(null, null);
        //     // return JDTServicesManager.getInstance().getExistingContextsFromClassPath(uri, snippetContexts);
        // });
	}

	// /**
	//  * Create the completion parameters from the given argument map
	//  *
	//  * @param arguments
	//  * @param commandId
	//  * @return the completion parameters from the given argument map
	//  */
	// private static MicroProfileJavaCompletionParams createMicroProfileJavaCompletionParams(List<Object> arguments,
	// 		String commandId) {
	// 	Map<String, Object> obj = getFirst(arguments);
	// 	if (obj == null) {
	// 		throw new UnsupportedOperationException(String.format(
	// 				"Command '%s' must be called with one MicroProfileJavaCompletionParams argument!", commandId));
	// 	}
	// 	String javaFileUri = getString(obj, "uri");
	// 	if (javaFileUri == null) {
	// 		throw new UnsupportedOperationException(String.format(
	// 				"Command '%s' must be called with required MicroProfileJavaCompletionParams.uri (java URI)!",
	// 				commandId));
	// 	}
	// 	Position position = getPosition(obj, "position");
	// 	if (position == null) {
	// 		throw new UnsupportedOperationException(String.format(
	// 				"Command '%s' must be called with required MicroProfileJavaCompletionParams.position (completion trigger location)!",
	// 				commandId));
	// 	}
	// 	MicroProfileJavaCompletionParams params = new MicroProfileJavaCompletionParams(javaFileUri, position);
	// 	return params;
	// }

	// /**
	//  * Returns the list o <code>MicroProfileLocationLink</code> for the definition
	//  * described in <code>arguments</code>
	//  *
	//  * @param arguments
	//  * @param commandId
	//  * @param monitor
	//  * @return
	//  * @throws JavaModelException
	//  * @throws CoreException
	//  */
	// private static List<MicroProfileDefinition> getDefinitionForJava(List<Object> arguments, String commandId,
	// 		IProgressMonitor monitor) throws JavaModelException, CoreException {
	// 	// Create java definition parameter
	// 	MicroProfileJavaDefinitionParams params = createMicroProfileJavaDefinitionParams(arguments, commandId);
	// 	// Return hover info from hover parameter
	// 	return PropertiesManagerForJava.getInstance().definition(params, JDTUtilsLSImpl.getInstance(), monitor);
	// }

	// /**
	//  * Returns the java definition parameters from the given arguments map.
	//  *
	//  * @param arguments
	//  * @param commandId
	//  *
	//  * @return the definition hover parameters
	//  */
	// private static MicroProfileJavaDefinitionParams createMicroProfileJavaDefinitionParams(List<Object> arguments,
	// 		String commandId) {
	// 	Map<String, Object> obj = getFirst(arguments);
	// 	if (obj == null) {
	// 		throw new UnsupportedOperationException(String.format(
	// 				"Command '%s' must be called with one MicroProfileJavaDefinitionParams argument!", commandId));
	// 	}
	// 	String javaFileUri = getString(obj, "uri");
	// 	if (javaFileUri == null) {
	// 		throw new UnsupportedOperationException(String.format(
	// 				"Command '%s' must be called with required MicroProfileJavaDefinitionParams.uri (java URI)!",
	// 				commandId));
	// 	}

	// 	Position hoverPosition = getPosition(obj, "position");
	// 	return new MicroProfileJavaDefinitionParams(javaFileUri, hoverPosition);
	// }

	/**
	 * Returns the publish diagnostics list for a given java file URIs.
	 *
	 * @param arguments
	 * @param commandId
	 * @param monitor
	 * @return the publish diagnostics list for a given java file URIs.
	 * @throws JavaModelException
	 */
	// private static List<PublishDiagnosticsParams> getDiagnosticsForJava(List<Object> arguments, String commandId,
	// 		IProgressMonitor monitor) throws JavaModelException {
	// 	return null;
	// 	// // Create java diagnostics parameter
	// 	// MicroProfileJavaDiagnosticsParams params = createMicroProfileJavaDiagnosticsParams(arguments, commandId);
	// 	// // Return diagnostics from parameter
	// 	// return PropertiesManagerForJava.getInstance().diagnostics(params, JDTUtilsLSImpl.getInstance(), monitor);
	// }

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
