/*******************************************************************************
* Copyright (c) 2020 IBM Corporation, Reza Akhavan and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0.
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     IBM Corporation, Reza Akhavan - initial API and implementation
*******************************************************************************/

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
import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.DIAGNOSTIC_CODE_METHOD;
import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.DIAGNOSTIC_CODE_STATIC;
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
import org.eclipse.jdt.core.IMethod;
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
            IMethod[] allMethods;
            IAnnotation[] allMethodAnnotations;

            try {
                alltypes = unit.getAllTypes();
                for (IType type : alltypes) {
                    allFields = type.getFields();
                    for (IField field : allFields) {
                        allFieldAnnotations = field.getAnnotations();
                        String fieldType = field.getTypeSignature();
                        ISourceRange fieldNameRange = JDTUtils.getNameRange(field);
                        Range fieldRange = JDTUtils.toRange(unit, fieldNameRange.getOffset(),
                                fieldNameRange.getLength());

                        for (IAnnotation annotation : allFieldAnnotations) {
                            if (SET_OF_ANNOTATIONS.contains(annotation.getElementName())) {
                                checkAnnotationAllowedTypes(unit, diagnostics, fieldType, annotation, fieldRange);

                                if (Flags.isStatic(field.getFlags())) {
                                    Diagnostic diagnostic = new Diagnostic(fieldRange,
                                            "Constraint annotations are not allowed on static fields");
                                    diagnostic.setSource(DIAGNOSTIC_SOURCE);
                                    diagnostic.setCode(DIAGNOSTIC_CODE_STATIC);
                                    diagnostic.setSeverity(SEVERITY);
                                    diagnostics.add(diagnostic);
                                }
                            }
                        }
                    }
                    allMethods = type.getMethods();
                    for (IMethod method : allMethods) {
                        allMethodAnnotations = method.getAnnotations();
                        String returnType = method.getReturnType();
                        ISourceRange methodNameRange = JDTUtils.getNameRange(method);
                        Range methodRange = JDTUtils.toRange(unit, methodNameRange.getOffset(),
                                methodNameRange.getLength());
                        
                        for (IAnnotation annotation : allMethodAnnotations) {
                            if (SET_OF_ANNOTATIONS.contains(annotation.getElementName())) {
                                checkAnnotationAllowedTypes(unit, diagnostics, returnType, annotation, methodRange, true);
                                
                                if (Flags.isStatic(method.getFlags())) {
                                    Diagnostic diagnostic = new Diagnostic(methodRange,
                                            "Constraint annotations are not allowed on static methods");
                                    diagnostic.setSource(DIAGNOSTIC_SOURCE);
                                    diagnostic.setCode(DIAGNOSTIC_CODE_STATIC);
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

    private void checkAnnotationAllowedTypes(ICompilationUnit unit, List<Diagnostic> diagnostics, String type,
            IAnnotation annotation, Range range) throws JavaModelException {
        checkAnnotationAllowedTypes(unit, diagnostics, type, annotation, range, false);
    }
    
    private void checkAnnotationAllowedTypes(ICompilationUnit unit, List<Diagnostic> diagnostics, String type,
            IAnnotation annotation, Range range, Boolean isMethod) throws JavaModelException {
        String source = isMethod ? "methods." : "fields.";
        String code = isMethod ? DIAGNOSTIC_CODE_METHOD : DIAGNOSTIC_CODE_FIELD; 
        
        if (annotation.getElementName().equals(ASSERT_FALSE) || annotation.getElementName().equals(ASSERT_TRUE)) {

            if (!type.equals(getSignatureFormatOfType(BOOLEAN)) && !type.equals(Signature.SIG_BOOLEAN)) {
                
                Diagnostic diagnostic = new Diagnostic(range, "The @" + annotation.getElementName()
                        + " annotation can only be used on boolean and Boolean type " + source);
                diagnostic.setCode(code);
                completeDiagnostic(diagnostic);
                diagnostics.add(diagnostic);
            }
        } else if (annotation.getElementName().equals(DECIMAL_MAX) || annotation.getElementName().equals(DECIMAL_MIN)
                || annotation.getElementName().equals(DIGITS)) {

            if (!type.equals(getSignatureFormatOfType(BIG_DECIMAL))
                    && !type.equals(getSignatureFormatOfType(BIG_INTEGER))
                    && !type.equals(getSignatureFormatOfType(CHAR_SEQUENCE))
                    && !type.equals(getSignatureFormatOfType(BYTE))
                    && !type.equals(getSignatureFormatOfType(SHORT))
                    && !type.equals(getSignatureFormatOfType(INTEGER))
                    && !type.equals(getSignatureFormatOfType(LONG)) && !type.equals(Signature.SIG_BYTE)
                    && !type.equals(Signature.SIG_SHORT) && !type.equals(Signature.SIG_INT)
                    && !type.equals(Signature.SIG_LONG)) {

                Diagnostic diagnostic = new Diagnostic(range,
                        "The @" + annotation.getElementName()
                                + " annotation can only be used on: \n- BigDecimal \n- BigInteger \n- CharSequence"
                                + "\n- byte, short, int, long (and their respective wrappers) \n type " + source);
                diagnostic.setCode(code);
                completeDiagnostic(diagnostic);
                diagnostics.add(diagnostic);
            }
        } else if (annotation.getElementName().equals(EMAIL)) {

            if (!type.equals(getSignatureFormatOfType(STRING))
                    && !type.equals(getSignatureFormatOfType(CHAR_SEQUENCE))) {

                Diagnostic diagnostic = new Diagnostic(range, "The @" + annotation.getElementName()
                        + " annotation can only be used on String and CharSequence type " + source);
                diagnostic.setCode(code);
                completeDiagnostic(diagnostic);
                diagnostics.add(diagnostic);
            }
        } else if (annotation.getElementName().equals(FUTURE) || annotation.getElementName().equals(FUTURE_OR_PRESENT)
                || annotation.getElementName().equals(PAST) || annotation.getElementName().equals(PAST_OR_PRESENT)) {

            if (!type.equals(getSignatureFormatOfType(DATE))
                    && !type.equals(getSignatureFormatOfType(CALENDAR))
                    && !type.equals(getSignatureFormatOfType(INSTANT))
                    && !type.equals(getSignatureFormatOfType(LOCAL_DATE))
                    && !type.equals(getSignatureFormatOfType(LOCAL_DATE_TIME))
                    && !type.equals(getSignatureFormatOfType(LOCAL_TIME))
                    && !type.equals(getSignatureFormatOfType(MONTH_DAY))
                    && !type.equals(getSignatureFormatOfType(OFFSET_DATE_TIME))
                    && !type.equals(getSignatureFormatOfType(OFFSET_TIME))
                    && !type.equals(getSignatureFormatOfType(YEAR))
                    && !type.equals(getSignatureFormatOfType(YEAR_MONTH))
                    && !type.equals(getSignatureFormatOfType(ZONED_DATE_TIME))
                    && !type.equals(getSignatureFormatOfType(HIJRAH_DATE))
                    && !type.equals(getSignatureFormatOfType(JAPANESE_DATE))
                    && !type.equals(getSignatureFormatOfType(MINGUO_DATE))
                    && !type.equals(getSignatureFormatOfType(THAI_BUDDHIST_DATE))) {

                Diagnostic diagnostic = new Diagnostic(range, "The @" + annotation.getElementName()
                        + " annotation can only be used on: Date, Calendar, Instant, "
                        + "LocalDate, LocalDateTime, LocalTime, MonthDay, OffsetDateTime, "
                        + "OffsetTime, Year, YearMonth, ZonedDateTime, "
                        + "HijrahDate, JapaneseDate, JapaneseDate, MinguoDate and " + "ThaiBuddhistDate type " + source);
                diagnostic.setCode(code);
                completeDiagnostic(diagnostic);
                diagnostics.add(diagnostic);
            }
        } else if (annotation.getElementName().equals(MIN) || annotation.getElementName().equals(MAX)) {

            if (!type.equals(getSignatureFormatOfType(BIG_DECIMAL))
                    && !type.equals(getSignatureFormatOfType(BIG_INTEGER))
                    && !type.equals(getSignatureFormatOfType(BYTE))
                    && !type.equals(getSignatureFormatOfType(SHORT))
                    && !type.equals(getSignatureFormatOfType(INTEGER))
                    && !type.equals(getSignatureFormatOfType(LONG)) && !type.equals(Signature.SIG_BYTE)
                    && !type.equals(Signature.SIG_SHORT) && !type.equals(Signature.SIG_INT)
                    && !type.equals(Signature.SIG_LONG)) {

                Diagnostic diagnostic = new Diagnostic(range,
                        "The @" + annotation.getElementName()
                                + " annotation can only be used on \n- BigDecimal \n- BigInteger"
                                + "\n- byte, short, int, long (and their respective wrappers) \n type " + source);
                diagnostic.setCode(code);
                completeDiagnostic(diagnostic);
                diagnostics.add(diagnostic);
            }
        } else if (annotation.getElementName().equals(NEGATIVE) || annotation.getElementName().equals(NEGATIVE_OR_ZERO)
                || annotation.getElementName().equals(POSITIVE)
                || annotation.getElementName().equals(POSTIVE_OR_ZERO)) {

            if (!type.equals(getSignatureFormatOfType(BIG_DECIMAL))
                    && !type.equals(getSignatureFormatOfType(BIG_INTEGER))
                    && !type.equals(getSignatureFormatOfType(BYTE))
                    && !type.equals(getSignatureFormatOfType(SHORT))
                    && !type.equals(getSignatureFormatOfType(INTEGER))
                    && !type.equals(getSignatureFormatOfType(LONG))
                    && !type.equals(getSignatureFormatOfType(FLOAT))
                    && !type.equals(getSignatureFormatOfType(DOUBLE)) && !type.equals(Signature.SIG_BYTE)
                    && !type.equals(Signature.SIG_SHORT) && !type.equals(Signature.SIG_INT)
                    && !type.equals(Signature.SIG_LONG) && !type.equals(Signature.SIG_FLOAT)
                    && !type.equals(Signature.SIG_DOUBLE)) {

                Diagnostic diagnostic = new Diagnostic(range, "The @" + annotation.getElementName()
                        + " annotation can only be used on \n- BigDecimal \n- BigInteger"
                        + "\n- byte, short, int, long, float, double (and their respective wrappers) \n type " + source);
                diagnostic.setCode(code);
                completeDiagnostic(diagnostic);
                diagnostics.add(diagnostic);
            }
        } else if (annotation.getElementName().equals(NOT_BLANK)) {

            if (!type.equals(getSignatureFormatOfType(STRING))
                    && !type.equals(getSignatureFormatOfType(CHAR_SEQUENCE))) {

                Diagnostic diagnostic = new Diagnostic(range, "The @" + annotation.getElementName()
                        + " annotation can only be used on String and CharSequence type " + source);
                diagnostic.setCode(code);
                completeDiagnostic(diagnostic);
                diagnostics.add(diagnostic);
            }
        } else if (annotation.getElementName().equals(PATTERN)) {

            if (!type.equals(getSignatureFormatOfType(STRING))
                    && !type.equals(getSignatureFormatOfType(CHAR_SEQUENCE))) {

                Diagnostic diagnostic = new Diagnostic(range, "The @" + annotation.getElementName()
                        + " annotation can only be used on String and CharSequence type " + source);
                diagnostic.setCode(code);
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