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
*    Himanshu Chotwani - initial API and implementation
*******************************************************************************/

package org.eclipse.lsp4jakarta.jdt.codeAction.proposal.quickfix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.internal.corext.dom.Bindings;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4jakarta.jdt.codeAction.IJavaCodeActionParticipant;
import org.eclipse.lsp4jakarta.jdt.codeAction.JavaCodeActionContext;
import org.eclipse.lsp4jakarta.jdt.codeAction.proposal.ModifyModifiersProposal;
import org.eclipse.lsp4jakarta.jdt.core.di.DependencyInjectionConstants;


/**
 * QuickFix for removing modifiers.
 *
 * @author Himanshu Chotwani
 *
 */
public class RemoveModifierConflictQuickFix implements IJavaCodeActionParticipant {
    
    private final String[] modifiers;

    protected final boolean generateOnlyOneCodeAction;
    
    
    /**
     * Constructor for remove modifier quick fix.
     *
     * <p>
     * The participant will generate a CodeAction per modifier.
     * </p>
     *
     * @param modifiers list of modifiers to remove.
     */
    public RemoveModifierConflictQuickFix(String... modifiers) {
        this(false, modifiers);
    }
    
    
    /**
     * Constructor for remove modifiers quick fix.
     *
     * @param generateOnlyOneCodeAction true if the participant must generate a
     *                                  CodeAction which remove the list of
     *                                  modifiers and false otherwise.
     * @param modifiers               list of modifiers to remove.
     */
    public RemoveModifierConflictQuickFix(boolean generateOnlyOneCodeAction, String... modifiers) {
        this.generateOnlyOneCodeAction = generateOnlyOneCodeAction;
        this.modifiers = modifiers;
    }
    
    
    @Override
    public List<? extends CodeAction> getCodeActions(JavaCodeActionContext context, Diagnostic diagnostic,
            IProgressMonitor monitor) throws CoreException {
        ASTNode node = context.getCoveredNode();
        IBinding parentType = getBinding(node);

        List<CodeAction> codeActions = new ArrayList<>();
        removeModifiers(diagnostic, context, parentType, codeActions);

        return codeActions;
    }
    
    protected void removeModifiers(Diagnostic diagnostic, JavaCodeActionContext context, IBinding parentType,
            List<CodeAction> codeActions) throws CoreException {
        if (generateOnlyOneCodeAction) {
            removeModifier(diagnostic, context, parentType, codeActions, modifiers);
        } else {
            for (String modifier : modifiers) {
                removeModifier(diagnostic, context, parentType, codeActions, modifier);
            }
        }
    }
    
    
    /**
     * use setData() API with diagnostic to pass in ElementType in diagnostic collector class.
     *
     */
    private void removeModifier(Diagnostic diagnostic, JavaCodeActionContext context, IBinding parentType,
            List<CodeAction> codeActions, String... modifier) throws CoreException {
        // Remove the modifier and the proper import by using JDT Core Manipulation
        // API
        ASTNode coveredNode = context.getCoveredNode().getParent();
        String type = "";
        if (diagnostic.getData().equals(String.valueOf(IJavaElement.LOCAL_VARIABLE))){
            type = "variable";
        } else if (diagnostic.getData().toString().equals(String.valueOf(IJavaElement.FIELD))) {
            type = "field";
        } else if (diagnostic.getData().equals(String.valueOf(IJavaElement.METHOD))) {
            type = "method";
        } else if (diagnostic.getData().equals(String.valueOf(IJavaElement.CLASS_FILE))) {
            type = "class";
        }

        String name = "Remove " + modifier[0] + " modifier from this ";
        name = name.concat(type);
        ModifyModifiersProposal proposal = new ModifyModifiersProposal(name, context.getCompilationUnit(), 
                context.getASTRoot(), parentType, 0, coveredNode, new ArrayList<>(), Arrays.asList(modifier));
        CodeAction codeAction = context.convertToCodeAction(proposal, diagnostic);

        if (codeAction != null) {
            codeActions.add(codeAction);
        }
    }
    
    
    protected IBinding getBinding(ASTNode node) {
        if (node.getParent() instanceof VariableDeclarationFragment) {
            return ((VariableDeclarationFragment) node.getParent()).resolveBinding();
        }
        return Bindings.getBindingOfParentType(node);
    }

}
