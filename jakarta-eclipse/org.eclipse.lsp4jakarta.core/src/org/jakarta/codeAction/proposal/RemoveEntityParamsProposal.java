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
public class RemoveEntityParamsProposal extends ChangeCorrectionProposal {

    private final CompilationUnit invocationNode;
    private final IBinding binding;

    // the entity parameters of the function
    private final List<SingleVariableDeclaration> entityParams;

    // the entity parameter to keep
    private final SingleVariableDeclaration entityParamToKeep;

    /**
     * Constructor for RemoveEntityParamsProposal that accepts the entity parameters
     * of the function and the entity parameter to keep.
     *
     * @param entityParams      the entity parameters of the function
     * @param entityParamToKeep the entity parameter to keep
     */
    public RemoveEntityParamsProposal(String label, ICompilationUnit targetCU, CompilationUnit invocationNode,
            IBinding binding, int relevance, List<SingleVariableDeclaration> entityParams,
            SingleVariableDeclaration entityParamToKeep) {
        super(label, CodeActionKind.QuickFix, targetCU, null, relevance);
        this.invocationNode = invocationNode;
        this.binding = binding;
        this.entityParams = entityParams;
        this.entityParamToKeep = entityParamToKeep;
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

        // remove all entity parameters except the entity parameter to keep
        for (SingleVariableDeclaration entityParam : entityParams) {
            if (!entityParam.equals(entityParamToKeep)) {
                parametersList.remove(entityParam, null);
            }
        }

        return rewrite;
    }
}
