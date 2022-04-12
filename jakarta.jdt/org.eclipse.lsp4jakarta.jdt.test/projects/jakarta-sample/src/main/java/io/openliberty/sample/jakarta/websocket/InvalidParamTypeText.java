package src.main.java.io.openliberty.sample.jakarta.websocket;

import java.io.IOException;

import jakarta.websocket.CloseReason;
import jakarta.websocket.OnOpen;
import jakarta.websocket.OnClose;
import jakarta.websocket.server.ServerEndpoint;

/**
 * Expected Diagnostics are related to validating that the parameters for the 
 * WebSocket methods are of valid types for onOpen (diagnostic code: OnOpenChangeInvalidParam) 
 * and onClose (diagnostic code: OnCloseChangeInvalidParam).
 * See issues #247 (onOpen) and #248 (onClose)
 */
@ServerEndpoint(value = "/infos")
public class InvalidParamTypeText {
    @OnOpen
    public void OnOpen(Session session) throws IOException {
        System.out.println("Websocket opened: " + session.getId().toString());
    }
    
    @OnMessage
    public void OnMessage(Session session, String message, boolean parts, ByteBuffer invalid) throws IOException {
        System.out.println("Websocket opened: " + session.getId().toString());
    }
    
    @OnClose
    public void OnClose(Session session) throws IOException {
        System.out.println("WebSocket closed for " + session.getId());
    }
}
