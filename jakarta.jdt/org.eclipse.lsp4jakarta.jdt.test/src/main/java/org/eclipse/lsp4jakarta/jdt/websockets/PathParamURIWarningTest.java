package org.eclipse.lsp4jakarta.jdt.websockets;

import java.io.IOException;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import jakarta.websocket.OnMessage;
import jakarta.websocket.Session;

@ServerEndpoint(value = "/paramdemo/{test}/{abcd}")
public class PathParamURIWarningTest {

	@OnMessage
	public void onMessage(String message, Session session, @PathParam("tast") String test) {
		System.out.println("Program requested " + message + " using " + session.getId());
	}
}
