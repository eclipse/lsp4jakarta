/*******************************************************************************
* Copyright (c) 2021 IBM Corporation and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0.
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     IBM Corporation - initial API and implementation
*******************************************************************************/

package org.eclipse.lsp4jakarta.jdt.core.annotations;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4jakarta.jdt.codeAction.JavaCodeActionContext;
import org.eclipse.lsp4jakarta.jdt.codeAction.proposal.ChangeCorrectionProposal;
import org.eclipse.lsp4jakarta.jdt.codeAction.proposal.RemoveParamsProposal;
import org.eclipse.lsp4jakarta.jdt.codeAction.proposal.quickfix.RemoveModifierConflictQuickFix;

/**
 * Quickfix for removing method parameters with PostConstruct
 * 
 * 
 * @author Zijian Pei
 *
 */
public class PostConstructQuickFix extends RemoveModifierConflictQuickFix {
	public List<? extends CodeAction> getCodeActions(JavaCodeActionContext context, Diagnostic diagnostic,
            IProgressMonitor monitor) throws CoreException {
        ASTNode node = context.getCoveredNode();
        MethodDeclaration parentNode = (MethodDeclaration) node.getParent();        
        IMethodBinding parentMethod = parentNode.resolveBinding();
        List<CodeAction> codeActions = new ArrayList<>();
        List<SingleVariableDeclaration> parameters = (List<SingleVariableDeclaration>) parentNode.parameters();        
        if (diagnostic.getCode().getLeft().equals(AnnotationConstants.DIAGNOSTIC_CODE_POSTCONSTRUCT_PARAMS)) {           
                String name = "Remove all parameters";
                ChangeCorrectionProposal proposal = new RemoveParamsProposal(name,
                        context.getCompilationUnit(), context.getASTRoot(), parentMethod, 0, parameters, null);
                // Convert the proposal to LSP4J CodeAction
                CodeAction codeAction = context.convertToCodeAction(proposal, diagnostic);
                codeActions.add(codeAction);  
        }
        return codeActions;
    }

}
