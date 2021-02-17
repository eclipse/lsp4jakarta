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
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite.ImportRewriteContext;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.internal.core.manipulation.dom.ASTResolving;
import org.eclipse.jdt.internal.corext.codemanipulation.ContextSensitiveImportRewriteContext;
import org.eclipse.lsp4j.CodeActionKind;

public class ChangeVisibilityProposal extends ChangeCorrectionProposal {

    private final CompilationUnit fInvocationNode;
    private final IBinding fBinding;
    
    public ChangeVisibilityProposal(String label, ICompilationUnit targetCU, CompilationUnit invocationNode,
            IBinding binding, int relevance) {
        super(label, CodeActionKind.QuickFix, targetCU, null, relevance);
        fInvocationNode = invocationNode;
        fBinding = binding;
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
        
        ImportRewrite imports = createImportRewrite(newRoot);
        
        AST ast = declNode.getAST();
        ASTRewrite rewrite = ASTRewrite.create(ast);
        
        ImportRewriteContext importRewriteContext = new ContextSensitiveImportRewriteContext(declNode, imports);
        
        Modifier marker = newASTModifier(ast, "public");
        
        ListRewrite modifiersList = rewrite.getListRewrite(declNode, MethodDeclaration.MODIFIERS2_PROPERTY);
        List<ASTNode> modifiers = (List<ASTNode>) declNode.getStructuralProperty(MethodDeclaration.MODIFIERS2_PROPERTY);
        
        for (ASTNode modifier : modifiers) {
            if (modifier instanceof Modifier) {
                // TODO: Check it's not final or static modifier
                modifiersList.replace(modifier, marker, null);
            }
        }
        
        
        return rewrite;
    }
    
    public Modifier newASTModifier(AST ast, String modifier) {
        Modifier publicModifier = ast.newModifier(Modifier.ModifierKeyword.toKeyword(modifier));
        return publicModifier;
    }

}
