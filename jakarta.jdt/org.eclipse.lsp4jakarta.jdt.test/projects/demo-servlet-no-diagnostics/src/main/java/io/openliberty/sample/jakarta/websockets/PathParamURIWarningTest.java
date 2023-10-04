package io.openliberty.sample.jakarta.websockets;

import java.io.IOException;
import jakarta.fake.server.PathParam;
import jakarta.fake.server.ServerEndpoint;
import jakarta.fake.OnOpen;
import jakarta.fake.OnError;
import jakarta.fake.OnMessage;
import jakarta.fake.OnClose;
import jakarta.fake.Session;

@ServerEndpoint(value = "/paramdemo/{test}/{abcd}")
public class PathParamURIWarningTest {
    private static Session session;

    @OnOpen
    public void OnOpen(Session session) throws IOException {
        this.session = session;
        System.out.println("Websocket opened: " + session.getId().toString());
    }

    @OnMessage
    public void onMessage(String message, Session session, @PathParam("tast") String test) {
        System.out.println("Program requested " + message + " using " + session.getId());
        System.out.println(session.getPathParameters() + " - " + session.getRequestURI());
        if (test == null) {
            session.getAsyncRemote().sendText("Test String was Null!");
        } else {
            session.getAsyncRemote().sendText("Test String was \"" + message + "\"");
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("There was an error WebSocket error for " + session.getId() + " " + throwable.getMessage());
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("WebSocket closed for " + session.getId());
    }
}
