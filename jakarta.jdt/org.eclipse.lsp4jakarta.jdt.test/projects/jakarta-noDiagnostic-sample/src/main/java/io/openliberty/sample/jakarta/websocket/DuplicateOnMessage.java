package io.openliberty.sample.jakarta.websocket;

import java.io.Reader;

import jakarta.fake.OnMessage;
import jakarta.fake.Session;
import jakarta.fake.server.PathParam;
import jakarta.fake.server.ServerEndpoint;

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
