package src.main.java.io.openliberty.sample.jakarta.websocket;

import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/{varA}/{varB}/{varA}")
public class ServerEndpointDuplicateVariableURI {}
