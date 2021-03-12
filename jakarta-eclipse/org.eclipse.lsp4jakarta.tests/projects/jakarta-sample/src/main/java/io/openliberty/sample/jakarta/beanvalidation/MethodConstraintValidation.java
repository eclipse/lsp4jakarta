package io.openliberty.sample.jakarta.beanvalidation;

import jakarta.validation.constraints.AssertFalse;
import jakarta.validation.constraints.AssertTrue;

public class MethodConstraintValidation {

    // valid cases
    @AssertFalse
    private boolean falseMethod() {
        return false;
    }

    @AssertTrue
    public boolean trueMethod() {
        return true;
    }

    // invalid cases
    @AssertTrue
    public static boolean anotherTruth() {
        return true;
    }

    @AssertTrue
    public String notBoolean() {
        return "aha!";
    }
   
}