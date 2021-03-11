package src.main.java.io.openliberty.sample.jakarta.cdi;

import jakarta.inject.Inject;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.ObservesAsync;

public class InjectAndDisposesObservesObservesAsync {
    
    @Inject
    public String greetDisposes(@Disposes String name) {
        return this.greeting + " " + name + "!";
    }
    
    
    @Inject
    public String greetObserves(@Observes String name) {
        return this.greeting + " " + name + "!";
    }
    
    
    @Inject
    public String greetObservesAsync(@ObservesAsync String name) {
        return this.greeting + " " + name + "!";
    }
    
    
    @Inject
    public String greetDisposesObserves(@Disposes String name1, @Observes String name2) {
        return this.greeting + " " + name + "!";
    }
    
    
    @Inject
    public String greetObservesObservesAsync(@Observes String name1, @ObservesAsync String name2) {
        return this.greeting + " " + name + "!";
    }
    
    
    @Inject
    public String greetDisposesObservesAsync(@Disposes String name1, @ObservesAsync String name2) {
        return this.greeting + " " + name + "!";
    }
    
    
    @Inject
    public String greetDisposesObservesObservesAsync(@Disposes String name1, @Observes String name2, @ObservesAsync String name3) {
        return this.greeting + " " + name + "!";
    }
}
