package org.eclipse.lsp4jakarta.jdt.beanvalidation;

import static org.eclipse.lsp4jakarta.jdt.core.JakartaForJavaAssert.assertJavaCodeAction;
import static org.eclipse.lsp4jakarta.jdt.core.JakartaForJavaAssert.assertJavaDiagnostics;
import static org.eclipse.lsp4jakarta.jdt.core.JakartaForJavaAssert.ca;
import static org.eclipse.lsp4jakarta.jdt.core.JakartaForJavaAssert.createCodeActionParams;
import static org.eclipse.lsp4jakarta.jdt.core.JakartaForJavaAssert.d;
import static org.eclipse.lsp4jakarta.jdt.core.JakartaForJavaAssert.te;

import java.util.Arrays;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4jakarta.jdt.core.BaseJakartaTest;
import org.jakarta.jdt.JDTUtils;
import org.junit.Test;

import io.microshed.jakartals.commons.JakartaDiagnosticsParams;
import io.microshed.jakartals.commons.JakartaJavaCodeActionParams;

public class BeanValidationTest extends BaseJakartaTest {
    protected static JDTUtils JDT_UTILS = new JDTUtils();

    @Test
    public void validFieldConstraints() throws Exception {
        JDTUtils utils = JDT_UTILS;
        IJavaProject javaProject = loadJavaProject("jakarta-sample", "");

        IFile javaFile = javaProject.getProject().getFile(
                new Path("src/main/java/io/openliberty/sample/jakarta/beanvalidation/ValidConstraints.java"));
        String uri = javaFile.getLocation().toFile().toURI().toString();

        JakartaDiagnosticsParams diagnosticsParams = new JakartaDiagnosticsParams();
        diagnosticsParams.setUris(Arrays.asList(uri));

        // should be no errors 
        assertJavaDiagnostics(diagnosticsParams, utils);
    }

    @Test
    public void fieldConstraintValidation() throws Exception {
        JDTUtils utils = JDT_UTILS;
        IJavaProject javaProject = loadJavaProject("jakarta-sample", "");

        IFile javaFile = javaProject.getProject().getFile(
                new Path("src/main/java/io/openliberty/sample/jakarta/beanvalidation/FieldConstraintValidation.java"));
        String uri = javaFile.getLocation().toFile().toURI().toString();

        JakartaDiagnosticsParams diagnosticsParams = new JakartaDiagnosticsParams();
        diagnosticsParams.setUris(Arrays.asList(uri));

        // Test diagnostics
        Diagnostic d1 = d(10, 16, 23,
                "The @AssertTrue annotation can only be used on boolean and Boolean type fields.",
                DiagnosticSeverity.Error, "jakarta-bean-validation", "FixTypeOfElement");
        Diagnostic d2 = d(13, 19, 24,
                "The @AssertFalse annotation can only be used on boolean and Boolean type fields.",
                DiagnosticSeverity.Error, "jakarta-bean-validation", "FixTypeOfElement");
        Diagnostic d3 = d(17, 19, 29,
                "The @DecimalMax annotation can only be used on: \n"
                + "- BigDecimal \n"
                + "- BigInteger \n"
                + "- CharSequence\n"
                + "- byte, short, int, long (and their respective wrappers) \n"
                + " type fields.",
                DiagnosticSeverity.Error, "jakarta-bean-validation", "FixTypeOfElement");
        Diagnostic d4 = d(17, 19, 29,
                "The @DecimalMin annotation can only be used on: \n"
                + "- BigDecimal \n"
                + "- BigInteger \n"
                + "- CharSequence\n"
                + "- byte, short, int, long (and their respective wrappers) \n"
                + " type fields.",
                DiagnosticSeverity.Error, "jakarta-bean-validation", "FixTypeOfElement");
        Diagnostic d5 = d(20, 20, 26,
                "The @Digits annotation can only be used on: \n"
                + "- BigDecimal \n"
                + "- BigInteger \n"
                + "- CharSequence\n"
                + "- byte, short, int, long (and their respective wrappers) \n"
                + " type fields.",
                DiagnosticSeverity.Error, "jakarta-bean-validation", "FixTypeOfElement");
        Diagnostic d6 = d(23, 20, 32,
                "The @Email annotation can only be used on String and CharSequence type fields.",
                DiagnosticSeverity.Error, "jakarta-bean-validation", "FixTypeOfElement");
        Diagnostic d7 = d(26, 20, 34,
                "The @FutureOrPresent annotation can only be used on: Date, Calendar, Instant, LocalDate, LocalDateTime, LocalTime, MonthDay, OffsetDateTime, OffsetTime, Year, YearMonth, ZonedDateTime, HijrahDate, JapaneseDate, JapaneseDate, MinguoDate and ThaiBuddhistDate type fields.",
                DiagnosticSeverity.Error, "jakarta-bean-validation", "FixTypeOfElement");
        Diagnostic d8 = d(29, 19, 30,
                "The @Future annotation can only be used on: Date, Calendar, Instant, LocalDate, LocalDateTime, LocalTime, MonthDay, OffsetDateTime, OffsetTime, Year, YearMonth, ZonedDateTime, HijrahDate, JapaneseDate, JapaneseDate, MinguoDate and ThaiBuddhistDate type fields.",
                DiagnosticSeverity.Error, "jakarta-bean-validation", "FixTypeOfElement");
        Diagnostic d9 = d(33, 20, 23,
                "The @Min annotation can only be used on \n"
                        + "- BigDecimal \n"
                        + "- BigInteger\n"
                        + "- byte, short, int, long (and their respective wrappers) \n"
                        + " type fields.",
                        DiagnosticSeverity.Error, "jakarta-bean-validation", "FixTypeOfElement");
        Diagnostic d10 = d(33, 20, 23,
                "The @Max annotation can only be used on \n"
                + "- BigDecimal \n"
                + "- BigInteger\n"
                + "- byte, short, int, long (and their respective wrappers) \n"
                + " type fields.",
                DiagnosticSeverity.Error, "jakarta-bean-validation", "FixTypeOfElement");
        Diagnostic d11 = d(36, 20, 27,
                "The @Negative annotation can only be used on \n"
                + "- BigDecimal \n"
                + "- BigInteger\n"
                + "- byte, short, int, long, float, double (and their respective wrappers) \n"
                + " type fields.",
                DiagnosticSeverity.Error, "jakarta-bean-validation", "FixTypeOfElement");
        Diagnostic d12 = d(39, 19, 25,
                "The @NegativeOrZero annotation can only be used on \n"
                + "- BigDecimal \n"
                + "- BigInteger\n"
                + "- byte, short, int, long, float, double (and their respective wrappers) \n"
                + " type fields.",
                DiagnosticSeverity.Error, "jakarta-bean-validation", "FixTypeOfElement");
        Diagnostic d13 = d(42, 20, 32,
                "The @NotBlank annotation can only be used on String and CharSequence type fields.",
                DiagnosticSeverity.Error, "jakarta-bean-validation", "FixTypeOfElement");
        Diagnostic d14 = d(45, 21, 31,
                "The @Pattern annotation can only be used on String and CharSequence type fields.",
                DiagnosticSeverity.Error, "jakarta-bean-validation", "FixTypeOfElement");
        Diagnostic d15 = d(48, 19, 33,
                "The @Past annotation can only be used on: Date, Calendar, Instant, LocalDate, LocalDateTime, LocalTime, MonthDay, OffsetDateTime, OffsetTime, Year, YearMonth, ZonedDateTime, HijrahDate, JapaneseDate, JapaneseDate, MinguoDate and ThaiBuddhistDate type fields.",
                DiagnosticSeverity.Error, "jakarta-bean-validation", "FixTypeOfElement");
        Diagnostic d16 = d(51, 19, 33,
                "The @PastOrPresent annotation can only be used on: Date, Calendar, Instant, LocalDate, LocalDateTime, LocalTime, MonthDay, OffsetDateTime, OffsetTime, Year, YearMonth, ZonedDateTime, HijrahDate, JapaneseDate, JapaneseDate, MinguoDate and ThaiBuddhistDate type fields.",
                DiagnosticSeverity.Error, "jakarta-bean-validation", "FixTypeOfElement");
        Diagnostic d17 = d(54, 21, 25,
                "The @Positive annotation can only be used on \n"
                + "- BigDecimal \n"
                + "- BigInteger\n"
                + "- byte, short, int, long, float, double (and their respective wrappers) \n"
                + " type fields.",
                DiagnosticSeverity.Error, "jakarta-bean-validation", "FixTypeOfElement");
        // not yet implemented
//        Diagnostic d18 = d(11, 17, 24,
//                "The @PositiveOrZero annotation can only be used on boolean and Boolean type fields.",
//                DiagnosticSeverity.Error, "jakarta-bean-validation", "FixTypeOfElement");
        Diagnostic d19 = d(60, 27, 36,
                "Constraint annotations are not allowed on static fields",
                DiagnosticSeverity.Error, "jakarta-bean-validation", "MakeNotStatic");

        assertJavaDiagnostics(diagnosticsParams, utils, d1, d2, d3, d4, d5, d6, d7, d8,
                d9, d10, d11, d12, d13, d14, d15, d16, d17, d19);

        // Test quickfix codeActions (the same for all except last)
        JakartaJavaCodeActionParams codeActionParams = createCodeActionParams(uri, d1);
        TextEdit te = te(9, 4, 10, 4, "");
        CodeAction ca = ca(uri, "Remove constraint annotation from element", d1, te);    

        assertJavaCodeAction(codeActionParams, utils, ca);

        JakartaJavaCodeActionParams codeActionParams2 = createCodeActionParams(uri, d19);
        TextEdit te1 = te(59, 4, 60, 4, "");
        TextEdit te2 = te(60, 11, 60, 18, "");
        CodeAction ca1 = ca(uri, "Remove constraint annotation from element", d19, te1);
        CodeAction ca2 = ca(uri, "Remove static modifier from element", d19, te2);

        assertJavaCodeAction(codeActionParams2, utils, ca1, ca2);
    }
    
    @Test
    public void methodConstraintValidation() throws Exception {
        JDTUtils utils = JDT_UTILS;
        IJavaProject javaProject = loadJavaProject("jakarta-sample", "");

        IFile javaFile = javaProject.getProject().getFile(
                new Path("src/main/java/io/openliberty/sample/jakarta/beanvalidation/MethodConstraintValidation.java"));
        String uri = javaFile.getLocation().toFile().toURI().toString();

        JakartaDiagnosticsParams diagnosticsParams = new JakartaDiagnosticsParams();
        diagnosticsParams.setUris(Arrays.asList(uri));

        // Test diagnostics -- should only be two
        Diagnostic d1 = d(20, 26, 38,
                "Constraint annotations are not allowed on static methods",
                DiagnosticSeverity.Error, "jakarta-bean-validation", "MakeNotStatic");
        Diagnostic d2 = d(25, 18, 28,
                "The @AssertTrue annotation can only be used on boolean and Boolean type methods.",
                DiagnosticSeverity.Error, "jakarta-bean-validation", "FixTypeOfElement");

        assertJavaDiagnostics(diagnosticsParams, utils, d1, d2);

        // Test quickfix codeActions
        JakartaJavaCodeActionParams codeActionParams = createCodeActionParams(uri, d1);
        TextEdit te = te(19, 4, 20, 4, "");
        TextEdit te2 = te(0, 0, 0, 0, "");
        CodeAction ca = ca(uri, "Remove constraint annotation from element", d1, te);
        CodeAction ca2 = ca(uri, "Remove static modifier from element", d1, te2);

        assertJavaCodeAction(codeActionParams, utils, ca, ca2);

        codeActionParams = createCodeActionParams(uri, d2);
        te = te(24, 4, 25, 4, "");
        ca = ca(uri, "Remove constraint annotation from element", d2, te);    

        assertJavaCodeAction(codeActionParams, utils, ca);
    }
}
