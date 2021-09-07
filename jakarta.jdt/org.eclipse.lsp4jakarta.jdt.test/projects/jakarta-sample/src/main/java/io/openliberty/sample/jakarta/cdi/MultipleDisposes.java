package io.openliberty.sample.jakarta.cdi;

import jakarta.enterprise.context.ApplicationScoped;

import jakarta.enterprise.inject.Disposes;

@ApplicationScoped
public class MultipleDisposes {
    
    public String greet(@Disposes String name1, @Disposes String name2) {
        return "Hi " + name1 + " and " + name2 + "!";
    }
}
