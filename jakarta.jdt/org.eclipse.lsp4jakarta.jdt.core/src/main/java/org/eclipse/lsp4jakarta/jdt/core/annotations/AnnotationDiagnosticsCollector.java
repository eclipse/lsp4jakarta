/*******************************************************************************
* Copyright (c) 2021 IBM Corporation and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0.
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     IBM Corporation - initial API and implementation
*******************************************************************************/

package org.eclipse.lsp4jakarta.jdt.core.annotations;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IAnnotatable;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Tuple;
import org.eclipse.lsp4j.jsonrpc.messages.Tuple.Two;
import org.eclipse.lsp4jakarta.jdt.core.DiagnosticsCollector;
import org.eclipse.lsp4jakarta.jdt.core.JDTUtils;
import org.eclipse.lsp4jakarta.jdt.core.JakartaCorePlugin;

public class AnnotationDiagnosticsCollector implements DiagnosticsCollector {
	
    public AnnotationDiagnosticsCollector() {
    }

    public void completeDiagnostic(Diagnostic diagnostic) {
        diagnostic.setSource(AnnotationConstants.DIAGNOSTIC_SOURCE);
        diagnostic.setSeverity(AnnotationConstants.SEVERITY);
    }
    
    public void collectDiagnostics(ICompilationUnit unit, List<Diagnostic> diagnostics) {
        if (unit != null) {
        	try {
    			ArrayList<Tuple.Two<IAnnotation, IAnnotatable>> annotatables = new ArrayList<Two<IAnnotation, IAnnotatable>>();
   		
    			for (IPackageDeclaration p : unit.getPackageDeclarations()) {
    				for (IAnnotation annotation : p.getAnnotations()) {
    					annotatables.add(new Tuple.Two<>(annotation, p));
    				}    			
    			}
    			
    			for (IType type : unit.getAllTypes()) {
    				
    				for (IAnnotation annotation : type.getAnnotations()) {
    					annotatables.add(new Tuple.Two<>(annotation, type));
    				}
    				
    				for (IMethod method : type.getMethods()) {
    					for (IAnnotation annotation : method.getAnnotations()) {
    						annotatables.add(new Tuple.Two<>(annotation, method));
        				}
    				}
    				
    				for (IField field : type.getFields()) {
    					for (IAnnotation annotation : field.getAnnotations()) {
    						annotatables.add(new Tuple.Two<>(annotation, field));
        				}
    				}
    			}
    			
    			for (Tuple.Two<IAnnotation, IAnnotatable> annotatable : annotatables) {
    				IAnnotation annotation = annotatable.getFirst();
    				IAnnotatable element = annotatable.getSecond();
    				
    				if (annotation.getElementName().equals(AnnotationConstants.GENERATED)) {
    					for (IMemberValuePair pair : annotation.getMemberValuePairs()) {
							// If date element exists and is non-empty, it must follow ISO 8601 format.
    						if (pair.getMemberName().equals("date")) {
    							if (pair.getValue() instanceof String) {
    								String date = (String) pair.getValue();
    								if (!date.equals("")) {
    									if (!Pattern.matches(AnnotationConstants.ISO_8601_REGEX, date)) {
    										ISourceRange annotationNameRange = JDTUtils.getNameRange(annotation);
    										Range annotationRange = JDTUtils.toRange(
    												unit,
    												annotationNameRange.getOffset(),
    												annotationNameRange.getLength());
    										Diagnostic diagnostic = new Diagnostic(
    												annotationRange,
    												"The 'date' attribute of the Generated annotation MUST follow ISO 8601.");
    										diagnostic.setCode(AnnotationConstants.DIAGNOSTIC_CODE_DATE_FORMAT);
    										diagnostics.add(diagnostic);
    									}
    								}	
    							}
    						}
    					}
    				} else if (annotation.getElementName().equals(AnnotationConstants.RESOURCE)) {
    					if (element instanceof IType) {
    						IType type = (IType) element;
    						if (type.getElementType() == IJavaElement.TYPE && ((IType)type).isClass()) {
        						Boolean nameEmpty = true;
        						Boolean typeEmpty = true;
        						for (IMemberValuePair pair : annotation.getMemberValuePairs()) {
        							if (pair.getMemberName().equals("name")) {
            							nameEmpty = false;
        							}
        							if (pair.getMemberName().equals("type")) {
            							typeEmpty = false;
        							}
        						}
        						ISourceRange annotationNameRange = JDTUtils.getNameRange(annotation);
    							Range annotationRange = JDTUtils.toRange(
    									unit,
    									annotationNameRange.getOffset(),
    									annotationNameRange.getLength());
        						
        						if (nameEmpty) {
        							Diagnostic diagnostic = new Diagnostic(
    										annotationRange,
    										"Missing 'name' attribute on @Resource.");
    								diagnostic.setCode(AnnotationConstants.DIAGNOSTIC_CODE_MISSING_RESOURCE_NAME_ATTRIBUTE);
    								diagnostics.add(diagnostic);
        						}
        						
        						if (typeEmpty) {
        							Diagnostic diagnostic = new Diagnostic(
    										annotationRange,
    										"Missing 'type' attribute on @Resource.");
    								diagnostic.setCode(AnnotationConstants.DIAGNOSTIC_CODE_MISSING_RESOURCE_TYPE_ATTRIBUTE);
    								diagnostics.add(diagnostic);
        						}
        					}
    					}
    				} else if (annotation.getElementName().equals(AnnotationConstants.POST_CONSTRUCT)) {
    					if (element instanceof IMethod) {
    						IMethod method = (IMethod) element;
    						
    						ISourceRange methodNameRange = JDTUtils.getNameRange(method);
							Range methodRange = JDTUtils.toRange(
									unit,
									methodNameRange.getOffset(),
									methodNameRange.getLength());
							
    						if (method.getNumberOfParameters() != 0) {
    							Diagnostic diagnostic = new Diagnostic(
										methodRange,
										"@PostConstruct method should not have any parameters.");
								diagnostic.setCode(AnnotationConstants.DIAGNOSTIC_CODE_POSTCONSTRUCT_PARAMS);
								diagnostics.add(diagnostic);
    						}
    						
    						if (!method.getReturnType().equals("V")) {
    							Diagnostic diagnostic = new Diagnostic(
										methodRange,
										"@PostConstruct method must be void.");
								diagnostic.setCode(AnnotationConstants.DIAGNOSTIC_CODE_POSTCONSTRUCT_RETURN_TYPE);
								diagnostics.add(diagnostic);
    						}
    						
    						if (method.getExceptionTypes().length != 0) {
    							Diagnostic diagnostic = new Diagnostic(
										methodRange,
										"@PostConstruct method should not throw checked exceptions.");
								diagnostic.setCode(AnnotationConstants.DIAGNOSTIC_CODE_POSTCONSTRUCT_EXCEPTION);
								diagnostics.add(diagnostic);
    						}
    					}
    				} else if (annotation.getElementName().equals(AnnotationConstants.PRE_DESTROY)) {
    					if (element instanceof IMethod) {
    						IMethod method = (IMethod) element;
    						
    						ISourceRange methodNameRange = JDTUtils.getNameRange(method);
							Range methodRange = JDTUtils.toRange(
									unit,
									methodNameRange.getOffset(),
									methodNameRange.getLength());
							
    						if (method.getNumberOfParameters() != 0) {
    							Diagnostic diagnostic = new Diagnostic(
										methodRange,
										"@PreDestroy method should not have any parameters.");
								diagnostic.setCode(AnnotationConstants.DIAGNOSTIC_CODE_PREDESTROY_PARAMS);
								diagnostics.add(diagnostic);
    						}
    						
    						if (Flags.isStatic(method.getFlags())) {
    							Diagnostic diagnostic = new Diagnostic(
										methodRange,
										"@PostConstruct method must not be static.");
								diagnostic.setCode(AnnotationConstants.DIAGNOSTIC_CODE_PREDESTROY_STATIC);
								diagnostics.add(diagnostic);
    						}
    						
    						if (method.getExceptionTypes().length != 0) {
    							Diagnostic diagnostic = new Diagnostic(
										methodRange,
										"@PostConstruct method should not throw checked exceptions.");
								diagnostic.setCode(AnnotationConstants.DIAGNOSTIC_CODE_PREDESTROY_EXCEPTION);
								diagnostics.add(diagnostic);
    						}
    					}
    				}
    			}
	        } catch (JavaModelException e) {
	        	JakartaCorePlugin.logException("Cannot calculate diagnostics", e);
	        }
        }
    }
}
