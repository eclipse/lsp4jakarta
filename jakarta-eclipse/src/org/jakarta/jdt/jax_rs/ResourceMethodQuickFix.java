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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.internal.core.manipulation.util.BasicElementLabels;
import org.eclipse.jdt.internal.corext.dom.Bindings;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;
import org.jakarta.codeAction.IJavaCodeActionParticipant;
import org.jakarta.codeAction.JavaCodeActionContext;
import org.jakarta.codeAction.proposal.ChangeCorrectionProposal;
import org.jakarta.codeAction.proposal.ExtendClassProposal;
import org.jakarta.jdt.servlet.ServletConstants;


public class ResourceMethodQuickFix implements IJavaCodeActionParticipant {

    @Override
    public List<? extends CodeAction> getCodeActions(JavaCodeActionContext context, Diagnostic diagnostic,
            IProgressMonitor monitor) throws CoreException {
        ASTNode node = context.getCoveredNode();
        ITypeBinding parentType = Bindings.getBindingOfParentType(node);
        if (parentType != null) {
            List<CodeAction> codeActions = new ArrayList<>();
            final String TITLE_MESSAGE = "Make method public";
            String args[] = { BasicElementLabels.getJavaElementName(parentType.getName()),
                    BasicElementLabels.getJavaElementName(Jax_RSConstants.RESOURCE_METHOD) };
            ChangeCorrectionProposal proposal = new ExtendClassProposal(MessageFormat.format(TITLE_MESSAGE, args),
                    context.getCompilationUnit(), parentType, context.getASTRoot(), 
                    "test.words", 0);
            CodeAction codeAction = context.convertToCodeAction(proposal, diagnostic);
            return codeActions;
        }
        return null;
    }

}
