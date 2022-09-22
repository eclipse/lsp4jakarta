package io.openliberty.sample.jakarta.websocket;

import jakarta.websocket.server.ServerEndpoint;

// Diagnostics:
// + Server endpoint paths must not use the same variable more than once in a path.
@ServerEndpoint("/{varA}/{varB}/{varA}")
public class ServerEndpointDuplicateVariableURI {}
