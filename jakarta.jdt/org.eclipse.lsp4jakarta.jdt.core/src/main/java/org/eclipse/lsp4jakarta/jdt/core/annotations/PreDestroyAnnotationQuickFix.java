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
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.internal.corext.dom.Bindings;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4jakarta.jdt.codeAction.IJavaCodeActionParticipant;
import org.eclipse.lsp4jakarta.jdt.codeAction.JavaCodeActionContext;
import org.eclipse.lsp4jakarta.jdt.codeAction.proposal.ChangeCorrectionProposal;
import org.eclipse.lsp4jakarta.jdt.codeAction.proposal.ModifyModifiersProposal;
import org.eclipse.lsp4jakarta.jdt.codeAction.proposal.RemoveParamsProposal;
import org.eclipse.lsp4jakarta.jdt.core.annotations.AnnotationConstants;
import org.eclipse.lsp4jakarta.jdt.core.beanvalidation.BeanValidationConstants;
import org.eclipse.lsp4jakarta.jdt.codeAction.proposal.quickfix.RemoveModifierConflictQuickFix;


/**
 * Quickfix for annotation PreDestory 
 * 1. Removing static from a static method
 * 2. Removing all parameters from method
 * 
 * @author Zijian Pei
 *
 */
public class PreDestroyAnnotationQuickFix implements IJavaCodeActionParticipant {
	
	public List<? extends CodeAction> getCodeActions(JavaCodeActionContext context, Diagnostic diagnostic,
            IProgressMonitor monitor) throws CoreException {
        ASTNode node = context.getCoveredNode();
        MethodDeclaration parentNode = (MethodDeclaration) node.getParent();
        IBinding parentMethod = getBinding(node);
        
        MethodDeclaration parentNode2 = (MethodDeclaration) node.getParent();
        IMethodBinding parentMethod2 = parentNode2.resolveBinding();

        List<CodeAction> codeActions = new ArrayList<>();
        List<SingleVariableDeclaration> parameters = (List<SingleVariableDeclaration>) parentNode.parameters();
        
        if (diagnostic.getCode().getLeft().equals(AnnotationConstants.DIAGNOSTIC_CODE_PREDESTROY_PARAMS)) {           
                String name = "Remove all parameters";
                ChangeCorrectionProposal proposal = new RemoveParamsProposal(name,
                        context.getCompilationUnit(), context.getASTRoot(), parentMethod2, 0, parameters, null);
                // Convert the proposal to LSP4J CodeAction
                CodeAction codeAction = context.convertToCodeAction(proposal, diagnostic);
                codeActions.add(codeAction);  
        }
        

        return codeActions;
    }
	
	
	protected IBinding getBinding(ASTNode node) {
        if (node.getParent() instanceof VariableDeclarationFragment) {
            return ((VariableDeclarationFragment) node.getParent()).resolveBinding();
        }
        return Bindings.getBindingOfParentType(node);
    }
	

}
