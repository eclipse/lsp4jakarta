package io.openliberty.sample.jakarta.persistence;

import jakarta.fake.Entity;

@Entity
public class EntityMissingConstructor {

    private EntityMissingConstructor(int x) {}

}