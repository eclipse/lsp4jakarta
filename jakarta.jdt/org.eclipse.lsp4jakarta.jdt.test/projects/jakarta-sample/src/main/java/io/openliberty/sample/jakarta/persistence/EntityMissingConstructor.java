package io.openliberty.sample.jakarta.persistence;

import jakarta.persistence.Entity;

@Entity
public class EntityMissingConstructor {

    private EntityMissingConstructor(int x) {}

}