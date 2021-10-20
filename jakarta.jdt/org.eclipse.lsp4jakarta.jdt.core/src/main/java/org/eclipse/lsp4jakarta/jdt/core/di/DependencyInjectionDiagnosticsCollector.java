/*******************************************************************************
* Copyright (c) 2021 IBM Corporation and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
* which is available at https://www.apache.org/licenses/LICENSE-2.0.
*
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
*
* Contributors:
*     IBM Corporation, Himanshu Chotwani - initial API and implementation
*******************************************************************************/

package org.eclipse.lsp4jakarta.jdt.core.di;

import static org.eclipse.lsp4jakarta.jdt.core.di.DependencyInjectionConstants.DIAGNOSTIC_SOURCE;
import static org.eclipse.lsp4jakarta.jdt.core.di.DependencyInjectionConstants.SEVERITY;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4jakarta.jdt.core.DiagnosticsCollector;
import org.eclipse.lsp4jakarta.jdt.core.JDTUtils;
import org.eclipse.lsp4jakarta.jdt.core.JakartaCorePlugin;

/**
 * 
 * jararta.annotation Diagnostics
 * 
 * <li>Diagnostic 1: @Inject fields cannot be final.</li>
 * 
 * @see https://jakarta.ee/specifications/dependency-injection/2.0/jakarta-injection-spec-2.0.html
 *
 */
public class DependencyInjectionDiagnosticsCollector implements DiagnosticsCollector{

    @Override
    public void completeDiagnostic(Diagnostic diagnostic) {
        diagnostic.setSource(DIAGNOSTIC_SOURCE);
        diagnostic.setSeverity(SEVERITY);
	}

	@Override
	public void collectDiagnostics(ICompilationUnit unit, List<Diagnostic> diagnostics) {
		if (unit == null)
            return;
		Diagnostic diagnostic;
		IType[] alltypes;
        IAnnotation[] allAnnotations;
		try {
			alltypes = unit.getAllTypes();
			for (IType type : alltypes) {
				allAnnotations = type.getAnnotations();
				
				IField[] allFields = type.getFields();
				for (IField field : allFields) {
					int fieldFlags = field.getFlags();
					List<IAnnotation> fieldAnnotations = Arrays.asList(field.getAnnotations());
					
					boolean isInjectField = fieldAnnotations.stream()
                            .anyMatch(annotation -> annotation.getElementName()
                            		.equals(DependencyInjectionConstants.INJECT));
					
					boolean isFinal = Flags.isFinal(fieldFlags);
					
					if (isFinal && isInjectField) {
						ISourceRange nameRange = JDTUtils.getNameRange(field);
						Range range = JDTUtils.toRange(unit, nameRange.getOffset(), nameRange.getLength());
						String msg = "Injectable fields cannot be final";
						diagnostic = new Diagnostic(range, msg);
						diagnostic.setCode(DependencyInjectionConstants.DIAGNOSTIC_CODE_INJECT_FINAL);
						diagnostic.setData(field.getElementType());
						completeDiagnostic(diagnostic);
						diagnostics.add(diagnostic);
					}
				}
			}
		} catch (JavaModelException e) {
        	JakartaCorePlugin.logException("Cannot calculate diagnostics", e);
        }
	}

}
