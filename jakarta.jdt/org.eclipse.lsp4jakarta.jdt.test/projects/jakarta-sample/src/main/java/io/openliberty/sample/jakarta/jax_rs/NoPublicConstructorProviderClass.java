package io.openliberty.sample.jakarta.jax_rs;

import jakarta.ws.rs.ext.Provider;

@Provider()
public class NoPublicConstructorProviderClass {

    private NoPublicConstructorClass() {

    }

    protected NoPublicConstructorClass(int arg1) {

    }

}
