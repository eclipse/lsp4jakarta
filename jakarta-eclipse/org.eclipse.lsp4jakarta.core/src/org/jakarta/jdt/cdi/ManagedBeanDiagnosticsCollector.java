/*******************************************************************************
* Copyright (c) 2021 IBM Corporation.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0.
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     Hani Damlaj
*******************************************************************************/

package org.jakarta.jdt.cdi;

import java.util.List;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Range;
import org.jakarta.jdt.DiagnosticsCollector;
import org.jakarta.jdt.JDTUtils;
import org.jakarta.lsp4e.Activator;

import static org.jakarta.jdt.cdi.ManagedBeanConstants.*;
import static org.jakarta.jdt.cdi.Utils.getManagedBeanAnnotations;

public class ManagedBeanDiagnosticsCollector implements DiagnosticsCollector {

    private Diagnostic createDiagnostic(ICompilationUnit unit, IJavaElement el, String message)
            throws JavaModelException {
        ISourceRange nameRange = JDTUtils.getNameRange(el);
        Range range = JDTUtils.toRange(unit, nameRange.getOffset(), nameRange.getLength());
        Diagnostic diagnostic = new Diagnostic(range, message);
        completeDiagnostic(diagnostic);
        return diagnostic;
    }

    @Override
    public void completeDiagnostic(Diagnostic diagnostic) {
        diagnostic.setSource(DIAGNOSTIC_SOURCE);
        diagnostic.setSeverity(SEVERITY);
    }

    @Override
    public void collectDiagnostics(ICompilationUnit unit, List<Diagnostic> diagnostics) {
        if (unit == null)
            return;

        try {
            for (IType type : unit.getAllTypes()) {
                List<String> managedBeanAnnotations = getManagedBeanAnnotations(type);
                boolean isManagedBean = managedBeanAnnotations.size() > 0;

                for (IField field : type.getFields()) {
                    int fieldFlags = field.getFlags();

                    /**
                     * If a managed bean has a non-static public field, it must have
                     * scope @Dependent. If a managed bean with a non-static public field declares
                     * any scope other than @Dependent, the container automatically detects the
                     * problem and treats it as a definition error.
                     * 
                     * https://jakarta.ee/specifications/cdi/2.0/cdi-spec-2.0.html#managed_beans
                     */
                    if (isManagedBean && Flags.isPublic(fieldFlags) && !Flags.isStatic(fieldFlags)
                            && managedBeanAnnotations.stream()
                                    .anyMatch(annotation -> !annotation.equals("Dependent"))) {
                        Diagnostic diagnostic = createDiagnostic(unit, field,
                                "A managed bean with a non-static public field must not declare any scope other than @Dependent");
                        diagnostic.setCode(DIAGNOSTIC_CODE);
                        diagnostics.add(diagnostic);
                    }
                }
            }
        } catch (JavaModelException e) {
            Activator.logException("Cannot calculate diagnostics", e);
        }
    }
}
