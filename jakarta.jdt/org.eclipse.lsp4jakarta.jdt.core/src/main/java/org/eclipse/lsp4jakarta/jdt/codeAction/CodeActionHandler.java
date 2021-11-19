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

package org.eclipse.lsp4jakarta.jdt.codeAction;

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
import org.eclipse.lsp4jakarta.commons.JakartaJavaCodeActionParams;
import org.eclipse.lsp4jakarta.jdt.core.JDTUtils;
import org.eclipse.lsp4jakarta.jdt.core.JsonRpcHelpers;
import org.eclipse.lsp4jakarta.jdt.core.beanvalidation.BeanValidationConstants;
import org.eclipse.lsp4jakarta.jdt.core.beanvalidation.BeanValidationQuickFix;
import org.eclipse.lsp4jakarta.jdt.core.persistence.PersistenceAnnotationQuickFix;
import org.eclipse.lsp4jakarta.jdt.core.persistence.PersistenceConstants;
import org.eclipse.lsp4jakarta.jdt.core.persistence.PersistenceEntityQuickFix;
import org.eclipse.lsp4jakarta.jdt.core.jax_rs.Jax_RSConstants;
import org.eclipse.lsp4jakarta.jdt.core.jax_rs.NonPublicResourceMethodQuickFix;
import org.eclipse.lsp4jakarta.jdt.core.jax_rs.ResourceMethodMultipleEntityParamsQuickFix;
import org.eclipse.lsp4jakarta.jdt.core.jsonb.JsonbAnnotationQuickFix;
import org.eclipse.lsp4jakarta.jdt.core.jsonb.JsonbConstants;
import org.eclipse.lsp4jakarta.jdt.core.cdi.ConflictProducesInjectQuickFix;
import org.eclipse.lsp4jakarta.jdt.core.cdi.ManagedBeanConstants;
import org.eclipse.lsp4jakarta.jdt.core.cdi.ManagedBeanConstructorQuickFix;
import org.eclipse.lsp4jakarta.jdt.core.cdi.ManagedBeanQuickFix;
import org.eclipse.lsp4jakarta.jdt.core.cdi.ScopeDeclarationQuickFix;
import org.eclipse.lsp4jakarta.jdt.core.di.DependencyInjectionConstants;
import org.eclipse.lsp4jakarta.jdt.core.di.RemoveAbstractModifierQuickFix;
import org.eclipse.lsp4jakarta.jdt.core.di.RemoveFinalModifierQuickFix;
import org.eclipse.lsp4jakarta.jdt.core.di.RemoveInjectAnnotationQuickFix;
import org.eclipse.lsp4jakarta.jdt.core.di.RemoveStaticModifierQuickFix;
import org.eclipse.lsp4jakarta.jdt.core.servlet.CompleteFilterAnnotationQuickFix;
import org.eclipse.lsp4jakarta.jdt.core.servlet.CompleteServletAnnotationQuickFix;
import org.eclipse.lsp4jakarta.jdt.core.servlet.FilterImplementationQuickFix;
import org.eclipse.lsp4jakarta.jdt.core.servlet.HttpServletQuickFix;
import org.eclipse.lsp4jakarta.jdt.core.servlet.ListenerImplementationQuickFix;
import org.eclipse.lsp4jakarta.jdt.core.servlet.ServletConstants;
import org.eclipse.lsp4jakarta.jdt.core.persistence.DeleteConflictMapKeyQuickFix;
import org.eclipse.lsp4jakarta.jdt.core.persistence.PersistenceConstants;
import org.eclipse.lsp4jakarta.jdt.core.JakartaCorePlugin;

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
            NonPublicResourceMethodQuickFix NonPublicResourceMethodQuickFix = new NonPublicResourceMethodQuickFix();
            ResourceMethodMultipleEntityParamsQuickFix ResourceMethodMultipleEntityParamsQuickFix = new ResourceMethodMultipleEntityParamsQuickFix();
            ManagedBeanQuickFix ManagedBeanQuickFix = new ManagedBeanQuickFix();
            PersistenceEntityQuickFix PersistenceEntityQuickFix = new PersistenceEntityQuickFix();
            ConflictProducesInjectQuickFix ConflictProducesInjectQuickFix = new ConflictProducesInjectQuickFix();
            BeanValidationQuickFix BeanValidationQuickFix = new BeanValidationQuickFix();
            ManagedBeanConstructorQuickFix ManagedBeanConstructorQuickFix = new ManagedBeanConstructorQuickFix();
            JsonbAnnotationQuickFix JsonbAnnotationQuickFix = new JsonbAnnotationQuickFix();
            ScopeDeclarationQuickFix ScopeDeclarationQuickFix = new ScopeDeclarationQuickFix();
            RemoveInjectAnnotationQuickFix RemoveInjectAnnotationQuickFix = new RemoveInjectAnnotationQuickFix();
            RemoveFinalModifierQuickFix RemoveFinalModifierQuickFix = new RemoveFinalModifierQuickFix();
            RemoveAbstractModifierQuickFix RemoveAbstractModifierQuickFix = new RemoveAbstractModifierQuickFix();
            RemoveStaticModifierQuickFix RemoveStaticModifierQuickFix = new RemoveStaticModifierQuickFix();
            
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
                        codeActions
                                .addAll(NonPublicResourceMethodQuickFix.getCodeActions(context, diagnostic, monitor));
                    }
                    if (diagnostic.getCode().getLeft().equals(Jax_RSConstants.DIAGNOSTIC_CODE_MULTIPLE_ENTITY_PARAMS)) {
                        codeActions.addAll(ResourceMethodMultipleEntityParamsQuickFix.getCodeActions(context,
                                diagnostic, monitor));
                    }
                    if (diagnostic.getCode().getLeft()
                            .equals(PersistenceConstants.DIAGNOSTIC_CODE_MISSING_ATTRIBUTES)) {
                        codeActions.addAll(PersistenceAnnotationQuickFix.getCodeActions(context, diagnostic, monitor));
                    }
                    if (diagnostic.getCode().getLeft()
                            .equals(PersistenceConstants.DIAGNOSTIC_CODE_INVALID_ANNOTATION)) {
                        codeActions.addAll(DeleteConflictMapKeyQuickFix.getCodeActions(context, diagnostic, monitor));
                    }
                    if (diagnostic.getCode().getLeft().equals(PersistenceConstants.DIAGNOSTIC_CODE_FINAL_METHODS)
                            || diagnostic.getCode().getLeft()
                                    .equals(PersistenceConstants.DIAGNOSTIC_CODE_FINAL_VARIABLES)
                            || diagnostic.getCode().getLeft().equals(PersistenceConstants.DIAGNOSTIC_CODE_FINAL_CLASS)
                            || diagnostic.getCode().getLeft()
                                    .equals(PersistenceConstants.DIAGNOSTIC_CODE_MISSING_EMPTY_CONSTRUCTOR)) {
                        codeActions.addAll(PersistenceEntityQuickFix.getCodeActions(context, diagnostic, monitor));
                    }
                    if (diagnostic.getCode().getLeft().equals(ManagedBeanConstants.DIAGNOSTIC_CODE)) {
                        codeActions.addAll(ManagedBeanQuickFix.getCodeActions(context, diagnostic, monitor));
                    }
                    if (diagnostic.getCode().getLeft().equals(ManagedBeanConstants.DIAGNOSTIC_CODE_PRODUCES_INJECT)) {
                        codeActions.addAll(ConflictProducesInjectQuickFix.getCodeActions(context, diagnostic, monitor));
                    }
                    if (diagnostic.getCode().getLeft().equals(BeanValidationConstants.DIAGNOSTIC_CODE_STATIC)
                            || diagnostic.getCode().getLeft()
                                    .equals(BeanValidationConstants.DIAGNOSTIC_CODE_INVALID_TYPE)) {
                        codeActions.addAll(BeanValidationQuickFix.getCodeActions(context, diagnostic, monitor));
                    }
                    if (diagnostic.getCode().getLeft().equals(ManagedBeanConstants.CONSTRUCTOR_DIAGNOSTIC_CODE)) {
                        codeActions.addAll(ManagedBeanConstructorQuickFix.getCodeActions(context, diagnostic, monitor));
                    }
                    if (diagnostic.getCode().getLeft().equals(JsonbConstants.DIAGNOSTIC_CODE_ANNOTATION)) {
                        codeActions.addAll(JsonbAnnotationQuickFix.getCodeActions(context, diagnostic, monitor));
                    }
                    if(diagnostic.getCode().getLeft().equals(ManagedBeanConstants.DIAGNOSTIC_CODE_SCOPEDECL)) {
                        codeActions.addAll(ScopeDeclarationQuickFix.getCodeActions(context, diagnostic, monitor));
                    }
                    if(diagnostic.getCode().getLeft().equals(DependencyInjectionConstants.DIAGNOSTIC_CODE_INJECT_FINAL)) {
                        codeActions.addAll(RemoveInjectAnnotationQuickFix.getCodeActions(context, diagnostic, monitor));
                        codeActions.addAll(RemoveFinalModifierQuickFix.getCodeActions(context, diagnostic, monitor));
                    }

                    /**if(diagnostic.getCode().getLeft().equals(DependencyInjectionConstants.DIAGNOSTIC_CODE_INJECT_CONSTRUCTOR)) {
                        codeActions.addAll(RemoveInjectAnnotationQuickFix.getCodeActions(context, diagnostic, monitor));
                    }*/

                    if(diagnostic.getCode().getLeft().equals(DependencyInjectionConstants.DIAGNOSTIC_CODE_INJECT_CONSTRUCTOR) ||
                            diagnostic.getCode().getLeft().equals(DependencyInjectionConstants.DIAGNOSTIC_CODE_INJECT_GENERIC)) {
                        codeActions.addAll(RemoveInjectAnnotationQuickFix.getCodeActions(context, diagnostic, monitor));
                    }
                    if(diagnostic.getCode().getLeft().equals(DependencyInjectionConstants.DIAGNOSTIC_CODE_INJECT_ABSTRACT)) {
                        codeActions.addAll(RemoveInjectAnnotationQuickFix.getCodeActions(context, diagnostic, monitor));
                        codeActions.addAll(RemoveAbstractModifierQuickFix.getCodeActions(context, diagnostic, monitor));
                    }
                    if(diagnostic.getCode().getLeft().equals(DependencyInjectionConstants.DIAGNOSTIC_CODE_INJECT_STATIC)) {
                        codeActions.addAll(RemoveInjectAnnotationQuickFix.getCodeActions(context, diagnostic, monitor));
                        codeActions.addAll(RemoveStaticModifierQuickFix.getCodeActions(context, diagnostic, monitor));
                    }

                } catch (CoreException e) {
                    e.printStackTrace();
                }
            }
            return codeActions;

        } catch (JavaModelException e) {
            JakartaCorePlugin.logException("Failed to retrieve Jakarta code action", e);
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