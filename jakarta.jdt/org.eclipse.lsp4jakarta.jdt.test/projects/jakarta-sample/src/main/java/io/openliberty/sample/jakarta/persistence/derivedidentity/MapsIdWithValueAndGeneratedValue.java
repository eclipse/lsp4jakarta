package io.openliberty.sample.jakarta.persistence.derivedidentity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;

/**
 * INVALID: @MapsId("empId") + @Id + @GeneratedValue on the same relationship field.
 * @GeneratedValue is forbidden when the PK is derived (spec section 11.1.20).
 * Expects: GeneratedValueOnDerivedIdField on field "employee"
 */
@Entity
public class MapsIdWithValueAndGeneratedValue {

    @EmbeddedId
    private ProjectId id;

    @MapsId("empId")
    @ManyToOne
    @Id
    @GeneratedValue
    private Employee employee;
}
