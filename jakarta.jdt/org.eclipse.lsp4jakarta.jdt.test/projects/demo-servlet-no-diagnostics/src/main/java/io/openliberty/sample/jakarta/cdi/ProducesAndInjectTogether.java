package io.openliberty.sample.jakarta.cdi;

import jakarta.fake.context.ApplicationScoped; 
import jakarta.fake.inject.Produces; 
import jakarta.fake.Inject;


@ApplicationScoped
public class ProducesAndInjectTogether {
    @Produces
    @Inject
    private String greeting = "Hello";
    
    
    @Produces
    @Inject
    public String greet(String name) {
        return this.greeting + " " + name + "!";
    }
}