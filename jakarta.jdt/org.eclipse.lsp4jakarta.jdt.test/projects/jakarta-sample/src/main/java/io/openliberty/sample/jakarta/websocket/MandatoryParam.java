package io.openliberty.sample.jakarta.websocket;

import jakarta.websocket.OnError;
import jakarta.websocket.server.ServerEndpoint;

/**
 * Expected Diagnostics are related to validating that the parameters for the 
 * WebSocket methods are of valid types for onOpen (diagnostic code: OnOpenChangeInvalidParam) 
 * and onClose (diagnostic code: OnCloseChangeInvalidParam).
 * See issues #247 (onOpen) and #248 (onClose)
 */
@ServerEndpoint(value = "/infos")
public class MandatoryParam {
    @OnError
    public void OnError(Session session, String missingAnnotation) throws IOException {
        System.out.println("WebSocket closed for " + session.getId());
    }
}
