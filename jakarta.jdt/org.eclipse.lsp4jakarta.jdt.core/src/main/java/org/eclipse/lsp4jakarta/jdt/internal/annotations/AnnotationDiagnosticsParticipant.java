/*******************************************************************************
* Copyright (c) 2021, 2023 IBM Corporation and others.
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

package org.eclipse.lsp4jakarta.jdt.internal.annotations;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IAnnotatable;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IType;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Tuple;
import org.eclipse.lsp4j.jsonrpc.messages.Tuple.Two;
import org.eclipse.lsp4jakarta.jdt.core.java.diagnostics.IJavaDiagnosticsParticipant;
import org.eclipse.lsp4jakarta.jdt.core.java.diagnostics.JavaDiagnosticsContext;
import org.eclipse.lsp4jakarta.jdt.core.utils.IJDTUtils;
import org.eclipse.lsp4jakarta.jdt.core.utils.PositionUtils;
import org.eclipse.lsp4jakarta.jdt.internal.DiagnosticUtils;
import org.eclipse.lsp4jakarta.jdt.internal.Messages;
import org.eclipse.lsp4jakarta.jdt.internal.core.ls.JDTUtilsLSImpl;

/**
 * 
 * jararta.annotation Diagnostics
 * 
 * <li>Diagnostic 1: @Generated 'date' attribute does not follow ISO 8601.</li>
 * <li>Diagnostic 2: @Resource 'name' attribute missing (when annotation is used
 * on a class).</li>
 * <li>Diagnostic 3: @Resource 'type' attribute missing (when annotation is used
 * on a class).</li>
 * <li>Diagnostic 4: @PostConstruct method has parameters.</li>
 * <li>Diagnostic 5: @PostConstruct method is not void.</li>
 * <li>Diagnostic 6: @PostConstruct method throws checked exception(s).</li>
 * <li>Diagnostic 7: @PreDestroy method has parameters.</li>
 * <li>Diagnostic 8: @PreDestroy method is static.</li>
 * <li>Diagnostic 9: @PreDestroy method throws checked exception(s).</li>
 * 
 * @see https://jakarta.ee/specifications/annotations/2.0/annotations-spec-2.0.html#annotations
 *
 */
public class AnnotationDiagnosticsParticipant implements IJavaDiagnosticsParticipant {

	@Override
	public List<Diagnostic> collectDiagnostics(JavaDiagnosticsContext context, IProgressMonitor monitor)
			throws CoreException {
		String uri = context.getUri();
		IJDTUtils utils = JDTUtilsLSImpl.getInstance();
		ICompilationUnit unit = utils.resolveCompilationUnit(uri);
		List<Diagnostic> diagnostics = new ArrayList<>();

		if (unit != null) {
			ArrayList<Tuple.Two<IAnnotation, IAnnotatable>> annotatables = new ArrayList<Two<IAnnotation, IAnnotatable>>();
			String[] validAnnotations = { Constants.GENERATED_FQ_NAME };
			String[] validTypeAnnotations = { Constants.GENERATED_FQ_NAME,
					Constants.RESOURCE_FQ_NAME };
			String[] validMethodAnnotations = { Constants.GENERATED_FQ_NAME,
					Constants.POST_CONSTRUCT_FQ_NAME, Constants.PRE_DESTROY_FQ_NAME,
					Constants.RESOURCE_FQ_NAME };

			IPackageDeclaration[] packages = unit.getPackageDeclarations();
			for (IPackageDeclaration p : packages) {
				IAnnotation[] annotations = p.getAnnotations();
				for (IAnnotation annotation : annotations) {
					if (isValidAnnotation(annotation.getElementName(), validAnnotations))
						annotatables.add(new Tuple.Two<>(annotation, p));
				}
			}

			IType[] types = unit.getAllTypes();
			for (IType type : types) {
				// Type
				IAnnotation[] annotations = type.getAnnotations();
				for (IAnnotation annotation : annotations) {
					if (isValidAnnotation(annotation.getElementName(), validTypeAnnotations))
						annotatables.add(new Tuple.Two<>(annotation, type));
				}
				// Method
				IMethod[] methods = type.getMethods();
				for (IMethod method : methods) {
					annotations = method.getAnnotations();
					for (IAnnotation annotation : annotations) {
						if (isValidAnnotation(annotation.getElementName(), validMethodAnnotations))
							annotatables.add(new Tuple.Two<>(annotation, method));
					}
					// method parameters
					ILocalVariable[] parameters = method.getParameters();
					for (ILocalVariable parameter : parameters) {
						annotations = parameter.getAnnotations();
						for (IAnnotation annotation : annotations) {
							if (isValidAnnotation(annotation.getElementName(), validAnnotations))
								annotatables.add(new Tuple.Two<>(annotation, parameter));
						}
					}
				}
				// Field
				IField[] fields = type.getFields();
				for (IField field : fields) {
					annotations = field.getAnnotations();
					for (IAnnotation annotation : annotations) {
						if (isValidAnnotation(annotation.getElementName(), validTypeAnnotations))
							annotatables.add(new Tuple.Two<>(annotation, field));
					}
				}
			}

			for (Tuple.Two<IAnnotation, IAnnotatable> annotatable : annotatables) {
				IAnnotation annotation = annotatable.getFirst();
				IAnnotatable element = annotatable.getSecond();

				if (DiagnosticUtils.isMatchedAnnotation(unit, annotation, Constants.GENERATED_FQ_NAME)) {
					for (IMemberValuePair pair : annotation.getMemberValuePairs()) {
						// If date element exists and is non-empty, it must follow ISO 8601 format.
						if (pair.getMemberName().equals("date")) {
							if (pair.getValue() instanceof String) {
								String date = (String) pair.getValue();
								if (!date.equals("")) {
									if (!Pattern.matches(Constants.ISO_8601_REGEX, date)) {

										Range annotationRange = PositionUtils.toNameRange(annotation,
												context.getUtils());
										String diagnosticMessage = Messages.getMessage(
												"AnnotationMustDefineAttributeFollowing8601", "@Generated", "date");
										diagnostics.add(
												context.createDiagnostic(uri, diagnosticMessage, annotationRange,
														Constants.DIAGNOSTIC_SOURCE,
														ErrorCode.InvalidDateFormat,
														DiagnosticSeverity.Error));
									}
								}
							}
						}
					}
				} else if (DiagnosticUtils.isMatchedAnnotation(unit, annotation, Constants.RESOURCE_FQ_NAME)) {
					if (element instanceof IType) {
						IType type = (IType) element;
						if (type.getElementType() == IJavaElement.TYPE && ((IType) type).isClass()) {
							Range annotationRange = PositionUtils.toNameRange(annotation, context.getUtils());
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
							String diagnosticMessage;
							if (nameEmpty) {
								diagnosticMessage = Messages.getMessage("AnnotationMustDefineAttribute",
										"@Resource", "name");
								diagnostics.add(context.createDiagnostic(uri, diagnosticMessage, annotationRange,
										Constants.DIAGNOSTIC_SOURCE,
										ErrorCode.MissingResourceNameAttribute,
										DiagnosticSeverity.Error));
							}

							if (typeEmpty) {
								diagnosticMessage = Messages.getMessage("AnnotationMustDefineAttribute",
										"@Resource", "type");
								diagnostics.add(context.createDiagnostic(uri, diagnosticMessage, annotationRange,
										Constants.DIAGNOSTIC_SOURCE,
										ErrorCode.MissingResourceTypeAttribute,
										DiagnosticSeverity.Error));
							}
						}
					}
				}
				if (DiagnosticUtils.isMatchedAnnotation(unit, annotation, Constants.POST_CONSTRUCT_FQ_NAME)) {
					if (element instanceof IMethod) {
						IMethod method = (IMethod) element;
						Range methodRange = PositionUtils.toNameRange(method, context.getUtils());

						if (method.getNumberOfParameters() != 0) {

							String diagnosticMessage = Messages.getMessage("MethodMustNotHaveParameters",
									"@PostConstruct");
							diagnostics.add(context.createDiagnostic(uri, diagnosticMessage, methodRange,
									Constants.DIAGNOSTIC_SOURCE,
									ErrorCode.PostConstructParams,
									DiagnosticSeverity.Error));
						}

						if (!method.getReturnType().equals("V")) {
							String diagnosticMessage = Messages.getMessage("MethodMustBeVoid",
									"@PostConstruct");
							diagnostics.add(context.createDiagnostic(uri, diagnosticMessage, methodRange,
									Constants.DIAGNOSTIC_SOURCE,
									ErrorCode.PostConstructReturnType,
									DiagnosticSeverity.Error));
						}

						if (method.getExceptionTypes().length != 0) {
							String diagnosticMessage = Messages.getMessage("MethodMustNotThrow",
									"@PostConstruct");
							diagnostics.add(context.createDiagnostic(uri, diagnosticMessage, methodRange,
									Constants.DIAGNOSTIC_SOURCE,
									ErrorCode.PostConstructException,
									DiagnosticSeverity.Warning));
						}
					}
				} else if (DiagnosticUtils.isMatchedAnnotation(unit, annotation,
						Constants.PRE_DESTROY_FQ_NAME)) {
					if (element instanceof IMethod) {
						IMethod method = (IMethod) element;
						Range methodRange = PositionUtils.toNameRange(method, context.getUtils());

						if (method.getNumberOfParameters() != 0) {
							String diagnosticMessage = Messages.getMessage("MethodMustNotHaveParameters",
									"@PreDestroy");
							diagnostics.add(context.createDiagnostic(uri, diagnosticMessage, methodRange,
									Constants.DIAGNOSTIC_SOURCE,
									ErrorCode.PreDestroyParams,
									DiagnosticSeverity.Error));
						}

						if (Flags.isStatic(method.getFlags())) {
							String diagnosticMessage = Messages.getMessage("MethodMustNotBeStatic",
									"@PreDestroy");
							diagnostics.add(context.createDiagnostic(uri, diagnosticMessage, methodRange,
									Constants.DIAGNOSTIC_SOURCE,
									ErrorCode.PreDestroyStatic,
									DiagnosticSeverity.Error));
						}

						if (method.getExceptionTypes().length != 0) {
							String diagnosticMessage = Messages.getMessage("MethodMustNotThrow",
									"@PreDestroy");
							diagnostics.add(context.createDiagnostic(uri, diagnosticMessage, methodRange,
									Constants.DIAGNOSTIC_SOURCE,
									ErrorCode.PreDestroyException,
									DiagnosticSeverity.Warning));
						}
					}
				}
			}
		}

		return diagnostics;
	}

	private static boolean isValidAnnotation(String annotationName, String[] validAnnotations) {
		for (String fqName : validAnnotations) {
			if (fqName.endsWith(annotationName)) {
				return true;
			}
		}
		return false;
	}

}
