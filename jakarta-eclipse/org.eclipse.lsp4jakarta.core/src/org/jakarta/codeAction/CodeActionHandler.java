/*******************************************************************************
* Copyright (c) 2020 Red Hat Inc. and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
* which is available at https://www.apache.org/licenses/LICENSE-2.0.
*
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
*
* Contributors:
*     Red Hat Inc. - initial API and implementation
*******************************************************************************/

package org.jakarta.codeAction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.manipulation.CoreASTProvider;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Range;
import org.jakarta.jdt.JDTUtils;
import org.jakarta.jdt.JsonRpcHelpers;
import org.jakarta.jdt.persistence.PersistenceAnnotationQuickFix;
import org.jakarta.jdt.persistence.PersistenceConstants;
import org.jakarta.jdt.jax_rs.Jax_RSConstants;
import org.jakarta.jdt.jax_rs.ResourceMethodQuickFix;
import org.jakarta.jdt.cdi.ManagedBeanConstants;
import org.jakarta.jdt.cdi.ManagedBeanQuickFix;
import org.jakarta.jdt.servlet.CompleteFilterAnnotationQuickFix;
import org.jakarta.jdt.servlet.CompleteServletAnnotationQuickFix;
import org.jakarta.jdt.servlet.FilterImplementationQuickFix;
import org.jakarta.jdt.servlet.HttpServletQuickFix;
import org.jakarta.jdt.servlet.ListenerImplementationQuickFix;
import org.jakarta.jdt.servlet.ServletConstants;
import org.jakarta.jdt.persistence.DeleteConflictMapKeyQuickFix;
import org.jakarta.jdt.persistence.PersistenceConstants;
import org.jakarta.lsp4e.Activator;

import io.microshed.jakartals.commons.JakartaJavaCodeActionParams;

/**
 * Code action handler. Partially reused from
 * https://github.com/eclipse/lsp4mp/blob/b88710cc54170844717f655b9bff8bb4c4649a8d/microprofile.jdt/org.eclipse.lsp4mp.jdt.core/src/main/java/org/eclipse/lsp4mp/jdt/internal/core/java/codeaction/CodeActionHandler.java
 * Modified to fit the purposes of the Jakarta Language Server with deletions of
 * some unnecessary methods and modifications.
 *
 * @author credit to Angelo ZERR
 *
 */
public class CodeActionHandler {

    public List<CodeAction> codeAction(JakartaJavaCodeActionParams params, JDTUtils utils, IProgressMonitor monitor) {
        String uri = params.getUri();
        ICompilationUnit unit = utils.resolveCompilationUnit(uri);
        if (unit == null) {
            return Collections.emptyList();
        }
        try {
            Range range = params.getRange();
            int startOffset = toOffset(unit.getBuffer(), range.getStart().getLine(), range.getStart().getCharacter());
            int endOffset = toOffset(unit.getBuffer(), range.getEnd().getLine(), range.getEnd().getCharacter());
            JavaCodeActionContext context = new JavaCodeActionContext(unit, startOffset, endOffset - startOffset, utils,
                    params);
            context.setASTRoot(getASTRoot(unit, monitor));

            List<CodeAction> codeActions = new ArrayList<>();

            HttpServletQuickFix HttpServletQuickFix = new HttpServletQuickFix();
            FilterImplementationQuickFix FilterImplementationQuickFix = new FilterImplementationQuickFix();
            ListenerImplementationQuickFix ListenerImplementationQuickFix = new ListenerImplementationQuickFix();
            CompleteServletAnnotationQuickFix CompleteServletAnnotationQuickFix = new CompleteServletAnnotationQuickFix();
            CompleteFilterAnnotationQuickFix CompleteFilterAnnotationQuickFix = new CompleteFilterAnnotationQuickFix();
            PersistenceAnnotationQuickFix PersistenceAnnotationQuickFix = new PersistenceAnnotationQuickFix();
            DeleteConflictMapKeyQuickFix DeleteConflictMapKeyQuickFix = new DeleteConflictMapKeyQuickFix();
            ResourceMethodQuickFix ResourceMethodQuickFix = new ResourceMethodQuickFix();
            ManagedBeanQuickFix ManagedBeanQuickFix = new ManagedBeanQuickFix();

            for (Diagnostic diagnostic : params.getContext().getDiagnostics()) {
                try {
                    if (diagnostic.getCode().getLeft().equals(ServletConstants.DIAGNOSTIC_CODE)) {
                        codeActions.addAll(HttpServletQuickFix.getCodeActions(context, diagnostic, monitor));
                    }
                    if (diagnostic.getCode().getLeft().equals(ServletConstants.DIAGNOSTIC_CODE_FILTER)) {
                        codeActions.addAll(FilterImplementationQuickFix.getCodeActions(context, diagnostic, monitor));
                    }
                    if (diagnostic.getCode().getLeft().equals(ServletConstants.DIAGNOSTIC_CODE_LISTENER)) {
                        codeActions.addAll(ListenerImplementationQuickFix.getCodeActions(context, diagnostic, monitor));
                    }
                    if (diagnostic.getCode().getLeft().equals(ServletConstants.DIAGNOSTIC_CODE_MISSING_ATTRIBUTE)
                            || diagnostic.getCode().getLeft()
                                    .equals(ServletConstants.DIAGNOSTIC_CODE_DUPLICATE_ATTRIBUTES)) {
                        codeActions
                                .addAll(CompleteServletAnnotationQuickFix.getCodeActions(context, diagnostic, monitor));
                    }
                    if (diagnostic.getCode().getLeft().equals(ServletConstants.DIAGNOSTIC_CODE_FILTER_MISSING_ATTRIBUTE)
                            || diagnostic.getCode().getLeft()
                                    .equals(ServletConstants.DIAGNOSTIC_CODE_FILTER_DUPLICATE_ATTRIBUTES)) {
                        codeActions
                                .addAll(CompleteFilterAnnotationQuickFix.getCodeActions(context, diagnostic, monitor));
                    }
                    if (diagnostic.getCode().getLeft().equals(Jax_RSConstants.DIAGNOSTIC_CODE_NON_PUBLIC)) {
                        codeActions.addAll(ResourceMethodQuickFix.getCodeActions(context, diagnostic, monitor));
                    }
                    if(diagnostic.getCode().getLeft().equals(PersistenceConstants.DIAGNOSTIC_CODE_MISSING_ATTRIBUTES)) {
                    	codeActions.addAll(PersistenceAnnotationQuickFix.getCodeActions(context, diagnostic, monitor));
                    }
                    if (diagnostic.getCode().getLeft()
                            .equals(PersistenceConstants.DIAGNOSTIC_CODE_INVALID_ANNOTATION)) {
                        codeActions.addAll(DeleteConflictMapKeyQuickFix.getCodeActions(context, diagnostic, monitor));
                    }
                    if (diagnostic.getCode().getLeft().equals(ManagedBeanConstants.DIAGNOSTIC_CODE)) {
                        codeActions.addAll(ManagedBeanQuickFix.getCodeActions(context, diagnostic, monitor));
                    }
                } catch (CoreException e) {
                    e.printStackTrace();
                }
            }
            return codeActions;

        } catch (JavaModelException e) {
            Activator.logException("Failed to retrieve Jakarta code action", e);
        }
        return null;
    }

    private static CompilationUnit getASTRoot(ICompilationUnit unit, IProgressMonitor monitor) {
        return CoreASTProvider.getInstance().getAST(unit, CoreASTProvider.WAIT_YES, monitor);
    }

    public int toOffset(IBuffer buffer, int line, int column) {
        return JsonRpcHelpers.toOffset(buffer, line, column);
    }
}