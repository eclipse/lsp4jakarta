package io.openliberty.sample.jakarta.di;

import jakarta.inject.Inject;
import jakarta.enterprise.inject.Produces;

import java.util.ArrayList;
import java.util.List;

public abstract class GreetingServlet {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    // d1: test code for @Inject fields cannot be final
    @Inject
    private final Greeting greeting = new Greeting();

    @Produces
    public GreetingNoDefaultConstructor getInstance() {
        return new GreetingNoDefaultConstructor("Howdy");
    }

    // d2
    @Inject
    public final void injectFinal() {
        // test code for @Inject methods cannot be final
        return;
    }

    // d3: test code for @Inject methods cannot be abstract
    @Inject
    public abstract void injectAbstract();

    // d4: test code for @Inject methods cannot be static
    @Inject
    public static void injectStatic() {
        return;
    }

    // d5: test code for @Inject methods cannot be generic
    @Inject
    public <T> List<T> injectGeneric(T arg) {
        // do nothing
        return new ArrayList<T>();
    };

}
