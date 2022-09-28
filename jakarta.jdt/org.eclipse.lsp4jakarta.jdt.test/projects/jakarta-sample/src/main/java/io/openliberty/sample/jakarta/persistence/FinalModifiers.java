package io.openliberty.sample.jakarta.persistence;

import jakarta.persistence.Entity;

@Entity
public final class FinalModifiers {

    final int x = 1;
    final String y = "hello", z = "world";
    
    public final int methody() {
        final int ret = 100;
        return 100 + ret;
    }
}