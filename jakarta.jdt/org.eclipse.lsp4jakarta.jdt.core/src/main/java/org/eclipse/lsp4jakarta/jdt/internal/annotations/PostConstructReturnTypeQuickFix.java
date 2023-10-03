/*******************************************************************************
 * Copyright (c) 2022, 2023 IBM Corporation and others.
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
package org.eclipse.lsp4jakarta.jdt.internal.annotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.internal.corext.dom.Bindings;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionKind;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4jakarta.commons.codeaction.CodeActionResolveData;
import org.eclipse.lsp4jakarta.commons.codeaction.ICodeActionId;
import org.eclipse.lsp4jakarta.commons.codeaction.JakartaCodeActionId;
import org.eclipse.lsp4jakarta.jdt.core.java.codeaction.ExtendedCodeAction;
import org.eclipse.lsp4jakarta.jdt.core.java.codeaction.IJavaCodeActionParticipant;
import org.eclipse.lsp4jakarta.jdt.core.java.codeaction.JavaCodeActionContext;
import org.eclipse.lsp4jakarta.jdt.core.java.codeaction.JavaCodeActionResolveContext;
import org.eclipse.lsp4jakarta.jdt.core.java.corrections.proposal.ChangeCorrectionProposal;
import org.eclipse.lsp4jakarta.jdt.core.java.corrections.proposal.ModifyReturnTypeProposal;
import org.eclipse.lsp4jakarta.jdt.internal.Messages;

/**
 * Quick fix for AnnotationDiagnosticsCollector that changes the return type of
 * a method to void.
 * Uses ModifyReturnTypeProposal.
 * 
 * @author Yijia Jing
 *
 */
public class PostConstructReturnTypeQuickFix implements IJavaCodeActionParticipant {
	private static final Logger LOGGER = Logger.getLogger(PostConstructReturnTypeQuickFix.class.getName());

	@Override
	public String getParticipantId() {
		return PostConstructReturnTypeQuickFix.class.getName();
	}

	@Override
	public List<? extends CodeAction> getCodeActions(JavaCodeActionContext context, Diagnostic diagnostic,
			IProgressMonitor monitor) throws CoreException {
		List<CodeAction> codeActions = new ArrayList<>();
		ExtendedCodeAction codeAction = new ExtendedCodeAction(getLabel());
		codeAction.setRelevance(0);
		codeAction.setKind(CodeActionKind.QuickFix);
		codeAction.setDiagnostics(Arrays.asList(diagnostic));

		ICodeActionId id = JakartaCodeActionId.ChangeReturnTypeToVoid;
		codeAction.setData(new CodeActionResolveData(context.getUri(), getParticipantId(),
				context.getParams().getRange(), null, context.getParams().isResourceOperationSupported(),
				context.getParams().isCommandConfigurationUpdateSupported(), id));
		codeActions.add(codeAction);
		return codeActions;
	}

	@Override
	public CodeAction resolveCodeAction(JavaCodeActionResolveContext context) {
		CodeAction toResolve = context.getUnresolved();
		ASTNode node = context.getCoveredNode();
		IBinding parentType = getBinding(node);

		ChangeCorrectionProposal proposal = new ModifyReturnTypeProposal(getLabel(), context.getCompilationUnit(),
				context.getASTRoot(), parentType, 0, node.getAST().newPrimitiveType(PrimitiveType.VOID));

		try {
			toResolve.setEdit(context.convertToWorkspaceEdit(proposal));
		} catch (CoreException e) {
			LOGGER.log(Level.SEVERE, "Unable to create workspace edit for code action for constructor actions", e);
		}

		return toResolve;
	}

	protected IBinding getBinding(ASTNode node) {
		if (node.getParent() instanceof MethodDeclaration) {
			return ((MethodDeclaration) node.getParent()).resolveBinding();
		}
		return Bindings.getBindingOfParentType(node);
	}

	private String getLabel() {
		return Messages.getMessage("ChangeReturnTypeToVoid");
	}
}
