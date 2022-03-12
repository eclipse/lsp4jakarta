package io.openliberty.sample.jakarta.websocket;

import java.io.IOException;

import jakarta.websocket.OnOpen;
import jakarta.websocket.server.ServerEndpoint;
import jakarta.websocket.Session;

@ServerEndpoint(value = "/infos")
public class AnnotationTest {
    @OnOpen
    public void OnOpen(Session session, String missingAnnotation) throws IOException {
        System.out.println("Websocket opened: " + session.getId().toString());
    }
    
    @OnClose
    public void OnClose(Session session, Integer missingAnnotation1, String missingAnnotation2) {
        System.out.println("Websocket opened: " + session.getId().toString());
    }
}
