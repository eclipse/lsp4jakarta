package io.openliberty.sample.jakarta.di;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Singleton;

@ApplicationScoped
public class Greeting {

    public String greet(String name) {
        return "Hello, " + name;
    }

}