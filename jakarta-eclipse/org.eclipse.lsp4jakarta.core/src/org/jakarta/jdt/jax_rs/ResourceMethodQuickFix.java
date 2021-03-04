/*******************************************************************************
 * Copyright (c) 2021 IBM Corporation, Matthew Shocrylas and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation, Matthew Shocrylas - initial API and implementation
 *******************************************************************************/

package org.jakarta.jdt.jax_rs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;
import org.jakarta.codeAction.IJavaCodeActionParticipant;
import org.jakarta.codeAction.JavaCodeActionContext;
import org.jakarta.codeAction.proposal.ChangeCorrectionProposal;
import org.jakarta.codeAction.proposal.ModifyModifiersProposal;

/**
 * Quick fix for ResourceMethodDiagnosticsCollector that uses ChangeVisibilityProposal.
 * 
 * @author Matthew Shocrylas
 *
 */
public class ResourceMethodQuickFix implements IJavaCodeActionParticipant {

    @Override
    public List<? extends CodeAction> getCodeActions(JavaCodeActionContext context, Diagnostic diagnostic,
            IProgressMonitor monitor) throws CoreException {
        ASTNode node = context.getCoveredNode();
        MethodDeclaration parentNode = (MethodDeclaration) node.getParent();
        IMethodBinding parentMethod = parentNode.resolveBinding();

        if (parentMethod != null) {
            List<CodeAction> codeActions = new ArrayList<>();
            
            final String TITLE_MESSAGE = "Make method public";
            
            ChangeCorrectionProposal proposal = new ModifyModifiersProposal(TITLE_MESSAGE,
                    context.getCompilationUnit(), context.getASTRoot(), parentMethod, 0, Arrays.asList("public"));
            
            // Convert the proposal to LSP4J CodeAction
            CodeAction codeAction = context.convertToCodeAction(proposal, diagnostic);
            codeAction.setTitle(TITLE_MESSAGE);
            codeActions.add(codeAction);
            return codeActions;
        }
        return null;
    }

}
