package io.openliberty.sample.jakarta.di;

import jakarta.fake.context.ApplicationScoped;
import jakarta.fake.Singleton;

@ApplicationScoped
public class Greeting {

    public String greet(String name) {
        return "Hello, " + name;
    }

}