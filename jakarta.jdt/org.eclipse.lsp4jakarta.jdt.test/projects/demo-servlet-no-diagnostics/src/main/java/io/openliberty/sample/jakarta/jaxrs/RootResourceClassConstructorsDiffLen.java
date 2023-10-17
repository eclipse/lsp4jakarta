package io.openliberty.sample.jakarta.jaxrs;

import jakarta.fake.rs.Path;

@Path("/somewhere")
public class RootResourceClassConstructorsDiffLen {

	public RootResourceClassConstructorsDiffLen() {
		
	}
	
	public RootResourceClassConstructorsDiffLen(int arg1) {
		
	}

}
