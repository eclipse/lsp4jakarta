package io.openliberty.sample.jakarta.cdi;

import jakarta.enterprise.context.ApplicationScoped; 
import jakarta.enterprise.inject.Produces; 
import jakarta.inject.Inject;


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