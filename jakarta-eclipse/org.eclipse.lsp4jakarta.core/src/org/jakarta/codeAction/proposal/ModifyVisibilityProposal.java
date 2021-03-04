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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite.ImportRewriteContext;
import org.eclipse.jdt.internal.core.manipulation.dom.ASTResolving;
import org.eclipse.jdt.internal.corext.codemanipulation.ContextSensitiveImportRewriteContext;
import org.eclipse.lsp4j.CodeActionKind;

/**
 * Code action proposal for changing the visibility modifier of a method, field, or type.
 * Used by JAX-RS ResourceMethodQuickFix.
 * 
 * @author  Matthew Shocrylas, Leslie Dawson
 * @see     CodeActionHandler
 * @see     ResourceMethodQuickFix
 * 
 */
public class ModifyVisibilityProposal extends ChangeCorrectionProposal {

    private final CompilationUnit invocationNode;
    private final IBinding binding;
    private final String visibility;
    
    // list of modifiers to add
    private final List<String> modifiersToAdd;

    // list of modifiers (if they exist) to remove
    private final List<String> modifiersToRemove;
    
    /**
     * Constructor for ChangeVisibilityProposal. 
     * 
     * @param visibility    a valid visibility modifier which will replace the method's current one.
     * 
     */
    public ModifyVisibilityProposal(String label, ICompilationUnit targetCU, CompilationUnit invocationNode,
            IBinding binding, int relevance, String visibility) {
        super(label, CodeActionKind.QuickFix, targetCU, null, relevance);
        this.invocationNode = invocationNode;
        this.binding = binding;
        this.visibility = visibility;
        this.modifiersToAdd = Arrays.asList(visibility);
        this.modifiersToRemove = new ArrayList<>();
    }

    public ModifyVisibilityProposal(String label, ICompilationUnit targetCU, CompilationUnit invocationNode,
            IBinding binding, int relevance, List<String> modifiersToAdd, List<String> modifiersToRemove) {
        super(label, CodeActionKind.QuickFix, targetCU, null, relevance);
        this.invocationNode = invocationNode;
        this.binding = binding;
        this.visibility = "";
        this.modifiersToAdd = modifiersToAdd;        
        this.modifiersToRemove = modifiersToRemove;
    }
    
    public ModifyVisibilityProposal(String label, ICompilationUnit targetCU, CompilationUnit invocationNode,
            IBinding binding, int relevance, List<String> modifiersToAdd) {
        super(label, CodeActionKind.QuickFix, targetCU, null, relevance);
        this.invocationNode = invocationNode;
        this.binding = binding;
        this.visibility = "";
        this.modifiersToAdd = modifiersToAdd;        
        this.modifiersToRemove = new ArrayList<>();
    }
    
    @Override
    protected ASTRewrite getRewrite() throws CoreException {
        ASTNode declNode = null;
        ASTNode boundNode = invocationNode.findDeclaringNode(binding);
        CompilationUnit newRoot = invocationNode;
   
        if (boundNode != null) {
            declNode = boundNode;
        } else {
            newRoot = ASTResolving.createQuickFixAST(getCompilationUnit(), null);
            declNode = newRoot.findDeclaringNode(binding.getKey());
        }

        boolean isField = declNode instanceof VariableDeclarationFragment;
        if (isField) {
            declNode = declNode.getParent();
        }
        
        AST ast = declNode.getAST();
        ASTRewrite rewrite = ASTRewrite.create(ast);

        ListRewrite modifiersList = null;
        List<ASTNode> modifiers = new ArrayList();

        switch(declNode.getNodeType()) {
        case ASTNode.METHOD_DECLARATION:
            modifiersList = rewrite.getListRewrite(declNode, MethodDeclaration.MODIFIERS2_PROPERTY);
            modifiers = (List<ASTNode>) declNode.getStructuralProperty(MethodDeclaration.MODIFIERS2_PROPERTY);
            break;
        case ASTNode.FIELD_DECLARATION:
            modifiersList = rewrite.getListRewrite(declNode, FieldDeclaration.MODIFIERS2_PROPERTY);
            modifiers = (List<ASTNode>) declNode.getStructuralProperty(FieldDeclaration.MODIFIERS2_PROPERTY);
            break;
        case ASTNode.TYPE_DECLARATION:
            modifiersList = rewrite.getListRewrite(declNode, TypeDeclaration.MODIFIERS2_PROPERTY);
            modifiers = (List<ASTNode>) declNode.getStructuralProperty(TypeDeclaration.MODIFIERS2_PROPERTY);
            break;
        default:
            modifiersList = null;
        }     

        for (ASTNode modifier : modifiers) {
            if (modifier instanceof Modifier) {
                Modifier.ModifierKeyword modKeyword = ((Modifier) modifier).getKeyword();

                // Check if modifier is in toRemove list
                if (modifiersToRemove.stream().anyMatch(m -> m.equals(modKeyword.toString()))) {
                    modifiersList.remove(modifier, null);
                    continue;
                }

                // Otherwise check if the existing modifier is private, public or protected
                if (modKeyword.equals(Modifier.ModifierKeyword.PRIVATE_KEYWORD) ||
                        modKeyword.equals(Modifier.ModifierKeyword.PUBLIC_KEYWORD) ||
                        modKeyword.equals(Modifier.ModifierKeyword.PROTECTED_KEYWORD)) {

                    // if adding a visibility modifier, need to remove the existing one
                    if (modifiersToAdd.stream().anyMatch(m -> (m.equals(Modifier.ModifierKeyword.PRIVATE_KEYWORD.toString()) ||
                            m.equals(Modifier.ModifierKeyword.PUBLIC_KEYWORD.toString()) ||
                            m.equals(Modifier.ModifierKeyword.PROTECTED_KEYWORD.toString())))) {
                        modifiersList.remove(modifier, null);
                    }
                }
            }
        }

        // now add the ones we want
        for (String newModifier : modifiersToAdd) {
            // make a new marker
            Modifier marker = ast.newModifier(Modifier.ModifierKeyword.toKeyword(newModifier));
            modifiersList.insertLast(marker, null);
        }

        return rewrite;
    }
}
