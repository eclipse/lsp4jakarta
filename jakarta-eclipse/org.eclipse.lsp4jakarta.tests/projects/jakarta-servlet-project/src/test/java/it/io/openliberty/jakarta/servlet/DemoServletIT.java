package it.io.openliberty.jakarta.servlet;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.provider.jsrjsonp.JsrJsonpProvider;
import org.junit.Test;

public class DemoServletIT {

  @Test
  public void testGetProperties() {

    // system properties
    String hostname = System.getProperty("liberty.hostname", "localhost");
    String port = System.getProperty("liberty.http.port", "9080");
    String url = "http://" + hostname + ":" + port + "/";

    // client setup
    Client client = ClientBuilder.newClient();
    client.register(JsrJsonpProvider.class);

    // request
    WebTarget target = client.target(url + "demo");
    Response response = target.request().get();

    // response
    assertEquals("Incorrect response code from " + url, 200, response.getStatus());

    response.close();
  }

}
