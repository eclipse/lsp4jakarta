/*******************************************************************************
* Copyright (c) 2021, 2023 IBM Corporation and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     IBM Corporation, Himanshu Chotwani - initial API and implementation
*     Ananya Rao - Diagnostic Collection for multiple constructors annotated with inject
*******************************************************************************/

package org.eclipse.lsp4jakarta.jdt.internal.di;

import static org.eclipse.lsp4jakarta.jdt.internal.di.Constants.INJECT_FQ_NAME;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4jakarta.jdt.core.JakartaCorePlugin;
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
 * <li>Diagnostic 1: @Inject fields cannot be final.</li>
 * <li>Diagnostic 2: @Inject methods cannot be final.</li>
 * <li>Diagnostic 3: @Inject methods cannot be abstract.</li>
 * <li>Diagnostic 4: @Inject methods cannot be static.</li>
 * <li>Diagnostic 5: @Inject methods cannot be generic.</li>
 * 
 * @see https://jakarta.ee/specifications/dependency-injection/2.0/jakarta-injection-spec-2.0.html
 *
 */
public class DependencyInjectionDiagnosticsParticipant implements IJavaDiagnosticsParticipant {

	@Override
	public List<Diagnostic> collectDiagnostics(JavaDiagnosticsContext context, IProgressMonitor monitor)
			throws CoreException {
		String uri = context.getUri();
		IJDTUtils utils = JDTUtilsLSImpl.getInstance();
		ICompilationUnit unit = utils.resolveCompilationUnit(uri);
		List<Diagnostic> diagnostics = new ArrayList<>();

		if (unit == null) {
			return diagnostics;
		}

		IType[] alltypes;

		alltypes = unit.getAllTypes();
		for (IType type : alltypes) {
			IField[] allFields = type.getFields();
			for (IField field : allFields) {
				if (Flags.isFinal(field.getFlags())
						&& containsAnnotation(type, field.getAnnotations(), INJECT_FQ_NAME)) {
					String msg = Messages.getMessage("InjectNoFinalField");
					Range range = PositionUtils.toNameRange(field,
							context.getUtils());
					diagnostics.add(
							context.createDiagnostic(uri, msg, range, Constants.DIAGNOSTIC_SOURCE,
									ErrorCode.InvalidInjectAnnotationOnFinalField,
									DiagnosticSeverity.Error));
				}
			}

			List<IMethod> injectedConstructors = new ArrayList<IMethod>();
			IMethod[] allMethods = type.getMethods();
			for (IMethod method : allMethods) {
				int methodFlag = method.getFlags();
				boolean isFinal = Flags.isFinal(methodFlag);
				boolean isAbstract = Flags.isAbstract(methodFlag);
				boolean isStatic = Flags.isStatic(methodFlag);
				boolean isGeneric = method.getTypeParameters().length != 0;
				Range range = PositionUtils.toNameRange(method,
						context.getUtils());
				if (containsAnnotation(type, method.getAnnotations(), INJECT_FQ_NAME)) {
					if (DiagnosticUtils.isConstructorMethod(method))
						injectedConstructors.add(method);
					if (isFinal) {
						String msg = Messages.getMessage("InjectNoFinalMethod");

						diagnostics.add(context.createDiagnostic(uri, msg, range, Constants.DIAGNOSTIC_SOURCE,
								ErrorCode.InvalidInjectAnnotationOnFinalMethod,
								DiagnosticSeverity.Error));
					}
					if (isAbstract) {
						String msg = Messages.getMessage("InjectNoAbstractMethod");
						diagnostics.add(context.createDiagnostic(uri, msg, range, Constants.DIAGNOSTIC_SOURCE,
								ErrorCode.InvalidInjectAnnotationOnAbstractMethod,
								DiagnosticSeverity.Error));
					}
					if (isStatic) {
						String msg = Messages.getMessage("InjectNoStaticMethod");
						diagnostics.add(context.createDiagnostic(uri, msg, range, Constants.DIAGNOSTIC_SOURCE,
								ErrorCode.InvalidInjectAnnotationOnStaticMethod,
								DiagnosticSeverity.Error));
					}

					if (isGeneric) {
						String msg = Messages.getMessage("InjectNoGenericMethod");
						diagnostics.add(context.createDiagnostic(uri, msg, range, Constants.DIAGNOSTIC_SOURCE,
								ErrorCode.InvalidInjectAnnotationOnGenericMethod,
								DiagnosticSeverity.Error));
					}
				}
			}

			// if more than one 'inject' constructor, add diagnostic to all constructors
			if (injectedConstructors.size() > 1) {
				String msg = Messages.getMessage("InjectMoreThanOneConstructor");
				for (IMethod method : injectedConstructors) {
					Range range = PositionUtils.toNameRange(method,
							context.getUtils());
					diagnostics.add(context.createDiagnostic(uri, msg, range, Constants.DIAGNOSTIC_SOURCE,
							ErrorCode.InvalidInjectAnnotationOnMultipleConstructors,
							DiagnosticSeverity.Error));
				}
			}
		}

		return diagnostics;
	}

	private boolean containsAnnotation(IType type, IAnnotation[] annotations, String annotationFQName) {
		return Stream.of(annotations).anyMatch(annotation -> {
			try {
				return DiagnosticUtils.isMatchedJavaElement(type, annotation.getElementName(), annotationFQName);
			} catch (JavaModelException e) {
				JakartaCorePlugin.logException("Cannot validate annotations", e);
				return false;
			}
		});
	}
}
