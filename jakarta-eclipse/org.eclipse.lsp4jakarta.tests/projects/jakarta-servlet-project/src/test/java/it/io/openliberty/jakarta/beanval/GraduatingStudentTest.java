package it.io.openliberty.jakarta.beanval;

import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Set;

import org.junit.Test;

import io.openliberty.sample.jakarta.beanval.GraduatingStudent;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class GraduatingStudentTest {

    @Test
    public void testGraduatingStudent() {

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Integer studentId = 7834242;
        boolean isHappy = true;
        boolean isSad = false;
        Calendar graduationDate = new GregorianCalendar(2021, 5, 1);
        Integer gpa = 80;
        String emailAddress = "Student@university.ca";

        GraduatingStudent student = new GraduatingStudent(studentId, isHappy, isSad, graduationDate, gpa, emailAddress);

        Set<ConstraintViolation<GraduatingStudent>> constraintViolations = validator.validate(student);

        assertTrue("Graduating student fails validation", constraintViolations.isEmpty());
    }

}