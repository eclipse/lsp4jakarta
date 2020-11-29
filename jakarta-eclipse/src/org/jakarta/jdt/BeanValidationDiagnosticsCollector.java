package org.jakarta.jdt;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Range;
import org.jakarta.lsp4e.Activator;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.Flags;

public class BeanValidationDiagnosticsCollector  implements DiagnosticsCollector {

	/* Annotations */
	private static final String ASSERT_TRUE = "AssertTrue";
	private static final String ASSERT_FALSE = "AssertFalse";
	private static final String DIGITS = "Digits";
	private static final String DECIMAL_MAX = "DecimalMax";
	private static final String DECIMAL_MIN = "DecimalMin";
	private static final String EMAIL = "Email";
	private static final String PAST_OR_PRESENT = "PastOrPresent";
	private static final String FUTURE_OR_PRESENT = "FutureOrPresent";
	private static final String PAST = "Past";
	private static final String FUTURE = "Future";
	private static final String MIN = "Min";
	private static final String MAX = "Max";
	private static final String NEGATIVE_OR_ZERO = "NegativeOrZero";
	private static final String POSTIVE_OR_ZERO = "PostiveOrZero";
	private static final String NEGATIVE = "Negative";
	private static final String POSITIVE = "Positive";
	private static final String NOT_BLANK = "NotBlank";
	private static final String PATTERN = "Pattern";
	private static final String SIZE = "Size";
	private static final String NOT_EMPTY = "NotEmpty";
	
	/* Types */
	private static final String THAI_BUDDHIST_DATE = "ThaiBuddhistDate";
	private static final String MINGUO_DATE = "MinguoDate";
	private static final String JAPANESE_DATE = "JapaneseDate";
	private static final String HIJRAH_DATE = "HijrahDate";
	private static final String ZONED_DATE_TIME = "ZonedDateTime";
	private static final String YEAR_MONTH = "YearMonth";
	private static final String YEAR = "Year";
	private static final String OFFSET_TIME = "OffsetTime";
	private static final String OFFSET_DATE_TIME = "OffsetDateTime";
	private static final String MONTH_DAY = "MonthDay";
	private static final String LOCAL_TIME = "LocalTime";
	private static final String LOCAL_DATE_TIME = "LocalDateTime";
	private static final String LOCAL_DATE = "LocalDate";
	private static final String INSTANT = "Instant";
	private static final String CALENDAR = "Calendar";
	private static final String DATE = "Date";
	private static final String BOOLEAN = "Boolean";
	private static final String CHAR_SEQUENCE = "CharSequence";
	private static final String STRING = "String";
	private static final String DOUBLE = "Double";
	private static final String FLOAT = "Float";
	private static final String LONG = "Long";
	private static final String INTEGER = "Integer";
	private static final String SHORT = "Short";
	private static final String BYTE = "Byte";
	private static final String BIG_INTEGER = "BigInteger";
	private static final String BIG_DECIMAL = "BigDecimal";
	
    public final static Set<String> SET_OF_ANNOTATIONS = Collections.unmodifiableSet(
            new HashSet<String>(Arrays.asList(
            		ASSERT_TRUE, 
            		ASSERT_FALSE,
            		DIGITS,
            		DECIMAL_MAX,
            		DECIMAL_MIN,
            		EMAIL,
            		PAST_OR_PRESENT,
            		FUTURE_OR_PRESENT,
            		PAST,
            		FUTURE,
            		MIN,
            		MAX,
            		NEGATIVE_OR_ZERO,
            		POSTIVE_OR_ZERO,
            		NEGATIVE,
            		POSITIVE,
            		NOT_BLANK,
            		PATTERN,
            		SIZE,
            		NOT_EMPTY
                  )));

	
	public void collectDiagnostics(ICompilationUnit unit, List<Diagnostic> diagnostics) {

		if (unit != null) {
			IType[] alltypes;
			IField[] allFields;
			IAnnotation[] allFieldAnnotations;

			try {
				alltypes = unit.getAllTypes();
				for (IType type : alltypes) {
					allFields = type.getFields();
					for(IField field: allFields) {


						allFieldAnnotations = field.getAnnotations();
						String fieldType = field.getTypeSignature();

						for (IAnnotation annotation : allFieldAnnotations) {
							if(SET_OF_ANNOTATIONS.contains(annotation.getElementName())) {
								checkAnnoatationAllowedTypes(unit, diagnostics, fieldType, annotation);
								
								
								if (Flags.isStatic(field.getFlags())) {
									ISourceRange fieldAnnotationNameRange = JDTUtils.getNameRange(annotation);
									Range fieldAnnotationrange = JDTUtils.toRange(unit, fieldAnnotationNameRange.getOffset(),
											fieldAnnotationNameRange.getLength());
									diagnostics.add(new Diagnostic(fieldAnnotationrange,
											"Constraint Annotations are not allowed on static fields"));	
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

	private void checkAnnoatationAllowedTypes(ICompilationUnit unit, List<Diagnostic> diagnostics, String fieldType,
			IAnnotation annotation) throws JavaModelException {
		
		ISourceRange fieldAnnotationNameRange = JDTUtils.getNameRange(annotation);
		Range fieldAnnotationrange = JDTUtils.toRange(unit, fieldAnnotationNameRange.getOffset(),
				fieldAnnotationNameRange.getLength());
		
		if (annotation.getElementName().equals(ASSERT_FALSE)|| annotation.getElementName().equals(ASSERT_TRUE)) {

			if (	!fieldType.equals(getSignatureFormatOfType(BOOLEAN)) &&
					!fieldType.equals(Signature.SIG_BOOLEAN)) {
				
				diagnostics.add(new Diagnostic(fieldAnnotationrange,
						"This annotation can only be used on boolean and Boolean type fields."));	
			}
		} else if (annotation.getElementName().equals(DECIMAL_MAX)|| annotation.getElementName().equals(DECIMAL_MIN) 
				|| annotation.getElementName().equals(DIGITS) ) {

			if (	!fieldType.equals(getSignatureFormatOfType(BIG_DECIMAL)) &&
					!fieldType.equals(getSignatureFormatOfType(BIG_INTEGER)) &&
					!fieldType.equals(getSignatureFormatOfType(CHAR_SEQUENCE)) &&
					!fieldType.equals(getSignatureFormatOfType(BYTE)) &&
					!fieldType.equals(getSignatureFormatOfType(SHORT)) &&
					!fieldType.equals(getSignatureFormatOfType(INTEGER)) &&
					!fieldType.equals(getSignatureFormatOfType(LONG)) &&
					!fieldType.equals(Signature.SIG_BYTE) &&
					!fieldType.equals(Signature.SIG_SHORT) &&
					!fieldType.equals(Signature.SIG_INT) &&
					!fieldType.equals(Signature.SIG_LONG)) {
				
				diagnostics.add(new Diagnostic(fieldAnnotationrange,
						"This annotation can only be used on BigDecimal, BigInteger, CharSequence"
						+ "byte, short, int, long (and their respective wrappers) type fields."));	
			}
		} else if (annotation.getElementName().equals(EMAIL)) {

			if (	!fieldType.equals(getSignatureFormatOfType(STRING)) &&
					!fieldType.equals(getSignatureFormatOfType(CHAR_SEQUENCE))) {
				
				diagnostics.add(new Diagnostic(fieldAnnotationrange,
						"This annotation can only be used on String and CharSequence type fields."));	
			}
		} else if (annotation.getElementName().equals(FUTURE)|| annotation.getElementName().equals(FUTURE_OR_PRESENT) 
				|| annotation.getElementName().equals(PAST) || annotation.getElementName().equals(PAST_OR_PRESENT)) {

			if (	!fieldType.equals(getSignatureFormatOfType(DATE)) &&
					!fieldType.equals(getSignatureFormatOfType(CALENDAR)) &&
					!fieldType.equals(getSignatureFormatOfType(INSTANT)) &&
					!fieldType.equals(getSignatureFormatOfType(LOCAL_DATE)) &&
					!fieldType.equals(getSignatureFormatOfType(LOCAL_DATE_TIME)) &&
					!fieldType.equals(getSignatureFormatOfType(LOCAL_TIME)) &&
					!fieldType.equals(getSignatureFormatOfType(MONTH_DAY)) &&
					!fieldType.equals(getSignatureFormatOfType(OFFSET_DATE_TIME)) &&
					!fieldType.equals(getSignatureFormatOfType(OFFSET_TIME)) &&
					!fieldType.equals(getSignatureFormatOfType(YEAR)) &&
					!fieldType.equals(getSignatureFormatOfType(YEAR_MONTH)) &&
					!fieldType.equals(getSignatureFormatOfType(ZONED_DATE_TIME)) &&
					!fieldType.equals(getSignatureFormatOfType(HIJRAH_DATE)) &&
					!fieldType.equals(getSignatureFormatOfType(JAPANESE_DATE)) &&
					!fieldType.equals(getSignatureFormatOfType(MINGUO_DATE)) &&
					!fieldType.equals(getSignatureFormatOfType(THAI_BUDDHIST_DATE))) {
				
				diagnostics.add(new Diagnostic(fieldAnnotationrange,
						"This annotation can only be used on Date, Calendar, Instant"
						+ "LocalDate, LocalDateTime, LocalTime, MonthDay, OffsetDateTime, "
						+ "OffsetTime, Year, YearMonth, ZonedDateTime, "
						+ "HijrahDate, JapaneseDate, JapaneseDate, MinguoDate, "
						+ "ThaiBuddhistDate type fields."));	
			}
		} else if (annotation.getElementName().equals(MIN)|| annotation.getElementName().equals(MAX)) {

			if (	!fieldType.equals(getSignatureFormatOfType(BIG_DECIMAL)) &&
					!fieldType.equals(getSignatureFormatOfType(BIG_INTEGER)) &&
					!fieldType.equals(getSignatureFormatOfType(BYTE)) &&
					!fieldType.equals(getSignatureFormatOfType(SHORT)) &&
					!fieldType.equals(getSignatureFormatOfType(INTEGER)) &&
					!fieldType.equals(getSignatureFormatOfType(LONG)) &&
					!fieldType.equals(Signature.SIG_BYTE) &&
					!fieldType.equals(Signature.SIG_SHORT) &&
					!fieldType.equals(Signature.SIG_INT) &&
					!fieldType.equals(Signature.SIG_LONG)) {
				
				diagnostics.add(new Diagnostic(fieldAnnotationrange,
						"This annotation can only be used on BigDecimal, BigInteger"
						+ "byte, short, int, long (and their respective wrappers) type fields."));	
			}
		} else if (annotation.getElementName().equals(NEGATIVE)|| annotation.getElementName().equals(NEGATIVE_OR_ZERO) || 
				annotation.getElementName().equals(POSITIVE)|| annotation.getElementName().equals(POSTIVE_OR_ZERO)) {

			if (	!fieldType.equals(getSignatureFormatOfType(BIG_DECIMAL)) &&
					!fieldType.equals(getSignatureFormatOfType(BIG_INTEGER)) &&
					!fieldType.equals(getSignatureFormatOfType(BYTE)) &&
					!fieldType.equals(getSignatureFormatOfType(SHORT)) &&
					!fieldType.equals(getSignatureFormatOfType(INTEGER)) &&
					!fieldType.equals(getSignatureFormatOfType(LONG)) &&
					!fieldType.equals(getSignatureFormatOfType(FLOAT)) &&
					!fieldType.equals(getSignatureFormatOfType(DOUBLE)) &&
					!fieldType.equals(Signature.SIG_BYTE) &&
					!fieldType.equals(Signature.SIG_SHORT) &&
					!fieldType.equals(Signature.SIG_INT) &&
					!fieldType.equals(Signature.SIG_LONG) &&
					!fieldType.equals(Signature.SIG_FLOAT) &&
					!fieldType.equals(Signature.SIG_DOUBLE)) {
				
				diagnostics.add(new Diagnostic(fieldAnnotationrange,
						"This annotation can only be used on BigDecimal, BigInteger"
						+ "byte, short, int, long, float, double (and their respective wrappers) type fields."));	
			}
		} else if (annotation.getElementName().equals(NOT_BLANK)) {

			if (	!fieldType.equals(getSignatureFormatOfType(STRING)) &&
					!fieldType.equals(getSignatureFormatOfType(CHAR_SEQUENCE))) {
				
				diagnostics.add(new Diagnostic(fieldAnnotationrange,
						"This annotation can only be used on String and CharSequence type fields."));	
			}
		} else if (annotation.getElementName().equals(PATTERN)) {
			
			if (	!fieldType.equals(getSignatureFormatOfType(STRING)) &&
					!fieldType.equals(getSignatureFormatOfType(CHAR_SEQUENCE))) {
				
				diagnostics.add(new Diagnostic(fieldAnnotationrange,
						"This annotation can only be used on String and CharSequence type fields."));	
			}
		}
		
		// These ones contains check on all collection types which requires resolving the String of the type somehow
		// This will also require us to check if the field type was a custom collection subtype which means we 
		// have to resolve it and get the super interfaces and check to see if Collection, Map or Array was implemented 
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
	
	/* Refer to Class signature documentation for the formating
	 * https://www.ibm.com/support/knowledgecenter/sl/SS5JSH_9.5.0/org.eclipse.jdt.doc.isv/reference/api/org/eclipse/jdt/core/Signature.html
	 */
	private String getSignatureFormatOfType(String type) {
		return "Q" + type + ";";
	}

}