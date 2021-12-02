package io.openliberty.sample.jakarta.jax_rs;

import jakarta.ws.rs.ext.Provider;


@Consumes("application/x-www-form-urlencoded")
@Provider
public class NoPublicConstructorProviderClass implements MessageBodyReader<NameValuePair> {

    private NoPublicConstructorProviderClass() {

    }

    protected NoPublicConstructorProviderClass(int arg1) {

    }

}
