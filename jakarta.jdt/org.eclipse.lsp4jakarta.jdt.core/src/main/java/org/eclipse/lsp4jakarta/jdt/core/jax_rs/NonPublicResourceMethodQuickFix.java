/*******************************************************************************
 * Copyright (c) 2021, 2023 IBM Corporation, Matthew Shocrylas and others.
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

package org.eclipse.lsp4jakarta.jdt.core.jax_rs;

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
import org.eclipse.lsp4jakarta.jdt.codeAction.IJavaCodeActionParticipant;
import org.eclipse.lsp4jakarta.jdt.codeAction.JavaCodeActionContext;
import org.eclipse.lsp4jakarta.jdt.codeAction.proposal.ChangeCorrectionProposal;
import org.eclipse.lsp4jakarta.jdt.codeAction.proposal.ModifyModifiersProposal;
import org.eclipse.lsp4jakarta.jdt.core.Messages;

/**
 * Quick fix for ResourceMethodDiagnosticsCollector that uses
 * ModifyModifiersProposal.
 * 
 * @author Matthew Shocrylas
 *
 */
public class NonPublicResourceMethodQuickFix implements IJavaCodeActionParticipant {

    @Override
    public List<? extends CodeAction> getCodeActions(JavaCodeActionContext context, Diagnostic diagnostic,
            IProgressMonitor monitor) throws CoreException {
        List<CodeAction> codeActions = new ArrayList<>();
        ASTNode node = context.getCoveredNode();
        MethodDeclaration parentNode = (MethodDeclaration) node.getParent();
        IMethodBinding parentMethod = parentNode.resolveBinding();

        if (parentMethod != null) {

            final String TITLE_MESSAGE = Messages.getMessage("MakeMethodPublic");

            ChangeCorrectionProposal proposal = new ModifyModifiersProposal(TITLE_MESSAGE, context.getCompilationUnit(),
                    context.getASTRoot(), parentMethod, 0, null, Arrays.asList("public"));

            // Convert the proposal to LSP4J CodeAction
            CodeAction codeAction = context.convertToCodeAction(proposal, diagnostic);
            codeAction.setTitle(TITLE_MESSAGE);
            codeActions.add(codeAction);
        }
        return codeActions;
    }

}
