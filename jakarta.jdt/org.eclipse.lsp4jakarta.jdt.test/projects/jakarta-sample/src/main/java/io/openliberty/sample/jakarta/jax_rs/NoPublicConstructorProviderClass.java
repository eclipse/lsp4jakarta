package io.openliberty.sample.jakarta.jax_rs;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.Provider;


@Consumes("application/x-www-form-urlencoded")
@Provider
public class NoPublicConstructorProviderClass implements MessageBodyReader<Object> {

    private NoPublicConstructorProviderClass() {

    }

    protected NoPublicConstructorProviderClass(int arg1) {

    }

	@Override
	public boolean isReadable(Class<?> arg0, Type arg1, Annotation[] arg2, MediaType arg3) {
		return false;
	}

	@Override
	public Object readFrom(Class<Object> arg0, Type arg1, Annotation[] arg2, MediaType arg3,
			MultivaluedMap<String, String> arg4, InputStream arg5) throws IOException, WebApplicationException {
		return null;
	}

}
