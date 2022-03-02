package io.openliberty.sample.jakarta.websocket;

import java.io.IOException;

import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/infos")
public class AnnotationMissing {
    @OnOpen
    public void OnOpen(Session session, EndpointConfig endpointConfig, String invalidParam) throws IOException {
        System.out.println("Websocket opened: " + session.getId().toString());
    }
}
