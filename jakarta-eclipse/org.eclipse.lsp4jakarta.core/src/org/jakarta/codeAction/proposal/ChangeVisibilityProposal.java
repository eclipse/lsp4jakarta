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

package org.jakarta.codeAction.proposal;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.internal.core.manipulation.dom.ASTResolving;
import org.eclipse.lsp4j.CodeActionKind;

/**
 * Code action proposal for changing the visibility modifier of a class method.
 * Used by JAX-RS ResourceMethodQuickFix.
 * 
 * @author  Matthew Shocrylas
 * @see     CodeActionHandler
 * @see     ResourceMethodQuickFix
 * 
 */
public class ChangeVisibilityProposal extends ChangeCorrectionProposal {

    private final CompilationUnit fInvocationNode;
    private final IBinding fBinding;
    private final String fVisibility;
    
    /**
     * Constructor for ChangeVisibilityProposal. 
     * 
     * @param visibility    a valid visibility modifier which will replace the method's current one.
     * 
     */
    public ChangeVisibilityProposal(String label, ICompilationUnit targetCU, CompilationUnit invocationNode,
            IBinding binding, int relevance, String visibility) {
        super(label, CodeActionKind.QuickFix, targetCU, null, relevance);
        fInvocationNode = invocationNode;
        fBinding = binding;
        fVisibility = visibility;
    }
    
    @Override
    protected ASTRewrite getRewrite() throws CoreException {
        ASTNode declNode = null;
        ASTNode boundNode = fInvocationNode.findDeclaringNode(fBinding);
        CompilationUnit newRoot = fInvocationNode;
        
        declNode = boundNode;
        if (boundNode != null) {
            declNode = boundNode;
        } else {
            newRoot = ASTResolving.createQuickFixAST(getCompilationUnit(), null);
            declNode = newRoot.findDeclaringNode(fBinding.getKey());
        }
        
        AST ast = declNode.getAST();
        ASTRewrite rewrite = ASTRewrite.create(ast);
        
        // Make visibility marker
        Modifier marker = ast.newModifier(Modifier.ModifierKeyword.toKeyword(fVisibility));
        
        ListRewrite modifiersList = rewrite.getListRewrite(declNode, MethodDeclaration.MODIFIERS2_PROPERTY);
        List<ASTNode> modifiers = (List<ASTNode>) declNode.getStructuralProperty(MethodDeclaration.MODIFIERS2_PROPERTY);
        
        for (ASTNode modifier : modifiers) {
            if (modifier instanceof Modifier) {
                Modifier.ModifierKeyword modKeyword = ((Modifier) modifier).getKeyword();
                // Check if the modifier is private, public or protected
                if (modKeyword.equals(Modifier.ModifierKeyword.PRIVATE_KEYWORD) ||
                        modKeyword.equals(Modifier.ModifierKeyword.PUBLIC_KEYWORD) ||
                        modKeyword.equals(Modifier.ModifierKeyword.PROTECTED_KEYWORD)) {
                    
                    modifiersList.replace(modifier, marker, null);
                    return rewrite;
                }
            }
        }
        // If no visibility modifier, add to the end of list
        modifiersList.insertLast(marker, null);
        
        return rewrite;
    }
}
