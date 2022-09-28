package io.openliberty.sample.jakarta.di;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.inject.Produces;

@WebServlet(name = "greetingServlet", urlPatterns = { "/di" })
public abstract class GreetingServlet extends HttpServlet {

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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // use @Inject greeting
        String greetingString = greeting.greet("Bob");
        // abc(greetingString);

        // use @Produces greeting
        // String greetingString = getInstance().greet("Bob");

        res.setContentType("text/html;charset=UTF-8");
        res.getWriter().println(greetingString);
    }

}
