package io.openliberty.sample.jakarta.jax_rs;

import jakarta.ws.rs.HEAD;

public class NotPublicResourceMethod {
	
    @HEAD
    private void privateMethod() {
        
    }
    
    @HEAD
    void defaultMethod() {
        
    }

}
