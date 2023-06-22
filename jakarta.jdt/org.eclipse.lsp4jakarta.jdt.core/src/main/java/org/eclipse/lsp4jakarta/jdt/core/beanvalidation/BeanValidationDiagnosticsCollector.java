/*******************************************************************************
* Copyright (c) 2020, 2022 IBM Corporation, Reza Akhavan and others.
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

package org.eclipse.lsp4jakarta.jdt.core.beanvalidation;

import static org.eclipse.lsp4jakarta.jdt.core.beanvalidation.BeanValidationConstants.ASSERT_FALSE;
import static org.eclipse.lsp4jakarta.jdt.core.beanvalidation.BeanValidationConstants.ASSERT_TRUE;
import static org.eclipse.lsp4jakarta.jdt.core.beanvalidation.BeanValidationConstants.BIG_DECIMAL;
import static org.eclipse.lsp4jakarta.jdt.core.beanvalidation.BeanValidationConstants.BIG_INTEGER;
import static org.eclipse.lsp4jakarta.jdt.core.beanvalidation.BeanValidationConstants.BOOLEAN;
import static org.eclipse.lsp4jakarta.jdt.core.beanvalidation.BeanValidationConstants.BYTE;
import static org.eclipse.lsp4jakarta.jdt.core.beanvalidation.BeanValidationConstants.CHAR_SEQUENCE;
import static org.eclipse.lsp4jakarta.jdt.core.beanvalidation.BeanValidationConstants.DECIMAL_MAX;
import static org.eclipse.lsp4jakarta.jdt.core.beanvalidation.BeanValidationConstants.DECIMAL_MIN;
import static org.eclipse.lsp4jakarta.jdt.core.beanvalidation.BeanValidationConstants.DIAGNOSTIC_CODE_INVALID_TYPE;
import static org.eclipse.lsp4jakarta.jdt.core.beanvalidation.BeanValidationConstants.DIAGNOSTIC_CODE_STATIC;
import static org.eclipse.lsp4jakarta.jdt.core.beanvalidation.BeanValidationConstants.DIAGNOSTIC_SOURCE;
import static org.eclipse.lsp4jakarta.jdt.core.beanvalidation.BeanValidationConstants.DIGITS;
import static org.eclipse.lsp4jakarta.jdt.core.beanvalidation.BeanValidationConstants.DOUBLE;
import static org.eclipse.lsp4jakarta.jdt.core.beanvalidation.BeanValidationConstants.EMAIL;
import static org.eclipse.lsp4jakarta.jdt.core.beanvalidation.BeanValidationConstants.FLOAT;
import static org.eclipse.lsp4jakarta.jdt.core.beanvalidation.BeanValidationConstants.FUTURE;
import static org.eclipse.lsp4jakarta.jdt.core.beanvalidation.BeanValidationConstants.FUTURE_OR_PRESENT;
import static org.eclipse.lsp4jakarta.jdt.core.beanvalidation.BeanValidationConstants.INTEGER;
import static org.eclipse.lsp4jakarta.jdt.core.beanvalidation.BeanValidationConstants.LONG;
import static org.eclipse.lsp4jakarta.jdt.core.beanvalidation.BeanValidationConstants.MAX;
import static org.eclipse.lsp4jakarta.jdt.core.beanvalidation.BeanValidationConstants.MIN;
import static org.eclipse.lsp4jakarta.jdt.core.beanvalidation.BeanValidationConstants.NEGATIVE;
import static org.eclipse.lsp4jakarta.jdt.core.beanvalidation.BeanValidationConstants.NEGATIVE_OR_ZERO;
import static org.eclipse.lsp4jakarta.jdt.core.beanvalidation.BeanValidationConstants.NOT_BLANK;
import static org.eclipse.lsp4jakarta.jdt.core.beanvalidation.BeanValidationConstants.PAST;
import static org.eclipse.lsp4jakarta.jdt.core.beanvalidation.BeanValidationConstants.PAST_OR_PRESENT;
import static org.eclipse.lsp4jakarta.jdt.core.beanvalidation.BeanValidationConstants.PATTERN;
import static org.eclipse.lsp4jakarta.jdt.core.beanvalidation.BeanValidationConstants.POSITIVE;
import static org.eclipse.lsp4jakarta.jdt.core.beanvalidation.BeanValidationConstants.POSTIVE_OR_ZERO;
import static org.eclipse.lsp4jakarta.jdt.core.beanvalidation.BeanValidationConstants.SET_OF_ANNOTATIONS;
import static org.eclipse.lsp4jakarta.jdt.core.beanvalidation.BeanValidationConstants.SET_OF_DATE_TYPES;
import static org.eclipse.lsp4jakarta.jdt.core.beanvalidation.BeanValidationConstants.SHORT;
import static org.eclipse.lsp4jakarta.jdt.core.beanvalidation.BeanValidationConstants.STRING;

import java.util.List;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4jakarta.jdt.core.AbstractDiagnosticsCollector;
import org.eclipse.lsp4jakarta.jdt.core.JakartaCorePlugin;
import org.eclipse.lsp4jakarta.jdt.core.Messages;

public class BeanValidationDiagnosticsCollector extends AbstractDiagnosticsCollector {

    public BeanValidationDiagnosticsCollector() {
        super();
    }

    @Override
    protected String getDiagnosticSource() {
        return DIAGNOSTIC_SOURCE;
    }

    public void collectDiagnostics(ICompilationUnit unit, List<Diagnostic> diagnostics) {

        if (unit != null) {
            IType[] alltypes;
            IField[] allFields;
            IAnnotation[] annotations;
            IMethod[] allMethods;

            try {
                alltypes = unit.getAllTypes();
                for (IType type : alltypes) {
                    allFields = type.getFields();
                    for (IField field : allFields) {
                        annotations = field.getAnnotations();
                        for (IAnnotation annotation : annotations) {
                            String matchedAnnotation = getMatchedJavaElementName(type, annotation.getElementName(),
                                    SET_OF_ANNOTATIONS.toArray(new String[0]));
                            if (matchedAnnotation != null) {
                                validAnnotation(field, annotation, matchedAnnotation, diagnostics);
                            }
                        }
                    }
                    allMethods = type.getMethods();
                    for (IMethod method : allMethods) {
                        annotations = method.getAnnotations();
                        for (IAnnotation annotation : annotations) {
                            String matchedAnnotation = getMatchedJavaElementName(type, annotation.getElementName(),
                                    SET_OF_ANNOTATIONS.toArray(new String[0]));
                            if (matchedAnnotation != null) {
                                validAnnotation(method, annotation, matchedAnnotation, diagnostics);
                            }
                        }
                    }
                }
            } catch (JavaModelException e) {
                JakartaCorePlugin.logException("Cannot calculate diagnostics", e);
            }
        }

    }

    private void validAnnotation(IMember element, IAnnotation annotation, String matchedAnnotation,
            List<Diagnostic> diagnostics) throws JavaModelException {
        IType declaringType = element.getDeclaringType();
        if (declaringType != null) {
            String annotationName = annotation.getElementName();
            boolean isMethod = element instanceof IMethod;

            if (Flags.isStatic(element.getFlags())) {
                String source = isMethod ?
                		Messages.getMessage("ConstraintAnnotationsMethod") :
                		Messages.getMessage("ConstraintAnnotationsField");
                diagnostics.add(createDiagnostic(element, declaringType.getCompilationUnit(),
                        source, DIAGNOSTIC_CODE_STATIC,
                        annotationName, DiagnosticSeverity.Error));
            } else {
                String type = (isMethod) ? ((IMethod) element).getReturnType() : ((IField) element).getTypeSignature();

                if (matchedAnnotation.equals(ASSERT_FALSE) || matchedAnnotation.equals(ASSERT_TRUE)) {
                    String source = isMethod ? 
    		        		Messages.getMessage("AnnotationBooleanMethods", "@" + annotationName) :
    		        		Messages.getMessage("AnnotationBooleanFields", "@" + annotationName);
                    if (!type.equals(getSignatureFormatOfType(BOOLEAN)) && !type.equals(Signature.SIG_BOOLEAN)) {
                        diagnostics.add(createDiagnostic(element, declaringType.getCompilationUnit(),
                                source, DIAGNOSTIC_CODE_INVALID_TYPE, annotationName, DiagnosticSeverity.Error));
                    }
                } else if (matchedAnnotation.equals(DECIMAL_MAX) || matchedAnnotation.equals(DECIMAL_MIN)
                        || matchedAnnotation.equals(DIGITS)) {
                    if (!type.equals(getSignatureFormatOfType(BIG_DECIMAL))
                            && !type.equals(getSignatureFormatOfType(BIG_INTEGER))
                            && !type.equals(getSignatureFormatOfType(CHAR_SEQUENCE))
                            && !type.equals(getSignatureFormatOfType(BYTE))
                            && !type.equals(getSignatureFormatOfType(SHORT))
                            && !type.equals(getSignatureFormatOfType(INTEGER))
                            && !type.equals(getSignatureFormatOfType(LONG)) && !type.equals(Signature.SIG_BYTE)
                            && !type.equals(Signature.SIG_SHORT) && !type.equals(Signature.SIG_INT)
                            && !type.equals(Signature.SIG_LONG)) {
                        String source = isMethod ? 
        		        		Messages.getMessage("AnnotationBigDecimalMethods", "@" + annotationName) :
        		        		Messages.getMessage("AnnotationBigDecimalFields", "@" + annotationName);
                        diagnostics.add(createDiagnostic(element, declaringType.getCompilationUnit(), source,
                                DIAGNOSTIC_CODE_INVALID_TYPE, annotationName, DiagnosticSeverity.Error));
                    }
                } else if (matchedAnnotation.equals(EMAIL)) {
                    if (!type.equals(getSignatureFormatOfType(STRING))
                            && !type.equals(getSignatureFormatOfType(CHAR_SEQUENCE))) {
                        String source = isMethod ? 
        		        		Messages.getMessage("AnnotationStringMethods", "@" + annotationName) :
        		        		Messages.getMessage("AnnotationStringFields", "@" + annotationName);
                        diagnostics.add(createDiagnostic(element, declaringType.getCompilationUnit(),
                                source, DIAGNOSTIC_CODE_INVALID_TYPE, annotationName, DiagnosticSeverity.Error));
                    }
                } else if (matchedAnnotation.equals(FUTURE) || matchedAnnotation.equals(FUTURE_OR_PRESENT)
                        || matchedAnnotation.equals(PAST) || matchedAnnotation.equals(PAST_OR_PRESENT)) {
                    String dataType = getDataTypeName(type);
                    String dataTypeFQName = getMatchedJavaElementName(declaringType, dataType,
                            SET_OF_DATE_TYPES.toArray(new String[0]));
                    if (dataTypeFQName == null) {
                        String source = isMethod ? 
        		        		Messages.getMessage("AnnotationDateMethods", "@" + annotationName) :
        		        		Messages.getMessage("AnnotationDateFields", "@" + annotationName);
                        diagnostics.add(createDiagnostic(element, declaringType.getCompilationUnit(),
                                source, DIAGNOSTIC_CODE_INVALID_TYPE, annotationName, DiagnosticSeverity.Error));
                    }
                } else if (matchedAnnotation.equals(MIN) || matchedAnnotation.equals(MAX)) {
                    if (!type.equals(getSignatureFormatOfType(BIG_DECIMAL))
                            && !type.equals(getSignatureFormatOfType(BIG_INTEGER))
                            && !type.equals(getSignatureFormatOfType(BYTE))
                            && !type.equals(getSignatureFormatOfType(SHORT))
                            && !type.equals(getSignatureFormatOfType(INTEGER))
                            && !type.equals(getSignatureFormatOfType(LONG)) && !type.equals(Signature.SIG_BYTE)
                            && !type.equals(Signature.SIG_SHORT) && !type.equals(Signature.SIG_INT)
                            && !type.equals(Signature.SIG_LONG)) {
                        String source = isMethod ? 
        		        		Messages.getMessage("AnnotationMinMaxMethods", "@" + annotationName) :
        		        		Messages.getMessage("AnnotationMinMaxFields", "@" + annotationName);
                        diagnostics.add(createDiagnostic(element, declaringType.getCompilationUnit(),
                        		source, DIAGNOSTIC_CODE_INVALID_TYPE, annotationName, DiagnosticSeverity.Error));
                    }
                } else if (matchedAnnotation.equals(NEGATIVE) || matchedAnnotation.equals(NEGATIVE_OR_ZERO)
                        || matchedAnnotation.equals(POSITIVE) || matchedAnnotation.equals(POSTIVE_OR_ZERO)) {
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
                        String source = isMethod ? 
        		        		Messages.getMessage("AnnotationPositiveMethods", "@" + annotationName) :
        		        		Messages.getMessage("AnnotationPositiveFields", "@" + annotationName);
                        diagnostics.add(createDiagnostic(element, declaringType.getCompilationUnit(),
                                source, DIAGNOSTIC_CODE_INVALID_TYPE, annotationName, DiagnosticSeverity.Error));
                    }
                } else if (matchedAnnotation.equals(NOT_BLANK)) {
                    if (!type.equals(getSignatureFormatOfType(STRING))
                            && !type.equals(getSignatureFormatOfType(CHAR_SEQUENCE))) {
                        String source = isMethod ? 
        		        		Messages.getMessage("AnnotationStringMethods", "@" + annotationName) :
        		        		Messages.getMessage("AnnotationStringFields", "@" + annotationName);
                        diagnostics.add(createDiagnostic(element, declaringType.getCompilationUnit(),
                                source, DIAGNOSTIC_CODE_INVALID_TYPE, annotationName, DiagnosticSeverity.Error));
                    }
                } else if (matchedAnnotation.equals(PATTERN)) {
                    if (!type.equals(getSignatureFormatOfType(STRING))
                            && !type.equals(getSignatureFormatOfType(CHAR_SEQUENCE))) {
                        String source = isMethod ? 
        		        		Messages.getMessage("AnnotationStringMethods", "@" + annotationName) :
        		        		Messages.getMessage("AnnotationStringFields", "@" + annotationName);
                        diagnostics.add(createDiagnostic(element, declaringType.getCompilationUnit(),
                                source, DIAGNOSTIC_CODE_INVALID_TYPE, annotationName, DiagnosticSeverity.Error));
                    }
                }

                // These ones contains check on all collection types which requires resolving
                // the String of the type somehow
                // This will also require us to check if the field type was a custom collection
                // subtype which means we
                // have to resolve it and get the super interfaces and check to see if
                // Collection, Map or Array was implemented
                // for that custom type (which could as well be a user made subtype)

//    			else if (annotation.getElementName().equals(NOT_EMPTY) || annotation.getElementName().equals(SIZE)) {
//    				
//    				System.out.println("--Field name: " + Signature.getTypeSignatureKind(fieldType));
//    				System.out.println("--Field name: " + Signature.getParameterTypes(fieldType));			
//    				if (	!fieldType.equals(getSignatureFormatOfType(CHAR_SEQUENCE)) &&
//    						!fieldType.contains("List") &&
//    						!fieldType.contains("Set") &&
//    						!fieldType.contains("Collection") &&
//    						!fieldType.contains("Array") &&
//    						!fieldType.contains("Vector") &&
//    						!fieldType.contains("Stack") &&
//    						!fieldType.contains("Queue") &&
//    						!fieldType.contains("Deque") &&
//    						!fieldType.contains("Map")) {
//    					
//    					diagnostics.add(new Diagnostic(fieldAnnotationrange,
//    							"This annotation can only be used on CharSequence, Collection, Array, "
//    							+ "Map type fields."));	
//    				}
//    			}
            }
        }
    }

    /*
     * Refer to Class signature documentation for the formating
     * https://www.ibm.com/support/knowledgecenter/sl/SS5JSH_9.5.0/org.eclipse.jdt.
     * doc.isv/reference/api/org/eclipse/jdt/core/Signature.html
     */
    private static String getSignatureFormatOfType(String type) {
        return "Q" + type + ";";
    }

    private static String getDataTypeName(String type) {
        int length = type.length();
        if (length > 0 && type.charAt(0) == 'Q' && type.charAt(length - 1) == ';') {
            return type.substring(1, length - 1);
        }
        return type;
    }
}