package io.openliberty.sample.jakarta.cdi;

import java.util.Collections;
import java.util.List;

import jakarta.fake.inject.Produces;

import jakarta.fake.context.*;

@ApplicationScoped @RequestScoped
public class ScopeDeclaration {
    @Produces @ApplicationScoped @Dependent
    private int n;
    
    @Produces @ApplicationScoped @RequestScoped
    public List<Integer> getAllProductIds() {
        return Collections.emptyList();
    }
}