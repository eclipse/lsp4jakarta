package io.openliberty.sample.jakarta.websocket;

import jakarta.websocket.server.ServerEndpoint;

// Diagnostics:
// + Server endpoint paths must be a URI-template (level-1) or a partial URI.
@ServerEndpoint("/{very incorrect variable}/")
public class ServerEndpointInvalidTemplateURI {}
