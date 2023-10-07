/*******************************************************************************
 * Copyright (c) 2022 IBM Corporation and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Yijia Jing
 *******************************************************************************/
package org.eclipse.lsp4jakarta.jdt.core.java.corrections.proposal;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.internal.core.manipulation.dom.ASTResolving;
import org.eclipse.lsp4j.CodeActionKind;
import org.eclipse.lsp4jakarta.jdt.internal.annotations.ModifyConstructReturnTypeQuickFix;

/**
 * Code action proposal for changing the return type of a method.
 * 
 * @author Yijia Jing
 * @see CodeActionHandler
 * @see ModifyConstructReturnTypeQuickFix
 *
 */
public class ModifyReturnTypeProposal extends ASTRewriteCorrectionProposal {

	private final CompilationUnit invocationNode;
	private final IBinding binding;
	private final Type newReturnType;

	/**
	 * Constructor for ModifyReturnTypeProposal that accepts the new return type of
	 * a method.
	 * 
	 * @param newReturnType the new return type to change to
	 */
	public ModifyReturnTypeProposal(String label, ICompilationUnit targetCU, CompilationUnit invocationNode,
			IBinding binding, int relevance, Type newReturnType) {
		super(label, CodeActionKind.QuickFix, targetCU, null, relevance);
		this.invocationNode = invocationNode;
		this.binding = binding;
		this.newReturnType = newReturnType;
	}

	@SuppressWarnings("restriction")
	@Override
	protected ASTRewrite getRewrite() {
		ASTNode declNode = null;
		ASTNode boundNode = invocationNode.findDeclaringNode(binding);
		CompilationUnit newRoot = invocationNode;

		if (boundNode != null) {
			declNode = boundNode;
		} else {
			newRoot = ASTResolving.createQuickFixAST(getCompilationUnit(), null);
			declNode = newRoot.findDeclaringNode(binding.getKey());
		}

		if (declNode.getNodeType() == ASTNode.METHOD_DECLARATION) {
			AST ast = declNode.getAST();
			ASTRewrite rewrite = ASTRewrite.create(ast);
			rewrite.set(declNode, MethodDeclaration.RETURN_TYPE2_PROPERTY, newReturnType, null);
			return rewrite;
		}
		return null;
	}
}
