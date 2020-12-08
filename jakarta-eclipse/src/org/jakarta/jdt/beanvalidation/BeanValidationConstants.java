package org.jakarta.jdt.beanvalidation;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.lsp4j.DiagnosticSeverity;

public class BeanValidationConstants {

    /* Annotations */
    public static final String ASSERT_TRUE = "AssertTrue";
    public static final String ASSERT_FALSE = "AssertFalse";
    public static final String DIGITS = "Digits";
    public static final String DECIMAL_MAX = "DecimalMax";
    public static final String DECIMAL_MIN = "DecimalMin";
    public static final String EMAIL = "Email";
    public static final String PAST_OR_PRESENT = "PastOrPresent";
    public static final String FUTURE_OR_PRESENT = "FutureOrPresent";
    public static final String PAST = "Past";
    public static final String FUTURE = "Future";
    public static final String MIN = "Min";
    public static final String MAX = "Max";
    public static final String NEGATIVE_OR_ZERO = "NegativeOrZero";
    public static final String POSTIVE_OR_ZERO = "PostiveOrZero";
    public static final String NEGATIVE = "Negative";
    public static final String POSITIVE = "Positive";
    public static final String NOT_BLANK = "NotBlank";
    public static final String PATTERN = "Pattern";
    public static final String SIZE = "Size";
    public static final String NOT_EMPTY = "NotEmpty";

    /* Types */
    public static final String THAI_BUDDHIST_DATE = "ThaiBuddhistDate";
    public static final String MINGUO_DATE = "MinguoDate";
    public static final String JAPANESE_DATE = "JapaneseDate";
    public static final String HIJRAH_DATE = "HijrahDate";
    public static final String ZONED_DATE_TIME = "ZonedDateTime";
    public static final String YEAR_MONTH = "YearMonth";
    public static final String YEAR = "Year";
    public static final String OFFSET_TIME = "OffsetTime";
    public static final String OFFSET_DATE_TIME = "OffsetDateTime";
    public static final String MONTH_DAY = "MonthDay";
    public static final String LOCAL_TIME = "LocalTime";
    public static final String LOCAL_DATE_TIME = "LocalDateTime";
    public static final String LOCAL_DATE = "LocalDate";
    public static final String INSTANT = "Instant";
    public static final String CALENDAR = "Calendar";
    public static final String DATE = "Date";
    public static final String BOOLEAN = "Boolean";
    public static final String CHAR_SEQUENCE = "CharSequence";
    public static final String STRING = "String";
    public static final String DOUBLE = "Double";
    public static final String FLOAT = "Float";
    public static final String LONG = "Long";
    public static final String INTEGER = "Integer";
    public static final String SHORT = "Short";
    public static final String BYTE = "Byte";
    public static final String BIG_INTEGER = "BigInteger";
    public static final String BIG_DECIMAL = "BigDecimal";

    public static final String DIAGNOSTIC_SOURCE = "jakarta-bean-validation";
    public static final String DIAGNOSTIC_CODE_FIELD = "FixTypeOfField";
    public static final String DIAGNOSTIC_CODE_Static = "MakeFieldNotStatic";
    public static final DiagnosticSeverity SEVERITY = DiagnosticSeverity.Error;

    public final static Set<String> SET_OF_ANNOTATIONS = Collections
            .unmodifiableSet(new HashSet<String>(Arrays.asList(ASSERT_TRUE, ASSERT_FALSE, DIGITS, DECIMAL_MAX,
                    DECIMAL_MIN, EMAIL, PAST_OR_PRESENT, FUTURE_OR_PRESENT, PAST, FUTURE, MIN, MAX, NEGATIVE_OR_ZERO,
                    POSTIVE_OR_ZERO, NEGATIVE, POSITIVE, NOT_BLANK, PATTERN, SIZE, NOT_EMPTY)));

}
