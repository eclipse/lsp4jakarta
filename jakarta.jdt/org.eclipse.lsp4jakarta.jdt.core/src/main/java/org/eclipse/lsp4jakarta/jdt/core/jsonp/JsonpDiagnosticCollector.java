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
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4jakarta.jdt.core.ASTUtils;
import org.eclipse.lsp4jakarta.jdt.core.AbstractDiagnosticsCollector;
import org.eclipse.lsp4jakarta.jdt.core.JDTUtils;
import org.eclipse.lsp4jakarta.jdt.core.JakartaCorePlugin;
import org.eclipse.lsp4jakarta.jdt.core.Messages;


public class JsonpDiagnosticCollector extends AbstractDiagnosticsCollector {

    public JsonpDiagnosticCollector() {
        super();
    }
    
    @Override
    protected String getDiagnosticSource() {
        return JsonpConstants.DIAGNOSTIC_SOURCE;
    }

    @Override
    public void collectDiagnostics(ICompilationUnit unit, List<Diagnostic> diagnostics) {
        if (unit == null) {
            return;
        }
        List<MethodInvocation> allMethodInvocations = ASTUtils.getMethodInvocations(unit);
        List<MethodInvocation> createPointerInvocations = allMethodInvocations.stream()
                .filter(mi -> {
                    try {
                        return isMatchedJsonCreatePointer(unit, mi);
                    } catch (JavaModelException e) {
                        return false;
                    }
                }).collect(Collectors.toList());
        for (MethodInvocation m: createPointerInvocations) {
            Expression arg = (Expression) m.arguments().get(0);
            if (isInvalidArgument(arg)) {
                // If the argument supplied to a createPointer invocation is a String literal and is neither an empty String 
                // or a sequence of '/' prefixed tokens, a diagnostic highlighting the invalid argument is created.
                try {
                    Range range = JDTUtils.toRange(unit, arg.getStartPosition(), arg.getLength());
                    Diagnostic diagnostic = new Diagnostic(range, Messages.getMessage("CreatePointerErrorMessage"));
                    completeDiagnostic(diagnostic, JsonpConstants.DIAGNOSTIC_CODE_CREATE_POINTER);
                    diagnostics.add(diagnostic);
                } catch (JavaModelException e) {
                    JakartaCorePlugin.logException("Cannot calculate diagnostics", e);
                }
            }
        }
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
    
    private boolean isMatchedJsonCreatePointer(ICompilationUnit unit, MethodInvocation mi)
            throws JavaModelException {
        if (mi.arguments().size() == 1 && JsonpConstants.CREATE_POINTER.equals(mi.getName().getIdentifier())
                && mi.getExpression() != null) {
            Expression ex = mi.getExpression();
            String qualifier = ex.toString();
            if (JsonpConstants.JSON_FQ_NAME.endsWith(qualifier)) {
                // For performance reason, we check if the import of Java element name is
                // declared
                if (isImportedJavaElement(unit, JsonpConstants.JSON_FQ_NAME) == true)
                    return true;
                // only check fully qualified java element
                if (JsonpConstants.JSON_FQ_NAME.equals(qualifier)) {
                    ITypeBinding itb = ex.resolveTypeBinding();
                    return itb != null && qualifier.equals(itb.getQualifiedName());
                }
            }
        }
        return false;
    }
}
