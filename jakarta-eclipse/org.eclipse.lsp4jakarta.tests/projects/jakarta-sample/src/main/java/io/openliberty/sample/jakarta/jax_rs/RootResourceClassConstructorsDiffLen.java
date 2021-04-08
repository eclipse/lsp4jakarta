package io.openliberty.sample.jakarta.jax_rs;

import jakarta.ws.rs.Path;

@Path("/somewhere")
public class RootResourceClassConstructorsDiffLen {

	public RootResourceClassConstructorsDiffLen() {
		
	}
	
	public RootResourceClassConstructorsDiffLen(int arg1) {
		
	}

}
