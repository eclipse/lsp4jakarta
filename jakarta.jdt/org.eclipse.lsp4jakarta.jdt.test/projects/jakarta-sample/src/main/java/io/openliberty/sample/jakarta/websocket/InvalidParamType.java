package io.openliberty.sample.jakarta.websocket;

import java.io.IOException;

import jakarta.websocket.CloseReason;
import jakarta.websocket.OnOpen;
import jakarta.websocket.OnClose;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/infos")
public class InvalidParamType {
    @OnOpen
    public void OnOpen(Session session, Object invalidParam) throws IOException {
        System.out.println("Websocket opened: " + session.getId().toString());
    }
    
    @OnClose
    public void OnClose(Session session, CloseReason closeReason, Object invalidParam) throws IOException {
        
    }
}
