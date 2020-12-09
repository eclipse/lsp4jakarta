package org.jakarta.jdt.beanvalidation;

import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.ASSERT_FALSE;
import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.ASSERT_TRUE;
import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.BIG_DECIMAL;
import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.BIG_INTEGER;
import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.BOOLEAN;
import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.BYTE;
import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.CALENDAR;
import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.CHAR_SEQUENCE;
import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.DATE;
import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.DECIMAL_MAX;
import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.DECIMAL_MIN;
import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.DIAGNOSTIC_CODE_FIELD;
import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.DIAGNOSTIC_CODE_Static;
import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.DIAGNOSTIC_SOURCE;
import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.DIGITS;
import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.DOUBLE;
import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.EMAIL;
import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.FLOAT;
import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.FUTURE;
import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.FUTURE_OR_PRESENT;
import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.HIJRAH_DATE;
import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.INSTANT;
import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.INTEGER;
import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.JAPANESE_DATE;
import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.LOCAL_DATE;
import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.LOCAL_DATE_TIME;
import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.LOCAL_TIME;
import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.LONG;
import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.MAX;
import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.MIN;
import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.MINGUO_DATE;
import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.MONTH_DAY;
import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.NEGATIVE;
import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.NEGATIVE_OR_ZERO;
import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.NOT_BLANK;
import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.OFFSET_DATE_TIME;
import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.OFFSET_TIME;
import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.PAST;
import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.PAST_OR_PRESENT;
import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.PATTERN;
import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.POSITIVE;
import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.POSTIVE_OR_ZERO;
import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.SET_OF_ANNOTATIONS;
import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.SEVERITY;
import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.SHORT;
import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.STRING;
import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.THAI_BUDDHIST_DATE;
import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.YEAR;
import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.YEAR_MONTH;
import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.ZONED_DATE_TIME;

import java.util.List;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Range;
import org.jakarta.jdt.DiagnosticsCollector;
import org.jakarta.jdt.JDTUtils;
import org.jakarta.lsp4e.Activator;

public class BeanValidationDiagnosticsCollector implements DiagnosticsCollector {

    @Override
    public void completeDiagnostic(Diagnostic diagnostic) {
        diagnostic.setSource(DIAGNOSTIC_SOURCE);
        diagnostic.setSeverity(SEVERITY);
    }

    public void collectDiagnostics(ICompilationUnit unit, List<Diagnostic> diagnostics) {

        if (unit != null) {
            IType[] alltypes;
            IField[] allFields;
            IAnnotation[] allFieldAnnotations;

            try {
                alltypes = unit.getAllTypes();
                for (IType type : alltypes) {
                    allFields = type.getFields();
                    for (IField field : allFields) {

                        allFieldAnnotations = field.getAnnotations();
                        String fieldType = field.getTypeSignature();

                        for (IAnnotation annotation : allFieldAnnotations) {
                            if (SET_OF_ANNOTATIONS.contains(annotation.getElementName())) {
                                checkAnnotationAllowedTypes(unit, diagnostics, fieldType, annotation);

                                if (Flags.isStatic(field.getFlags())) {
                                    ISourceRange fieldAnnotationNameRange = JDTUtils.getNameRange(annotation);
                                    Range fieldAnnotationrange = JDTUtils.toRange(unit,
                                            fieldAnnotationNameRange.getOffset(), fieldAnnotationNameRange.getLength());

                                    Diagnostic diagnostic = new Diagnostic(fieldAnnotationrange,
                                            "Constraint annotations are not allowed on static fields");
                                    diagnostic.setSource(DIAGNOSTIC_SOURCE);
                                    diagnostic.setCode(DIAGNOSTIC_CODE_Static);
                                    diagnostic.setSeverity(SEVERITY);
                                    diagnostics.add(diagnostic);
                                }
                            }

                        }

                    }
                }

            } catch (JavaModelException e) {
                Activator.logException("Cannot calculate diagnostics", e);
            }
        }

    }

    private void checkAnnotationAllowedTypes(ICompilationUnit unit, List<Diagnostic> diagnostics, String fieldType,
            IAnnotation annotation) throws JavaModelException {

        ISourceRange fieldAnnotationNameRange = JDTUtils.getNameRange(annotation);
        Range fieldAnnotationrange = JDTUtils.toRange(unit, fieldAnnotationNameRange.getOffset(),
                fieldAnnotationNameRange.getLength());

        if (annotation.getElementName().equals(ASSERT_FALSE) || annotation.getElementName().equals(ASSERT_TRUE)) {

            if (!fieldType.equals(getSignatureFormatOfType(BOOLEAN)) && !fieldType.equals(Signature.SIG_BOOLEAN)) {

                Diagnostic diagnostic = new Diagnostic(fieldAnnotationrange, "The @" + annotation.getElementName()
                        + " annotation can only be used on boolean and Boolean type fields.");
                diagnostic.setCode(DIAGNOSTIC_CODE_FIELD);
                completeDiagnostic(diagnostic);
                diagnostics.add(diagnostic);
            }
        } else if (annotation.getElementName().equals(DECIMAL_MAX) || annotation.getElementName().equals(DECIMAL_MIN)
                || annotation.getElementName().equals(DIGITS)) {

            if (!fieldType.equals(getSignatureFormatOfType(BIG_DECIMAL))
                    && !fieldType.equals(getSignatureFormatOfType(BIG_INTEGER))
                    && !fieldType.equals(getSignatureFormatOfType(CHAR_SEQUENCE))
                    && !fieldType.equals(getSignatureFormatOfType(BYTE))
                    && !fieldType.equals(getSignatureFormatOfType(SHORT))
                    && !fieldType.equals(getSignatureFormatOfType(INTEGER))
                    && !fieldType.equals(getSignatureFormatOfType(LONG)) && !fieldType.equals(Signature.SIG_BYTE)
                    && !fieldType.equals(Signature.SIG_SHORT) && !fieldType.equals(Signature.SIG_INT)
                    && !fieldType.equals(Signature.SIG_LONG)) {

                Diagnostic diagnostic = new Diagnostic(fieldAnnotationrange,
                        "The @" + annotation.getElementName()
                                + " annotation can only be used on: \n- BigDecimal \n- BigInteger \n- CharSequence"
                                + "\n- byte, short, int, long (and their respective wrappers) \n type fields.");
                diagnostic.setCode(DIAGNOSTIC_CODE_FIELD);
                completeDiagnostic(diagnostic);
                diagnostics.add(diagnostic);
            }
        } else if (annotation.getElementName().equals(EMAIL)) {

            if (!fieldType.equals(getSignatureFormatOfType(STRING))
                    && !fieldType.equals(getSignatureFormatOfType(CHAR_SEQUENCE))) {

                Diagnostic diagnostic = new Diagnostic(fieldAnnotationrange, "The @" + annotation.getElementName()
                        + " annotation can only be used on String and CharSequence type fields.");
                diagnostic.setCode(DIAGNOSTIC_CODE_FIELD);
                completeDiagnostic(diagnostic);
                diagnostics.add(diagnostic);
            }
        } else if (annotation.getElementName().equals(FUTURE) || annotation.getElementName().equals(FUTURE_OR_PRESENT)
                || annotation.getElementName().equals(PAST) || annotation.getElementName().equals(PAST_OR_PRESENT)) {

            if (!fieldType.equals(getSignatureFormatOfType(DATE))
                    && !fieldType.equals(getSignatureFormatOfType(CALENDAR))
                    && !fieldType.equals(getSignatureFormatOfType(INSTANT))
                    && !fieldType.equals(getSignatureFormatOfType(LOCAL_DATE))
                    && !fieldType.equals(getSignatureFormatOfType(LOCAL_DATE_TIME))
                    && !fieldType.equals(getSignatureFormatOfType(LOCAL_TIME))
                    && !fieldType.equals(getSignatureFormatOfType(MONTH_DAY))
                    && !fieldType.equals(getSignatureFormatOfType(OFFSET_DATE_TIME))
                    && !fieldType.equals(getSignatureFormatOfType(OFFSET_TIME))
                    && !fieldType.equals(getSignatureFormatOfType(YEAR))
                    && !fieldType.equals(getSignatureFormatOfType(YEAR_MONTH))
                    && !fieldType.equals(getSignatureFormatOfType(ZONED_DATE_TIME))
                    && !fieldType.equals(getSignatureFormatOfType(HIJRAH_DATE))
                    && !fieldType.equals(getSignatureFormatOfType(JAPANESE_DATE))
                    && !fieldType.equals(getSignatureFormatOfType(MINGUO_DATE))
                    && !fieldType.equals(getSignatureFormatOfType(THAI_BUDDHIST_DATE))) {

                Diagnostic diagnostic = new Diagnostic(fieldAnnotationrange, "The @" + annotation.getElementName()
                        + " annotation can only be used on: Date, Calendar, Instant, "
                        + "LocalDate, LocalDateTime, LocalTime, MonthDay, OffsetDateTime, "
                        + "OffsetTime, Year, YearMonth, ZonedDateTime, "
                        + "HijrahDate, JapaneseDate, JapaneseDate, MinguoDate and " + "ThaiBuddhistDate type fields.");
                diagnostic.setCode(DIAGNOSTIC_CODE_FIELD);
                completeDiagnostic(diagnostic);
                diagnostics.add(diagnostic);
            }
        } else if (annotation.getElementName().equals(MIN) || annotation.getElementName().equals(MAX)) {

            if (!fieldType.equals(getSignatureFormatOfType(BIG_DECIMAL))
                    && !fieldType.equals(getSignatureFormatOfType(BIG_INTEGER))
                    && !fieldType.equals(getSignatureFormatOfType(BYTE))
                    && !fieldType.equals(getSignatureFormatOfType(SHORT))
                    && !fieldType.equals(getSignatureFormatOfType(INTEGER))
                    && !fieldType.equals(getSignatureFormatOfType(LONG)) && !fieldType.equals(Signature.SIG_BYTE)
                    && !fieldType.equals(Signature.SIG_SHORT) && !fieldType.equals(Signature.SIG_INT)
                    && !fieldType.equals(Signature.SIG_LONG)) {

                Diagnostic diagnostic = new Diagnostic(fieldAnnotationrange,
                        "The @" + annotation.getElementName()
                                + " annotation can only be used on \n- BigDecimal \n- BigInteger"
                                + "\n- byte, short, int, long (and their respective wrappers) \n type fields.");
                diagnostic.setCode(DIAGNOSTIC_CODE_FIELD);
                completeDiagnostic(diagnostic);
                diagnostics.add(diagnostic);
            }
        } else if (annotation.getElementName().equals(NEGATIVE) || annotation.getElementName().equals(NEGATIVE_OR_ZERO)
                || annotation.getElementName().equals(POSITIVE)
                || annotation.getElementName().equals(POSTIVE_OR_ZERO)) {

            if (!fieldType.equals(getSignatureFormatOfType(BIG_DECIMAL))
                    && !fieldType.equals(getSignatureFormatOfType(BIG_INTEGER))
                    && !fieldType.equals(getSignatureFormatOfType(BYTE))
                    && !fieldType.equals(getSignatureFormatOfType(SHORT))
                    && !fieldType.equals(getSignatureFormatOfType(INTEGER))
                    && !fieldType.equals(getSignatureFormatOfType(LONG))
                    && !fieldType.equals(getSignatureFormatOfType(FLOAT))
                    && !fieldType.equals(getSignatureFormatOfType(DOUBLE)) && !fieldType.equals(Signature.SIG_BYTE)
                    && !fieldType.equals(Signature.SIG_SHORT) && !fieldType.equals(Signature.SIG_INT)
                    && !fieldType.equals(Signature.SIG_LONG) && !fieldType.equals(Signature.SIG_FLOAT)
                    && !fieldType.equals(Signature.SIG_DOUBLE)) {

                Diagnostic diagnostic = new Diagnostic(fieldAnnotationrange, "The @" + annotation.getElementName()
                        + " annotation can only be used on \n- BigDecimal \n- BigInteger"
                        + "\n- byte, short, int, long, float, double (and their respective wrappers) \n type fields.");
                diagnostic.setCode(DIAGNOSTIC_CODE_FIELD);
                completeDiagnostic(diagnostic);
                diagnostics.add(diagnostic);
            }
        } else if (annotation.getElementName().equals(NOT_BLANK)) {

            if (!fieldType.equals(getSignatureFormatOfType(STRING))
                    && !fieldType.equals(getSignatureFormatOfType(CHAR_SEQUENCE))) {

                Diagnostic diagnostic = new Diagnostic(fieldAnnotationrange, "The @" + annotation.getElementName()
                        + " annotation can only be used on String and CharSequence type fields.");
                diagnostic.setCode(DIAGNOSTIC_CODE_FIELD);
                completeDiagnostic(diagnostic);
                diagnostics.add(diagnostic);
            }
        } else if (annotation.getElementName().equals(PATTERN)) {

            if (!fieldType.equals(getSignatureFormatOfType(STRING))
                    && !fieldType.equals(getSignatureFormatOfType(CHAR_SEQUENCE))) {

                Diagnostic diagnostic = new Diagnostic(fieldAnnotationrange, "The @" + annotation.getElementName()
                        + " annotation can only be used on String and CharSequence type fields.");
                diagnostic.setCode(DIAGNOSTIC_CODE_FIELD);
                completeDiagnostic(diagnostic);
                diagnostics.add(diagnostic);
            }
        }

        // These ones contains check on all collection types which requires resolving
        // the String of the type somehow
        // This will also require us to check if the field type was a custom collection
        // subtype which means we
        // have to resolve it and get the super interfaces and check to see if
        // Collection, Map or Array was implemented
        // for that custom type (which could as well be a user made subtype)

//		else if (annotation.getElementName().equals(NOT_EMPTY) || annotation.getElementName().equals(SIZE)) {
//			
//			System.out.println("--Field name: " + Signature.getTypeSignatureKind(fieldType));
//			System.out.println("--Field name: " + Signature.getParameterTypes(fieldType));			
//			if (	!fieldType.equals(getSignatureFormatOfType(CHAR_SEQUENCE)) &&
//					!fieldType.contains("List") &&
//					!fieldType.contains("Set") &&
//					!fieldType.contains("Collection") &&
//					!fieldType.contains("Array") &&
//					!fieldType.contains("Vector") &&
//					!fieldType.contains("Stack") &&
//					!fieldType.contains("Queue") &&
//					!fieldType.contains("Deque") &&
//					!fieldType.contains("Map")) {
//				
//				diagnostics.add(new Diagnostic(fieldAnnotationrange,
//						"This annotation can only be used on CharSequence, Collection, Array, "
//						+ "Map type fields."));	
//			}
//		}
    }

    /*
     * Refer to Class signature documentation for the formating
     * https://www.ibm.com/support/knowledgecenter/sl/SS5JSH_9.5.0/org.eclipse.jdt.
     * doc.isv/reference/api/org/eclipse/jdt/core/Signature.html
     */
    private String getSignatureFormatOfType(String type) {
        return "Q" + type + ";";
    }

}