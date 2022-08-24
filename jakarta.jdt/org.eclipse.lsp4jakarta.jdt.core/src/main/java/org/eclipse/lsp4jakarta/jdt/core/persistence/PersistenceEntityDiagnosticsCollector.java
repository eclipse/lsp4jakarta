/*******************************************************************************
* Copyright (c) 2020, 2022 IBM Corporation, Ankush Sharma and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0.
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     IBM Corporation, Ankush Sharma - initial API and implementation
*******************************************************************************/

package org.eclipse.lsp4jakarta.jdt.core.persistence;

import java.util.List;

import org.eclipse.jdt.core.Flags;

/**
 * @author ankushsharma
 * @brief Diagnostics implementation for Jakarta Persistence 3.0
 */

// Imports
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4jakarta.jdt.core.AbstractDiagnosticsCollector;
import org.eclipse.lsp4jakarta.jdt.core.JakartaCorePlugin;

public class PersistenceEntityDiagnosticsCollector extends AbstractDiagnosticsCollector {

    public PersistenceEntityDiagnosticsCollector() {
        super();
    }

    @Override
    protected String getDiagnosticSource() {
        return PersistenceConstants.DIAGNOSTIC_SOURCE;
    }

    /**
     * check if the modifier provided is static
     * 
     * @param flag
     * @return
     * @note modifier flags are an addition of all flags combined
     */
    private boolean isStatic(int flag) {
        // If a field is static, we do not care about it, we care about all other field
        Integer isPublicStatic = flag - Flags.AccPublic;
        Integer isPrivateStatic = flag - Flags.AccPrivate;
        Integer isFinalStatic = flag - Flags.AccFinal;
        Integer isProtectedStatic = flag - Flags.AccProtected;
        Integer isStatic = flag;
        if (isPublicStatic.equals(Flags.AccStatic) || isPrivateStatic.equals(Flags.AccStatic)
                || isStatic.equals(Flags.AccStatic) || isFinalStatic.equals(Flags.AccStatic)
                || isProtectedStatic.equals(Flags.AccStatic)) {
            return true;
        }
        return false;
    }

    /**
     * check if the modifier provided is final
     * 
     * @param flag
     * @return
     * @note modifier flags are an addition of all flags combined
     */
    private boolean isFinal(int flag) {
        Integer isPublicFinal = flag - Flags.AccPublic;
        Integer isPrivateFinal = flag - Flags.AccPrivate;
        Integer isProtectedFinal = flag - Flags.AccProtected;
        Integer isFinal = flag;
        if (isPublicFinal.equals(Flags.AccFinal) || isPrivateFinal.equals(Flags.AccFinal)
                || isProtectedFinal.equals(Flags.AccFinal) || isFinal.equals(Flags.AccFinal)) {
            return true;
        }
        return false;
    }

    @Override
    public void collectDiagnostics(ICompilationUnit unit, List<Diagnostic> diagnostics) {
        if (unit != null) {
            IType[] alltypes;
            IAnnotation[] allAnnotations;

            try {
                alltypes = unit.getAllTypes();
                for (IType type : alltypes) {
                    allAnnotations = type.getAnnotations();

                    /* ============ Entity Annotation Diagnostics =========== */
                    IAnnotation EntityAnnotation = null;
                    for (IAnnotation annotation : allAnnotations) {
                        if (isMatchedJavaElement(type, annotation.getElementName(), PersistenceConstants.ENTITY)) {
                            EntityAnnotation = annotation;
                        }
                    }

                    if (EntityAnnotation != null) {
                        // Define boolean requirements for the diagnostics
                        boolean hasPublicOrProtectedNoArgConstructor = false;
                        boolean hasArgConstructor = false;
                        boolean isEntityClassFinal = false;

                        // Get the Methods of the annotated Class
                        for (IMethod method : type.getMethods()) {
                            if (isConstructorMethod(method)) {
                                // We have found a method that is a constructor
                                if (method.getNumberOfParameters() > 0) {
                                    hasArgConstructor = true;
                                    continue;
                                }
                                // Don't need to perform subtractions to check flags because eclipse notifies on
                                // illegal constructor modifiers
                                if (method.getFlags() != Flags.AccPublic && method.getFlags() != Flags.AccProtected)
                                    continue;
                                hasPublicOrProtectedNoArgConstructor = true;
                            }
                            // All Methods of this class should not be final
                            if (isFinal(method.getFlags())) {
                                diagnostics.add(createDiagnostic(method, unit,
                                        "A class using the @Entity annotation cannot contain any methods that are declared final",
                                        PersistenceConstants.DIAGNOSTIC_CODE_FINAL_METHODS, method.getElementType(),
                                        DiagnosticSeverity.Error));
                            }
                        }

                        // Go through the instance variables and make sure no instance vars are final
                        for (IField field : type.getFields()) {
                            // If a field is static, we do not care about it, we care about all other field
                            if (isStatic(field.getFlags())) {
                                continue;
                            }
                            // If we find a non-static variable that is final, this is a problem
                            if (isFinal(field.getFlags())) {
                                diagnostics.add(createDiagnostic(field, unit,
                                        "A class using the @Entity annotation cannot contain any persistent instance variables that are declared final",
                                        PersistenceConstants.DIAGNOSTIC_CODE_FINAL_VARIABLES, field.getElementType(),
                                        DiagnosticSeverity.Error));
                            }
                        }

                        // Ensure that the Entity class is not given a final modifier
                        if (isFinal(type.getFlags()))
                            isEntityClassFinal = true;

                        // Create Diagnostics if needed
                        if (!hasPublicOrProtectedNoArgConstructor && hasArgConstructor) {
                            diagnostics.add(createDiagnostic(type, unit,
                                    "A class using the @Entity annotation must contain a public or protected constructor with no arguments.",
                                    PersistenceConstants.DIAGNOSTIC_CODE_MISSING_EMPTY_CONSTRUCTOR, null,
                                    DiagnosticSeverity.Error));
                        }

                        if (isEntityClassFinal) {
                            diagnostics.add(createDiagnostic(type, unit,
                                    "A class using the @Entity annotation must not be final.",
                                    PersistenceConstants.DIAGNOSTIC_CODE_FINAL_CLASS, type.getElementType(),
                                    DiagnosticSeverity.Error));
                        }
                    }
                }
            } catch (JavaModelException e) {
                JakartaCorePlugin.logException("Cannot calculate persistence diagnostics", e);
            }
        }
        // We do not do anything if the found unit is null
    }
}
