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
 *     IBM Corporation, Matthew Shocrylas - initial API and implementation
 *     Bera Sogut
 *******************************************************************************/

package org.eclipse.lsp4jakarta.jdt.internal.jaxrs;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4jakarta.jdt.core.java.diagnostics.IJavaDiagnosticsParticipant;
import org.eclipse.lsp4jakarta.jdt.core.java.diagnostics.JavaDiagnosticsContext;
import org.eclipse.lsp4jakarta.jdt.core.utils.PositionUtils;
import org.eclipse.lsp4jakarta.jdt.internal.DiagnosticUtils;
import org.eclipse.lsp4jakarta.jdt.internal.Messages;

/**
 * JAX-RS diagnostic participant that manages the use of resource methods.
 */
public class ResourceMethodDiagnosticsParticipant implements IJavaDiagnosticsParticipant {

	@Override
	public List<Diagnostic> collectDiagnostics(JavaDiagnosticsContext context, IProgressMonitor monitor)
			throws CoreException {
		ITypeRoot typeRoot = context.getTypeRoot();
		String uri = context.getUri();
		IJavaElement[] elements = typeRoot.getChildren();
		List<Diagnostic> diagnostics = new ArrayList<>();

		String[] methodDesignators = ArrayUtils.addAll(Constants.SET_OF_METHOD_DESIGNATORS_ANNOTATIONS,
				Constants.PATH_ANNOTATION);

		for (IJavaElement element : elements) {
			if (monitor.isCanceled()) {
				return null;
			}

			if (element.getElementType() == IJavaElement.TYPE) {
				IType type = (IType) element;
				if (!type.isClass()) {
					continue;
				}

				IMethod[] methods = type.getMethods();
				boolean isInterface = type.isInterface();

				for (IMethod method : methods) {
					IAnnotation[] methodAnnotations = method.getAnnotations();
					boolean isResourceMethod = false;
					boolean isValid = true;
					boolean isPublic = Flags.isPublic(method.getFlags());
					boolean usesDfltAccessModifier = Flags.isPackageDefault(method.getFlags());
					Range methodRange = PositionUtils.toNameRange(method, context.getUtils());

					for (IAnnotation annotation : methodAnnotations) {
						String matchedAnnotation = DiagnosticUtils.getMatchedJavaElementName(type,
								annotation.getElementName(), methodDesignators);
						if (matchedAnnotation != null) {
							if (isValid && !isPublic && !(usesDfltAccessModifier && isInterface))
								isValid = false;
							if (!Constants.PATH_ANNOTATION.equals(matchedAnnotation)) {
								isResourceMethod = true;
								break;
							}
						}
					}
					if (!isValid) {
						diagnostics.add(context.createDiagnostic(uri, Messages.getMessage("OnlyPublicMethods"),
								methodRange, Constants.DIAGNOSTIC_SOURCE, ErrorCode.NonPublicResourceMethod,
								DiagnosticSeverity.Error));
					}
					if (isResourceMethod) {
						int numEntityParams = 0;
						ILocalVariable[] parameters = method.getParameters();
						for (ILocalVariable param : parameters) {
							boolean isEntityParam = true;
							IAnnotation[] annotations = param.getAnnotations();
							for (IAnnotation annotation : annotations) {
								String matchedAnnotation = DiagnosticUtils.getMatchedJavaElementName(type,
										annotation.getElementName(), Constants.SET_OF_NON_ENTITY_PARAM_ANNOTATIONS);
								if (matchedAnnotation != null) {
									isEntityParam = false;
									break;
								}
							}
							if (isEntityParam)
								numEntityParams++;
						}
						if (numEntityParams > 1) {
							diagnostics.add(
									context.createDiagnostic(uri, Messages.getMessage("ResourceMethodsEntityParameter"),
											methodRange, Constants.DIAGNOSTIC_SOURCE,
											ErrorCode.ResourceMethodMultipleEntityParams, DiagnosticSeverity.Error));
						}
					}
				}

			}
		}

		return diagnostics;
	}
}
