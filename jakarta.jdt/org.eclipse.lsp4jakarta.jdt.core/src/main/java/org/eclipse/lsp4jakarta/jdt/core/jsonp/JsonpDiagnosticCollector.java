/*******************************************************************************
 * Copyright (c) 2022 IBM Corporation and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Yijia Jing
 *******************************************************************************/

package org.eclipse.lsp4jakarta.jdt.core.jsonp;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4jakarta.jdt.core.ASTUtils;
import org.eclipse.lsp4jakarta.jdt.core.DiagnosticsCollector;
import org.eclipse.lsp4jakarta.jdt.core.JDTUtils;
import org.eclipse.lsp4jakarta.jdt.core.JakartaCorePlugin;


public class JsonpDiagnosticCollector implements DiagnosticsCollector {

    public JsonpDiagnosticCollector() {
    }

    @Override
    public void completeDiagnostic(Diagnostic diagnostic) {
        diagnostic.setSource(JsonpConstants.DIAGNOSTIC_SOURCE);
        diagnostic.setSeverity(JsonpConstants.SEVERITY);
    }

    @Override
    public void collectDiagnostics(ICompilationUnit unit, List<Diagnostic> diagnostics) {
        if (unit == null) {
            return;
        }
        List<MethodInvocation> allMethodInvocations = ASTUtils.getMethodInvocations(unit);
        List<MethodInvocation> createPointerInvocations = allMethodInvocations.stream()
                .filter(this::isCreatePointerInvocation).collect(Collectors.toList());
        for (MethodInvocation m: createPointerInvocations) {
            Expression arg = (Expression) m.arguments().get(0);
            if (isInvalidArgument(arg)) {
                // If the argument supplied to a createPointer invocation is a String literal and is neither an empty String 
                // or a sequence of '/' prefixed tokens, a diagnostic highlighting the invalid argument is created.
                try {
                    Range range = JDTUtils.toRange(unit, arg.getStartPosition(), arg.getLength());
                    Diagnostic diagnostic = new Diagnostic(range, JsonpConstants.CREATE_POINTER_ERROR_MESSAGE);
                    diagnostic.setCode(JsonpConstants.DIAGNOSTIC_CODE_CREATE_POINTER);
                    completeDiagnostic(diagnostic);
                    diagnostics.add(diagnostic);
                } catch (JavaModelException e) {
                    JakartaCorePlugin.logException("Cannot calculate diagnostics", e);
                }
            }
        }
    }

    private boolean isCreatePointerInvocation(MethodInvocation m) {
        return m.toString().startsWith(JsonpConstants.CREATE_POINTER) && m.arguments().size() == 1;
    }

    private boolean isInvalidArgument(Expression arg) {
        if (arg instanceof StringLiteral) {
            String argValue = ((StringLiteral)arg).getLiteralValue();
            if (!(argValue.isEmpty() || argValue.matches("^(\\/[^\\/]+)+$"))) {
                return true;
            }
        }
        return false;
    }
}
