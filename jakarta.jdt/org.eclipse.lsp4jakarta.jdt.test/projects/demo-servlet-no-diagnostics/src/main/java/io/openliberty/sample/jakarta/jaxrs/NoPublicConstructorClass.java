package io.openliberty.sample.jakarta.jaxrs;

import jakarta.fake.rs.Path;

@Path("/somewhere")
public class NoPublicConstructorClass {

    private NoPublicConstructorClass() {

    }

    protected NoPublicConstructorClass(int arg1) {

    }

}
