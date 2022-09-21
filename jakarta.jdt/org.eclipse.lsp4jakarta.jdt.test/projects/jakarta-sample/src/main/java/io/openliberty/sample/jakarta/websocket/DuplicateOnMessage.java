package io.openliberty.sample.jakarta.websocket;

import java.io.Reader;

import jakarta.websocket.OnMessage;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

// String and Reader will cause a duplicate diagnostic as they're both text message formats.
@ServerEndpoint("/{var}")
public class DuplicateOnMessage {
    @OnMessage
    public void textHandler1(@PathParam("var") String var, String text, Session session) {
        session.getAsyncRemote().sendText(text);
    }

    @OnMessage
    public void textHandler2(@PathParam("var") String var, Reader text, Session session) {
        session.getAsyncRemote().sendText(var);
    }
}
