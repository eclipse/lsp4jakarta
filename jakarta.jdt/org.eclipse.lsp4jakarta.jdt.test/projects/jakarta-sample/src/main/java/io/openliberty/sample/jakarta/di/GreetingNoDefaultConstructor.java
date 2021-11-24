package io.openliberty.sample.jakarta.di;

public class GreetingNoDefaultConstructor {

    private String greeting;

    public GreetingNoDefaultConstructor(String greeting) {
        this.greeting = greeting;
    }

    public String greet(String name) {
        return greeting + " " + name;
    }
}
