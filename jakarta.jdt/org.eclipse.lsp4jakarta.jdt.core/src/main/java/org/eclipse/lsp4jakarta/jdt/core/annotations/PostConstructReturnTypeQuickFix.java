/*******************************************************************************
 * Copyright (c) 2022, 2023 IBM Corporation and others.
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
package org.eclipse.lsp4jakarta.jdt.core.annotations;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.jdt.internal.corext.dom.Bindings;
import org.eclipse.lsp4jakarta.jdt.codeAction.IJavaCodeActionParticipant;
import org.eclipse.lsp4jakarta.jdt.codeAction.JavaCodeActionContext;
import org.eclipse.lsp4jakarta.jdt.codeAction.proposal.ChangeCorrectionProposal;
import org.eclipse.lsp4jakarta.jdt.codeAction.proposal.ModifyReturnTypeProposal;
import org.eclipse.lsp4jakarta.jdt.core.Messages;

/**
 * Quick fix for AnnotationDiagnosticsCollector that changes the return type of a method to void.
 * Uses ModifyReturnTypeProposal.
 * 
 * @author Yijia Jing
 *
 */
public class PostConstructReturnTypeQuickFix implements IJavaCodeActionParticipant {

    @Override
    public List<? extends CodeAction> getCodeActions(JavaCodeActionContext context, Diagnostic diagnostic,
            IProgressMonitor monitor) throws CoreException {
        List<CodeAction> codeActions = new ArrayList<>();
        ASTNode node = context.getCoveredNode();
        IBinding parentType = getBinding(node);
        String name = Messages.getMessage("ChangeReturnTypeToVoid");
        ChangeCorrectionProposal proposal = new ModifyReturnTypeProposal(name, context.getCompilationUnit(),
                context.getASTRoot(), parentType, 0, node.getAST().newPrimitiveType(PrimitiveType.VOID));
        CodeAction codeAction = context.convertToCodeAction(proposal, diagnostic);
        codeActions.add(codeAction);
        return codeActions;
    }

    protected IBinding getBinding(ASTNode node) {
        if (node.getParent() instanceof MethodDeclaration) {
            return ((MethodDeclaration) node.getParent()).resolveBinding();
        }
        return Bindings.getBindingOfParentType(node);
    }
}
