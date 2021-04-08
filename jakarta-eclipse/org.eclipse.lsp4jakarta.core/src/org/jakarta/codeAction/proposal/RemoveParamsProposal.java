/*******************************************************************************
 * Copyright (c) 2021 IBM Corporation, Bera Sogut and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation, Bera Sogut - initial API and implementation
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
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.internal.core.manipulation.dom.ASTResolving;
import org.eclipse.lsp4j.CodeActionKind;

/**
 * Code action proposal for removing the entity parameters of a method except
 * one. Used by JAX-RS ResourceMethodMultipleEntityParamsQuickFix.
 *
 * @author Bera Sogut
 * @see CodeActionHandler
 * @see ResourceMethodMultipleEntityParamsQuickFix
 *
 */
public class RemoveParamsProposal extends ChangeCorrectionProposal {

    private final CompilationUnit invocationNode;
    private final IBinding binding;

    // parameters to remove
    private final List<SingleVariableDeclaration> params;

    // parameter to keep
    private final SingleVariableDeclaration paramToKeep;

    /**
     * Constructor for RemoveParamsProposal that accepts parameters to remove and a
     * parameter to keep.
     *
     * @param params      the parameters of the function to remove
     * @param paramToKeep the parameter of the function to keep
     */
    public RemoveParamsProposal(String label, ICompilationUnit targetCU, CompilationUnit invocationNode,
            IBinding binding, int relevance, List<SingleVariableDeclaration> params,
            SingleVariableDeclaration paramToKeep) {
        super(label, CodeActionKind.QuickFix, targetCU, null, relevance);
        this.invocationNode = invocationNode;
        this.binding = binding;
        this.params = params;
        this.paramToKeep = paramToKeep;
    }

    @Override
    protected ASTRewrite getRewrite() throws CoreException {
        ASTNode declNode = null;
        ASTNode boundNode = invocationNode.findDeclaringNode(binding);

        if (boundNode != null) {
            declNode = boundNode;
        } else {
            CompilationUnit newRoot = ASTResolving.createQuickFixAST(getCompilationUnit(), null);
            declNode = newRoot.findDeclaringNode(binding.getKey());
        }

        AST ast = declNode.getAST();
        ASTRewrite rewrite = ASTRewrite.create(ast);

        ListRewrite parametersList = rewrite.getListRewrite(declNode, MethodDeclaration.PARAMETERS_PROPERTY);

        // remove the parameters except the parameter to keep
        for (SingleVariableDeclaration param : params) {
            if (!param.equals(paramToKeep)) {
                parametersList.remove(param, null);
            }
        }

        return rewrite;
    }
}
