package io.openliberty.sample.jakarta.websocket;

import java.lang.Throwable;

import jakarta.websocket.OnError;
import jakarta.websocket.server.ServerEndpoint;

/**
 * Expected Diagnostics are related to validating that the parameters for the onError 
 * WebSocket method are not duplicate (diagnostic code: OnErrorChangeInvalidParam).
 * See issue #249 (OnError)
 */
@ServerEndpoint(value = "/infos")
public class DuplicateParamCheck {
    @OnError
    public void OnError(Session session, Throwable error1, Throwable error2) throws IOException {
        System.out.println("WebSocket closed for " + session.getId());
    }
}
