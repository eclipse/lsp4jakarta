package io.openliberty.sample.jakarta.websocket;

import jakarta.fake.server.ServerEndpoint;

// Diagnostics:
// + Server endpoint paths must not contain the sequences '/../', '/./' or '//'.
@ServerEndpoint("/../path")
public class ServerEndpointRelativePathTest {}
