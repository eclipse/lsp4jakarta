package io.openliberty.sample.jakarta.persistence.derivedidentity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;

/**
 * INVALID: @MapsId + @Id + @GeneratedValue on the same relationship field.
 * @GeneratedValue is forbidden when the PK is derived (spec §11.1.20).
 * The entity type also triggers InvalidIdType since Employee is not a valid @Id type.
 * Expects: InvalidIdType and GeneratedValueOnDerivedIdField on field "employee"
 */
@Entity
public class MapsIdWithGeneratedValue {

    @Id
    @MapsId
    @OneToOne
    @GeneratedValue
    private Employee employee;
}
