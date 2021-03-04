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
*     Hani Damlaj, Jianing Xu
*******************************************************************************/

package org.jakarta.jdt.cdi;

import java.util.List;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Range;
import org.jakarta.jdt.DiagnosticsCollector;
import org.jakarta.jdt.JDTUtils;
import org.jakarta.jdt.persistence.PersistenceConstants;
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
    
    private Diagnostic createDiagnostic(IJavaElement el, ICompilationUnit unit, String msg, String code) {
        try {
            ISourceRange nameRange = JDTUtils.getNameRange(el);
            Range range = JDTUtils.toRange(unit, nameRange.getOffset(), nameRange.getLength());
            Diagnostic diagnostic = new Diagnostic(range, msg);
            diagnostic.setCode(code);
            completeDiagnostic(diagnostic);
            return diagnostic;
        } catch (JavaModelException e) {
            Activator.logException("Cannot calculate diagnostics", e);
        }
        return null;
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
                IAnnotation[] allAnnotations = type.getAnnotations();
                boolean isManagedBean = managedBeanAnnotations.size() > 0;
                
                ISourceRange nameRange = JDTUtils.getNameRange(type);
                Range range = JDTUtils.toRange(unit, nameRange.getOffset(), nameRange.getLength());

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
                
                /* ========= Produces and Inject Annotations Checks ========= */
                // go through each field and method to make sure @Produces and @Inject are not used together
                for (IMethod method : type.getMethods()) {
                    IAnnotation ProducesAnnotation = null;
                    IAnnotation InjectClassAnnotation = null;
                    for (IAnnotation annotation : method.getAnnotations()) {
                        if (annotation.getElementName().equals(ManagedBeanConstants.PRODUCES))
                            ProducesAnnotation = annotation;
                        if (annotation.getElementName().equals(ManagedBeanConstants.INJECT))
                            InjectClassAnnotation = annotation;
                    }
                    if (ProducesAnnotation != null && InjectClassAnnotation != null) {
                        // A single method cannot have the same
                        diagnostics.add(createDiagnostic(method, unit,
                                "@Produces and @Inject annotations cannot be used on the same field or property",
                                ManagedBeanConstants.DIAGNOSTIC_CODE_PRODUCES_INJECT));
                    }
                }
                
                for (IField field : type.getFields()) {
                    IAnnotation ProducesAnnotation = null;
                    IAnnotation InjectClassAnnotation = null;
                    for (IAnnotation annotation : field.getAnnotations()) {
                        if (annotation.getElementName().equals(ManagedBeanConstants.PRODUCES))
                            ProducesAnnotation = annotation;
                        if (annotation.getElementName().equals(ManagedBeanConstants.INJECT))
                            InjectClassAnnotation = annotation;
                    }
                    if (ProducesAnnotation != null && InjectClassAnnotation != null) {
                        // A single field cannot have the same
                        diagnostics.add(createDiagnostic(field, unit,
                                "@Produces and @Inject annotations cannot be used on the same field or property",
                                ManagedBeanConstants.DIAGNOSTIC_CODE_PRODUCES_INJECT));
                    }
                }
                
            }
        } catch (JavaModelException e) {
            Activator.logException("Cannot calculate diagnostics", e);
        }
    }
}
