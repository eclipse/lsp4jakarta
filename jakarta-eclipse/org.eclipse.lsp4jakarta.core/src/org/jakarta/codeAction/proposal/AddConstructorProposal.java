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
package org.jakarta.codeAction.proposal;


import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.internal.core.manipulation.dom.ASTResolving;
import org.eclipse.lsp4j.CodeActionKind;

/**
 * Code action proposal for adding a no-arg constructor to a class
 * 
 * @author  Leslie Dawson
 * @see     PersistenceEntityQuickFix
 * 
 */
public class AddConstructorProposal extends ChangeCorrectionProposal {

    private final CompilationUnit invocationNode;
    private final IBinding binding;
    private final String visibility;
    
    /**
     * Constructor for AddMethodProposal
     * 
     */
    public AddConstructorProposal(String label, ICompilationUnit targetCU, CompilationUnit invocationNode,
            IBinding binding, int relevance) {
        super(label, CodeActionKind.QuickFix, targetCU, null, relevance);
        this.invocationNode = invocationNode;
        this.binding = binding;
        this.visibility = "protected";
    }
    
    /**
     * Constructor for AddMethodProposal
     * 
     * @param visibility    a valid visibility modifier for the constructor, defaults to protected
     */
    public AddConstructorProposal(String label, ICompilationUnit targetCU, CompilationUnit invocationNode,
            IBinding binding, int relevance, String visibility) {
        super(label, CodeActionKind.QuickFix, targetCU, null, relevance);
        this.invocationNode = invocationNode;
        this.binding = binding;
        this.visibility = visibility;
    }

    @SuppressWarnings("unchecked")
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
        
        AST ast = declNode.getAST();
        ASTRewrite rewrite = ASTRewrite.create(ast);
        ListRewrite list = rewrite.getListRewrite(declNode, TypeDeclaration.BODY_DECLARATIONS_PROPERTY);

        // create method
        MethodDeclaration md = ast.newMethodDeclaration();
        SimpleName name = ast.newSimpleName(declNode.getStructuralProperty(TypeDeclaration.NAME_PROPERTY).toString());
        Block methodBody = ast.newBlock();
        List<Modifier> modifiers = md.modifiers();
        
        modifiers.add(ast.newModifier(Modifier.ModifierKeyword.toKeyword(visibility)));
        md.setName(name);
        md.setConstructor(true);
        md.setBody(methodBody);
    
        // insert method
        list.insertFirst(md, null);
        return rewrite;
    }

}
