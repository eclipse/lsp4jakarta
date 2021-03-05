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
package org.jakarta.jdt.persistence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.internal.corext.dom.Bindings;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;
import org.jakarta.codeAction.IJavaCodeActionParticipant;
import org.jakarta.codeAction.JavaCodeActionContext;
import org.jakarta.codeAction.proposal.AddConstructorProposal;
import org.jakarta.codeAction.proposal.ChangeCorrectionProposal;
import org.jakarta.codeAction.proposal.ModifyModifiersProposal;

/**
 * QuickFix for fixing {@link PersistenceConstants#DIAGNOSTIC_CODE_MISSING_ATTRIBUTES} error
 * by providing several code actions to remove incorrect modifiers or add missing constructor:
 * 
 * {@link PersistenceConstants#DIAGNOSTIC_CODE_MISSING_EMPTY_CONSTRUCTOR}
 * <ul>
 * <li> Add a (no-arg) void constructor to this class if the class has other constructors
 * which do not conform to this
 * </ul>
 * 
 * {@link PersistenceConstants#DIAGNOSTIC_CODE_FINAL_METHODS}
 * <ul>
 * <li> Remove the FINAL modifier from all methods in this class
 * </ul>
 *
 * {@link PersistenceConstants#DIAGNOSTIC_CODE_FINAL_VARIABLES}
 * <ul>
 * <li> Remove the FINAL modifier from all variables in this class
 * </ul>
 * 
 * {@link PersistenceConstants#DIAGNOSTIC_CODE_FINAL_CLASS}
 * <ul>
 * <li> Remove the FINAL modifier from this class
 * </ul>
 * 
 * @author Leslie Dawson (lamminade)
 *
 */
public class PersistenceEntityQuickFix implements IJavaCodeActionParticipant {

    @Override
    public List<? extends CodeAction> getCodeActions(JavaCodeActionContext context, Diagnostic diagnostic,
            IProgressMonitor monitor) throws CoreException {
        ASTNode node = context.getCoveredNode();
        IBinding parentType = getBinding(node);
        if (parentType != null) {
            List<CodeAction> codeActions = new ArrayList<>();
            
            // add constructor
            if (diagnostic.getCode().getLeft().equals(PersistenceConstants.DIAGNOSTIC_CODE_MISSING_EMPTY_CONSTRUCTOR)) {
                codeActions.addAll(addConstructor(diagnostic, context, parentType));
            }
            
            // remove modifiers
            if (diagnostic.getCode().getLeft().equals(PersistenceConstants.DIAGNOSTIC_CODE_FINAL_METHODS) 
                    || diagnostic.getCode().getLeft().equals(PersistenceConstants.DIAGNOSTIC_CODE_FINAL_VARIABLES) 
                    || diagnostic.getCode().getLeft().equals(PersistenceConstants.DIAGNOSTIC_CODE_FINAL_CLASS)) {
                codeActions.addAll(removeModifiers(diagnostic, context, parentType));
            }
            
            return codeActions;
        }
        return null;
    }
    
    protected static IBinding getBinding(ASTNode node) {
        if (node.getParent() instanceof VariableDeclarationFragment) {
            VariableDeclarationFragment fragment = (VariableDeclarationFragment) node.getParent();
            return ((VariableDeclarationFragment) node.getParent()).resolveBinding();
        }
        return Bindings.getBindingOfParentType(node);
    }
    
    private List<CodeAction> addConstructor(Diagnostic diagnostic, JavaCodeActionContext context, IBinding parentType) throws CoreException {
        List<CodeAction> codeActions = new ArrayList<>();

        // option for protected constructor
        String name = "Add a no-arg protected constructor to this class";
        ChangeCorrectionProposal proposal = new AddConstructorProposal(name,
                context.getCompilationUnit(), context.getASTRoot(), parentType, 0);
        CodeAction codeAction = context.convertToCodeAction(proposal, diagnostic);

        if (codeAction != null) {
            codeActions.add(codeAction);
        }

        // option for public constructor
        name = "Add a no-arg public constructor to this class";
        proposal = new AddConstructorProposal(name,
                context.getCompilationUnit(), context.getASTRoot(), parentType, 0, "public");
         codeAction = context.convertToCodeAction(proposal, diagnostic);

        if (codeAction != null) {
            codeActions.add(codeAction);
        }

        return codeActions;
    }
    
    private List<CodeAction> removeModifiers(Diagnostic diagnostic, JavaCodeActionContext context, IBinding parentType) throws CoreException {
        List<CodeAction> codeActions = new ArrayList<>();
        ASTNode coveredNode = null;
        
        String type = "";
        if (diagnostic.getCode().getLeft().equals(PersistenceConstants.DIAGNOSTIC_CODE_FINAL_METHODS)) {
            type = "method";
            coveredNode = context.getCoveredNode();
        } else if (diagnostic.getCode().getLeft().equals(PersistenceConstants.DIAGNOSTIC_CODE_FINAL_VARIABLES)) {
            type = "variable";
        } else if (diagnostic.getCode().getLeft().equals(PersistenceConstants.DIAGNOSTIC_CODE_FINAL_CLASS)) {
            type = "class";
        }
        
        String name = "Remove the 'final' modifier from this ";
        name = name.concat(type);
        ChangeCorrectionProposal proposal = new ModifyModifiersProposal(name, context.getCompilationUnit(), 
                context.getASTRoot(), parentType, 0, coveredNode, new ArrayList<>(), Arrays.asList("final"));
        CodeAction codeAction = context.convertToCodeAction(proposal, diagnostic);
        
        if (codeAction != null) {
            codeAction.setTitle(name);
            codeActions.add(codeAction);
        }
        return codeActions;
    }
    
}
