package io.openliberty.sample.jakarta.cdi;

import java.util.Collections;
import java.util.List;

import jakarta.enterprise.inject.Produces;

import jakarta.enterprise.context.*;

@ApplicationScoped @RequestScoped
public class ScopeDeclaration {
    @Produces @ApplicationScoped @Dependent
    private int n;
    
    @Produces @ApplicationScoped @RequestScoped
    public List<Integer> getAllProductIds() {
        return Collections.emptyList();
    }
}