package src.main.java.io.openliberty.sample.jakarta.websocket;

import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/{very incorrect variable}/")
public class ServerEndpointInvalidTemplateURI {}
