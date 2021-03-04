package io.openliberty.sample.jakarta.cdi;


@ApplicationScoped
public class ProducesAndInjectTogether{
    @Produces
    @Inject
    private String greeting = "Hello";
    
    
    @Produces
    @Inject
    public String greet(String name) {
        return this.greeting + " " + name + "!";
    }
}