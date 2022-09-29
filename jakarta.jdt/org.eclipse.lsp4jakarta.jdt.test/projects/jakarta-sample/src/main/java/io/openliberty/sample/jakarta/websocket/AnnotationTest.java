package io.openliberty.sample.jakarta.websocket;

import java.io.IOException;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnOpen;
import jakarta.websocket.server.ServerEndpoint;
import jakarta.websocket.Session;

/**
 * Expected Diagnostics are related to validating that the parameters have the 
 * valid annotation @PathParam (code: AddPathParamsAnnotation)
 * See issue #247 (onOpen) and #248 (onClose)
 */
@ServerEndpoint(value = "/infos")
public class AnnotationTest {
    // @PathParam missing annotation for "String missingAnnotation"
    @OnOpen
    public void OnOpen(Session session, String missingAnnotation) throws IOException {
        System.out.println("Websocket opened: " + session.getId().toString());
    }
    
    // Used to check that the expected diagnostic handle more than one case
    @OnClose
    public void OnClose(Session session, Integer missingAnnotation1, String missingAnnotation2) {
        System.out.println("Websocket opened: " + session.getId().toString());
    }
}
