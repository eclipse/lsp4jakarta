package io.openliberty.sample.jakarta.websocket;

import java.io.IOException;

import jakarta.websocket.OnOpen;
import jakarta.websocket.server.ServerEndpoint;
import jakarta.websocket.Session;

@ServerEndpoint(value = "/infos")
public class Test {
    @OnOpen
    public void OnOpen(Session session, String missingAnnotation) throws IOException {
        System.out.println("Websocket opened: " + session.getId().toString());
    }
}
