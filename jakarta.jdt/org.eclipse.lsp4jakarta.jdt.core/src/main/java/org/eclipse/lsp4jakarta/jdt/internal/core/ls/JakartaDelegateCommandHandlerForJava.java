package org.eclipse.lsp4jakarta.jdt.internal.core.ls;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ls.core.internal.IDelegateCommandHandler;
import org.eclipse.jdt.ls.core.internal.JavaLanguageServerPlugin;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionContext;
import org.eclipse.lsp4j.CodeActionParams;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.jsonrpc.CompletableFutures;
import org.eclipse.lsp4jakarta.commons.JakartaClasspathParams;
import org.eclipse.lsp4jakarta.commons.JakartaJavaCodeActionParams;
import org.eclipse.lsp4jakarta.jdt.core.JDTServicesManager;
import org.eclipse.lsp4jakarta.jdt.core.JDTUtils;

public class JakartaDelegateCommandHandlerForJava implements IDelegateCommandHandler {

    private static final String JAVA_CODEACTION_COMMAND_ID = "jakarta/java/codeaction";
    private static final String JAVA_COMPLETION_COMMAND_ID = "jakarta/java/classpath";
    private static final String JAVA_DIAGNOSTICS_COMMAND_ID = "jakarta/java/diagnostics";

    private static final String DATA_PROPERTY = "data";
    private static final String SOURCE_PROPERTY = "source";
    private static final String MESSAGE_PROPERTY = "message";
    private static final String CODE_PROPERTY = "code";
    private static final String RANGE_PROPERTY = "range";
    private static final String DIAGNOSTICS_PROPERTY = "diagnostics";
    private static final String END_PROPERTY = "end";
    private static final String START_PROPERTY = "start";
    private static final String CHARACTER_PROPERTY = "character";
    private static final String LINE_PROPERTY = "line";
    private static final String URI_PROPERTY = "uri";

    public JakartaDelegateCommandHandlerForJava() {
    }

    @Override
    public Object executeCommand(String commandId, List<Object> arguments, IProgressMonitor progress) throws Exception {
        JavaLanguageServerPlugin.logInfo("executeCommand: " + commandId);
        switch (commandId) {
            case JAVA_CODEACTION_COMMAND_ID:
                Object jakartaCodeActions = getCodeActionForJava(arguments, commandId, progress).get();
                JavaLanguageServerPlugin.logInfo("jakartaCodeActions: " + jakartaCodeActions.toString());
                return jakartaCodeActions;
            case JAVA_COMPLETION_COMMAND_ID:
                return getContextBasedFilter(arguments, progress).get();
            case JAVA_DIAGNOSTICS_COMMAND_ID:
                Object jakartaDiagnostics = getDiagnosticsForJava(arguments, commandId, progress).get();
                JavaLanguageServerPlugin.logInfo("jakartaDiagnostics: " + jakartaDiagnostics.toString());
                return jakartaDiagnostics;
            default:
                throw new UnsupportedOperationException(String.format("Unsupported command '%s'!", commandId));
        }
    }

    // /**
    // * Return the completion items for the given arguments
    // *
    // * @param arguments
    // * @param commandId
    // * @param monitor
    // * @return the completion items for the given arguments
    // * @throws JavaModelException
    // * @throws CoreException
    // */
    public CompletableFuture<Object> getContextBasedFilter(List<Object> arguments, IProgressMonitor progress)
            throws Exception {

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
    private CompletableFuture<List<PublishDiagnosticsParams>> getDiagnosticsForJava(List<Object> arguments,
            String commandId, IProgressMonitor monitor) {
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

    private CompletableFuture<List<CodeAction>> getCodeActionForJava(List<Object> arguments, String commandId,
            IProgressMonitor monitor) {
        JavaLanguageServerPlugin.logInfo("getCodeActionForJava arguments: " + arguments);
        Map<String, Object> obj = getFirst(arguments); // TODO null check
        JavaLanguageServerPlugin.logInfo("getCodeActionForJava obj: " + obj);

        // reconstruct JakartaJavaCodeActionParams
        TextDocumentIdentifier textDocumentIdentifier = getTextDocumentIdentifier(obj, "textDocument");
        if (textDocumentIdentifier == null) {
            throw new UnsupportedOperationException(String.format(
                    "Command '%s' must be called with required MicroProfileJavaCodeActionParams.texdDocumentIdentifier",
                    commandId));
        }
        Range range = getRange(obj, "range");
        CodeActionContext context = getCodeActionContext(obj, "context");
        boolean resourceOperationSupported = getBoolean(obj, "resourceOperationSupported");
        JakartaJavaCodeActionParams params = new JakartaJavaCodeActionParams();
        params.setTextDocument(textDocumentIdentifier);
        params.setRange(range);
        params.setContext(context);
        params.setResourceOperationSupported(resourceOperationSupported);
        JavaLanguageServerPlugin.logInfo("JakartaJavaCodeActionParams params: " + params);
        JDTUtils utils = new JDTUtils();
        return CompletableFutures.computeAsync((cancelChecker) -> {
//          IProgressMonitor monitor = getProgressMonitor(cancelChecker);
            List<CodeAction> codeActions = new ArrayList<CodeAction>();
            try {
                codeActions = JDTServicesManager.getInstance().getCodeAction(params, utils, monitor);
            } catch (JavaModelException e) {
                // TODO Auto-generated catch block
                JavaLanguageServerPlugin.logException(commandId, e);
            }
            return codeActions;
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

    public static boolean getBoolean(Map<String, Object> obj, String key) {
        Object result = obj.get(key);
        return result != null && result instanceof Boolean && ((Boolean) result).booleanValue();
    }

    public static int getInt(Map<String, Object> obj, String key) {
        Object result = obj.get(key);
        return result != null && result instanceof Number ? ((Number) result).intValue() : 0;
    }

    public static TextDocumentIdentifier getTextDocumentIdentifier(Map<String, Object> obj, String key) {
        Map<String, Object> textDocumentIdentifierObj = (Map<String, Object>) obj.get(key);
        if (textDocumentIdentifierObj == null) {
            return null;
        }
        String uri = getString(textDocumentIdentifierObj, URI_PROPERTY);
        return new TextDocumentIdentifier(uri);
    }

    public static Position getPosition(Map<String, Object> obj, String key) {
        Map<String, Object> positionObj = (Map<String, Object>) obj.get(key);
        if (positionObj == null) {
            return null;
        }
        int line = getInt(positionObj, LINE_PROPERTY);
        int character = getInt(positionObj, CHARACTER_PROPERTY);
        return new Position(line, character);
    }

    public static Range getRange(Map<String, Object> obj, String key) {
        Map<String, Object> rangeObj = (Map<String, Object>) obj.get(key);
        if (rangeObj == null) {
            return null;
        }
        Position start = getPosition(rangeObj, START_PROPERTY);
        Position end = getPosition(rangeObj, END_PROPERTY);
        return new Range(start, end);
    }

    public static CodeActionContext getCodeActionContext(Map<String, Object> obj, String key) {
        Map<String, Object> contextObj = (Map<String, Object>) obj.get(key);
        if (contextObj == null) {
            return null;
        }
        List<Map<String, Object>> diagnosticsObj = (List<Map<String, Object>>) contextObj.get(DIAGNOSTICS_PROPERTY);
        List<Diagnostic> diagnostics = diagnosticsObj.stream().map(diagnosticObj -> {
            Diagnostic diagnostic = new Diagnostic();
            diagnostic.setRange(getRange(diagnosticObj, RANGE_PROPERTY));
            diagnostic.setCode(getString(diagnosticObj, CODE_PROPERTY));
            diagnostic.setMessage(getString(diagnosticObj, MESSAGE_PROPERTY));
            diagnostic.setSource(getString(diagnosticObj, SOURCE_PROPERTY));
            // In Eclipse IDE (LSP client), the data is JsonObject, and in JDT-LS (ex :
            // vscode as LSP client) the data is a Map, we
            // convert the Map to a JsonObject to be consistent with any LSP clients.
            diagnostic.setData(getObjectAsJson(diagnosticObj, DATA_PROPERTY));
            return diagnostic;
        }).collect(Collectors.toList());
        List<String> only = null;
        return new CodeActionContext(diagnostics, only);
    }

    public static JsonObject getObjectAsJson(Map<String, Object> obj, String key) {
        Object child = obj.get(key);
        if (child != null && child instanceof Map<?, ?>) {
            Gson gson = new Gson();
            return (JsonObject) gson.toJsonTree(obj);
        }
        return null;
    }

    // /**
    // * Returns the java diagnostics parameters from the given arguments map.
    // *
    // * @param arguments
    // * @param commandId
    // *
    // * @return the java diagnostics parameters
    // */
    // private static MicroProfileJavaDiagnosticsParams
    // createMicroProfileJavaDiagnosticsParams(List<Object> arguments,
    // String commandId) {
    // Map<String, Object> obj = getFirst(arguments);
    // if (obj == null) {
    // throw new UnsupportedOperationException(String.format(
    // "Command '%s' must be called with one MicroProfileJavaDiagnosticsParams
    // argument!", commandId));
    // }
    // List<String> javaFileUri = getStringList(obj, "uris");
    // if (javaFileUri == null) {
    // throw new UnsupportedOperationException(String.format(
    // "Command '%s' must be called with required
    // MicroProfileJavaDiagnosticsParams.uri (java URIs)!",
    // commandId));
    // }
    // MicroProfileJavaDiagnosticsSettings settings = null;
    // Map<String, Object> settingsObj = getObject(obj, "settings");
    // if (settingsObj != null) {
    // List<String> patterns = getStringList(settingsObj, "patterns");
    // settings = new MicroProfileJavaDiagnosticsSettings(patterns);
    // }
    // return new MicroProfileJavaDiagnosticsParams(javaFileUri, settings);
    // }

    // /**
    // * Returns the <code>Hover</code> for the hover described in
    // * <code>arguments</code>
    // *
    // * @param arguments
    // * @param commandId
    // * @param monitor
    // * @return
    // * @throws JavaModelException
    // * @throws CoreException
    // */
    // private static Hover getHoverForJava(List<Object> arguments, String
    // commandId, IProgressMonitor monitor)
    // throws JavaModelException, CoreException {
    // // Create java hover parameter
    // MicroProfileJavaHoverParams params =
    // createMicroProfileJavaHoverParams(arguments, commandId);
    // // Return hover info from hover parameter
    // return PropertiesManagerForJava.getInstance().hover(params,
    // JDTUtilsLSImpl.getInstance(), monitor);
    // }

    // /**
    // * Returns the java hover parameters from the given arguments map.
    // *
    // * @param arguments
    // * @param commandId
    // *
    // * @return the java hover parameters
    // */
    // private static MicroProfileJavaHoverParams
    // createMicroProfileJavaHoverParams(List<Object> arguments,
    // String commandId) {
    // Map<String, Object> obj = getFirst(arguments);
    // if (obj == null) {
    // throw new UnsupportedOperationException(String
    // .format("Command '%s' must be called with one MicroProfileJavaHoverParams
    // argument!", commandId));
    // }
    // String javaFileUri = getString(obj, "uri");
    // if (javaFileUri == null) {
    // throw new UnsupportedOperationException(String.format(
    // "Command '%s' must be called with required MicroProfileJavaHoverParams.uri
    // (java URI)!",
    // commandId));
    // }

    // Position hoverPosition = getPosition(obj, "position");
    // DocumentFormat documentFormat = DocumentFormat.PlainText;
    // Number documentFormatIndex = (Number) obj.get("documentFormat");
    // if (documentFormatIndex != null) {
    // documentFormat = DocumentFormat.forValue(documentFormatIndex.intValue());
    // }
    // boolean surroundEqualsWithSpaces = ((Boolean)
    // obj.get("surroundEqualsWithSpaces")).booleanValue();
    // return new MicroProfileJavaHoverParams(javaFileUri, hoverPosition,
    // documentFormat, surroundEqualsWithSpaces);
    // }
}
