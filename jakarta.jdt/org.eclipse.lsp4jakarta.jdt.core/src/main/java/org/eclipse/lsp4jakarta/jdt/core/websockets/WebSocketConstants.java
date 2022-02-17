package org.eclipse.lsp4jakarta.jdt.core.websockets;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.lsp4j.DiagnosticSeverity;


public class WebSocketConstants {
    public static final String DIAGNOSTIC_SOURCE = "jakarta-websocket";

    public static final String DIAGNOSTIC_CODE_MISSING_ON_OPEN_ANNOTATION = "CompleteWebs";
    public static final DiagnosticSeverity ERROR = DiagnosticSeverity.Error;
	public static final DiagnosticSeverity WARNING = DiagnosticSeverity.Warning;

    /* https://jakarta.ee/specifications/websocket/2.0/websocket-spec-2.0.html#applications */
    // Class Level Annotations
    public static final String SERVER_ENDPOINT_ANNOTATION = "ServerEndpoint";
    public static final String CLIENT_ENDPOINT_ANNOTATION = "ClientEndpoint";
    // Superclass
    public static final String ENDPOINT_SUPERCLASS = "Endpoint";
    public static final Set<String> WS_ANNOTATION_CLASS = new HashSet<>(Arrays.asList(SERVER_ENDPOINT_ANNOTATION, CLIENT_ENDPOINT_ANNOTATION));

    /* Annotations */
    public static final String ON_OPEN = "OnOpen";
    public static final String ON_CLOSE = "OnClose";
    public static final String ON_MESSAGE = "OnMessage";
    public static final String ON_ERROR = "OnError";

    /* Types */
    // OnOpen annotation
    public static final String ENDPOINT_CONFIG = "EndpointConfig";
    public static final String SESSION = "Session";
    public static final String PATH_PARAM_ANNOTATION = "PathParam";
    public static final String STRING = "String";

    
    public final static Set<String> ON_OPEN_SET_PARAM_TYPES= new HashSet<>(Arrays.asList(ENDPOINT_CONFIG, SESSION, STRING)); 
}
