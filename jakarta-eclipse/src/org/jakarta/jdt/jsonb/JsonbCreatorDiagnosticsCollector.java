/*******************************************************************************
* Copyright (c) 2020 IBM Corporation, Matheus Cruz and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0.
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     IBM Corporation, Matheus Cruz - initial API and implementation
*******************************************************************************/

package org.jakarta.jdt.jsonb;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Range;
import org.jakarta.jdt.DiagnosticsCollector;
import org.jakarta.jdt.JDTUtils;
import org.jakarta.lsp4e.Activator;

public class JsonbCreatorDiagnosticsCollector implements DiagnosticsCollector {

    @Override
    public void completeDiagnostic(Diagnostic diagnostic) {

        diagnostic.setSource(JsonbConstants.DIAGNOSTIC_SOURCE);
        diagnostic.setSeverity(DiagnosticSeverity.Error);
    }

    @Override
    public void collectDiagnostics(ICompilationUnit unit, List<Diagnostic> diagnostics) {

        if (Objects.isNull(unit)) {
            return;
        }

        try {

            IType[] types = unit.getAllTypes();

            boolean annotationTwice = false;
            boolean alreadyAnnotaded = false;

            List<IAnnotation> annotations = new ArrayList<>();

            for (IType type : types) {

                IMethod[] methods = type.getMethods();

                for (IMethod method : methods) {

                    IAnnotation jsonbCreatorAnnotation = method.getAnnotation(JsonbConstants.JSONB_CREATOR);

                    if (method.isConstructor()) {

                        if (!Objects.isNull(jsonbCreatorAnnotation)) {

                            if (method.isConstructor() && alreadyAnnotaded) {
                                annotations.add(jsonbCreatorAnnotation);
                                annotationTwice = true;
                            }

                            if (method.isConstructor()) {
                                annotations.add(jsonbCreatorAnnotation);
                                alreadyAnnotaded = true;
                            }
                        }

                    }

                    if (Flags.isStatic(method.getFlags()) && alreadyAnnotaded) {
                        annotations.add(jsonbCreatorAnnotation);
                        annotationTwice = true;
                    }
                }

                if (annotationTwice) {

                    for (IAnnotation annotation : annotations) {
                        Diagnostic diagnostic = createDiagnosticBy(unit, annotation);
                        diagnostics.add(diagnostic);
                    }
                }
            }

        } catch (JavaModelException e) {
            Activator.logException("Cannot calculate jakarta-jsonb diagnostics", e);
        }
    }

    private Diagnostic createDiagnosticBy(ICompilationUnit unit, IAnnotation jsonbCreatorAnnotation)
            throws JavaModelException {

        ISourceRange sourceRange = JDTUtils.getNameRange(jsonbCreatorAnnotation);

        Range range = JDTUtils.toRange(unit, sourceRange.getOffset(), sourceRange.getLength());

        return new Diagnostic(range, JsonbConstants.ERROR_MESSAGE);

    }
}
