package io.openliberty.sample.jakarta.websocket;

import jakarta.websocket.OnError;
import jakarta.websocket.server.ServerEndpoint;

/**
 * Expected Diagnostics are related to validating that the parameters for the 
 * OnError WebSocket methods are not duplicate values
 * (diagnostic code: OnErrorMandatoryParamMissing). See issues #249 (OnError)
 */
@ServerEndpoint(value = "/infos")
public class MandatoryParamTypes {
    @OnError
    public void OnError(Session session) throws IOException {
        System.out.println("WebSocket closed for " + session.getId());
    }
}
