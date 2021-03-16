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

            List<IMethod> methods = new ArrayList<>();

            for (IType type : unit.getAllTypes()) {

                for (IMethod method : type.getMethods()) {

                    for (IAnnotation annotation : method.getAnnotations()) {

                        if (method.isConstructor() || Flags.isStatic(method.getFlags())) {

                            if (annotation.getElementName().equals(JsonbConstants.JSONB_CREATOR)) {
                                methods.add(method);
                            }
                        }
                    }
                }

                if (methods.size() > JsonbConstants.MAX_METHOD_WITH_JSONBCREATOR) {

                    for (IMethod method : methods) {
                        Diagnostic diagnostic = createDiagnosticBy(unit, method);
                        diagnostic.setCode(JsonbConstants.DIAGNOSTIC_CODE_ANNOTATION);
                        diagnostics.add(diagnostic);
                    }
                }
            }

        } catch (JavaModelException e) {
            Activator.logException("Cannot calculate jakarta-jsonb diagnostics", e);
        }
    }   

    private Diagnostic createDiagnosticBy(ICompilationUnit unit, IMethod method) throws JavaModelException {

        ISourceRange sourceRange = JDTUtils.getNameRange(method);

        Range range = JDTUtils.toRange(unit, sourceRange.getOffset(), sourceRange.getLength());

        return new Diagnostic(range, JsonbConstants.ERROR_MESSAGE);

    }
}
