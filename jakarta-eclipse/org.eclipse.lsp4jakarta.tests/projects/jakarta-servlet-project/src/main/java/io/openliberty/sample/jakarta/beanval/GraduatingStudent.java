package io.openliberty.sample.jakarta.beanval;

import java.util.Calendar;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.AssertFalse;
import jakarta.validation.constraints.AssertTrue;

public class GraduatingStudent {

    @Positive
    private Integer studentId;

    @AssertTrue
    private boolean isHappy;

    @AssertFalse
    private boolean isSad;

    @FutureOrPresent
    private Calendar graduationDate;

    @Min(value = 50)
    @Max(value = 100)
    private Integer gpa;

    @Email
    private String emailAddress;

    public GraduatingStudent(Integer studentId, Boolean isHappy, boolean isSad, Calendar graduationDate, Integer gpa,
            String emailAddress) {
        this.studentId = studentId;
        this.isHappy = isHappy;
        this.isSad = isSad;
        this.graduationDate = graduationDate;
        this.gpa = gpa;
        this.emailAddress = emailAddress;
    }

    public void printGraduationMessage() {
        System.out.println("Congrats class of " + graduationDate.toString());
    }

}