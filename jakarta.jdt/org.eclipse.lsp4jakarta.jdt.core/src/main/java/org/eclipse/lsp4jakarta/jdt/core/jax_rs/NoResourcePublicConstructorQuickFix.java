/*******************************************************************************
 * Copyright (c) 2021, 2023 IBM Corporation, Shaunak Tulshibagwale and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation, Shaunak Tulshibagwale
 *******************************************************************************/


package org.eclipse.lsp4jakarta.jdt.core.jax_rs;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.internal.corext.dom.Bindings;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4jakarta.jdt.codeAction.IJavaCodeActionParticipant;
import org.eclipse.lsp4jakarta.jdt.codeAction.JavaCodeActionContext;
import org.eclipse.lsp4jakarta.jdt.codeAction.proposal.AddConstructorProposal;
import org.eclipse.lsp4jakarta.jdt.codeAction.proposal.ChangeCorrectionProposal;
import org.eclipse.lsp4jakarta.jdt.codeAction.proposal.ModifyModifiersProposal;
import org.eclipse.lsp4jakarta.jdt.core.Messages;

/**
 * Quick fix for NoResourcePublicConstructorQuickFix that uses
 * ModifyModifiersProposal.
 * 
 * @author Shaunak Tulshibagwale
 *
 */
public class NoResourcePublicConstructorQuickFix implements IJavaCodeActionParticipant {

    @Override
    public List<? extends CodeAction> getCodeActions(JavaCodeActionContext context, Diagnostic diagnostic,
            IProgressMonitor monitor) throws CoreException {
        List<CodeAction> codeActions = new ArrayList<>();
        ASTNode node = context.getCoveredNode();
        MethodDeclaration parentNode = (MethodDeclaration) node.getParent();
        IMethodBinding parentMethod = parentNode.resolveBinding();
        IBinding parentType = getBinding(node);
        
        if (parentMethod != null) {

            final String TITLE_MESSAGE = Messages.getMessage("MakeConstructorPublic");

            ChangeCorrectionProposal proposal = new ModifyModifiersProposal(TITLE_MESSAGE, context.getCompilationUnit(),
                    context.getASTRoot(), parentMethod, 0, null, Arrays.asList("public"));

            // Convert the proposal to LSP4J CodeAction
            CodeAction codeAction = context.convertToCodeAction(proposal, diagnostic);
            codeAction.setTitle(TITLE_MESSAGE);
            codeActions.add(codeAction);
            
            // option for public constructor
            String name = Messages.getMessage("NoargPublicConstructor");
            proposal = new AddConstructorProposal(name,
                    context.getCompilationUnit(), context.getASTRoot(), parentType, 0, "public");
            codeAction = context.convertToCodeAction(proposal, diagnostic);
            codeActions.add(codeAction);
            
        }
        return codeActions;
    }

    protected static IBinding getBinding(ASTNode node) {
        if (node.getParent() instanceof VariableDeclarationFragment) {
            VariableDeclarationFragment fragment = (VariableDeclarationFragment) node.getParent();
            return ((VariableDeclarationFragment) node.getParent()).resolveBinding();
        }
        return Bindings.getBindingOfParentType(node);
    }
}
