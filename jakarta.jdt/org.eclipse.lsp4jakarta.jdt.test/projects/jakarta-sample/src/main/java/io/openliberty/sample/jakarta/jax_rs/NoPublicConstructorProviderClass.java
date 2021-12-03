package io.openliberty.sample.jakarta.jax_rs;

import jakarta.ws.rs.ext.Provider;


@Consumes("application/x-www-form-urlencoded")
@Provider
public class NoPublicConstructorProviderClass implements MessageBodyReader<NameValuePair> {

    private NoPublicConstructorProviderClass() {

    }

    protected NoPublicConstructorProviderClass(int arg1) {

    }
    
    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType,
                                                          Annotation[] annotations) {
        if (rawType == LocalDateTime.class) {
            return (ParamConverter<T>) new MyDateConverter();
        }
        return null;
    }

}
