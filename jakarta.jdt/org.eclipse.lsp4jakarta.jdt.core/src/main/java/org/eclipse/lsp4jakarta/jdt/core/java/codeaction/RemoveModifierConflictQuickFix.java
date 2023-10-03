/*******************************************************************************
* Copyright (c) 2021, 2023 IBM Corporation and others.
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

package org.eclipse.lsp4jakarta.jdt.core.java.codeaction;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionKind;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4jakarta.commons.codeaction.CodeActionResolveData;
import org.eclipse.lsp4jakarta.commons.codeaction.ICodeActionId;
import org.eclipse.lsp4jakarta.jdt.core.java.corrections.proposal.ModifyModifiersProposal;

/**
 * Removes modifiers.
 */
public abstract class RemoveModifierConflictQuickFix implements IJavaCodeActionParticipant {

	private static final Logger LOGGER = Logger.getLogger(RemoveAnnotationConflictQuickFix.class.getName());

	public static final String MODIFIERS_KEY = "modifiers";

	public static final String DIAGNOSTIC_DATA = "diagnostic.data";

	private static final String CODE_ACTION_LABEL = "Remove the ''{0}'' modifier";

	/**
	 * Array of modifiers to remove.
	 */
	private final String[] modifiers;

	/**
	 * Single action creation indicator. If true, a single code action is created to
	 * remove the specified set of modifiers; otherwise, a code action is created
	 * per modifier to delete.
	 */
	protected final boolean generateOnlyOneCodeAction;

	/**
	 * Constructor.
	 * 
	 * @param modifiers The modifiers to remove.
	 */
	public RemoveModifierConflictQuickFix(String... modifiers) {
		this(false, modifiers);
	}

	/**
	 * Constructor.
	 *
	 * @param generateOnlyOneCodeAction The single action creation indicator. If
	 *                                  true, a single code action is created to
	 *                                  remove the specified set of modifiers;
	 *                                  otherwise, a code action is created per
	 *                                  modifier to delete.
	 * @param modifiers                 list of modifiers to remove.
	 */
	public RemoveModifierConflictQuickFix(boolean generateOnlyOneCodeAction, String... modifiers) {
		this.generateOnlyOneCodeAction = generateOnlyOneCodeAction;
		this.modifiers = modifiers;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<? extends CodeAction> getCodeActions(JavaCodeActionContext context, Diagnostic diagnostic,
			IProgressMonitor monitor) throws CoreException {
		List<CodeAction> codeActions = new ArrayList<>();
		ASTNode node = context.getCoveredNode();
		IBinding parentType = getBinding(node);
		if (parentType != null) {
			createCodeActions(diagnostic, context, parentType, codeActions);
		}

		return codeActions;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CodeAction resolveCodeAction(JavaCodeActionResolveContext context) {
		CodeAction toResolve = context.getUnresolved();
		ASTNode coveredParentNode = context.getCoveredNode().getParent();
		IBinding parentType = getBinding(context.getCoveredNode());
		CodeActionResolveData data = (CodeActionResolveData) toResolve.getData();
		List<String> modifiersToDeleteList = (List<String>) data.getExtendedDataEntry(MODIFIERS_KEY);
		String[] modifiersToDelete = modifiersToDeleteList.toArray(String[]::new);
		String label = getLabel(modifiersToDelete);
		ModifyModifiersProposal proposal = new ModifyModifiersProposal(label, context.getCompilationUnit(),
				context.getASTRoot(), parentType, 0, coveredParentNode, new ArrayList<>(), Arrays.asList(modifiers));

		try {
			toResolve.setEdit(context.convertToWorkspaceEdit(proposal));
		} catch (CoreException e) {
			LOGGER.log(Level.SEVERE, "Unable to resolve code action to remove annotation", e);
		}

		return toResolve;
	}

	/**
	 * Creates one or more code actions to remove one or more modifiers.
	 * 
	 * @param diagnostic  The code diagnostic associated with the action to be
	 *                    created.
	 * @param context     The context.
	 * @param parentType  The parent type.
	 * @param codeActions The list of code action to update.
	 * 
	 * @throws CoreException
	 */
	protected void createCodeActions(Diagnostic diagnostic, JavaCodeActionContext context, IBinding parentType,
			List<CodeAction> codeActions) throws CoreException {
		if (generateOnlyOneCodeAction) {
			createCodeAction(diagnostic, context, parentType, codeActions, modifiers);
		} else {
			for (String modifier : modifiers) {
				createCodeAction(diagnostic, context, parentType, codeActions, modifier);
			}
		}
	}

	/**
	 * Creates a code action to remove the input modifiers.
	 * 
	 * @param diagnostic  The code diagnostic associated with the action to be
	 *                    created.
	 * @param context     The context.
	 * @param parentType  The parent type.
	 * @param codeActions The list of code actions.
	 * @param modifiers   The modifiers to remove.
	 * 
	 * 
	 * @throws CoreException
	 */
	protected void createCodeAction(Diagnostic diagnostic, JavaCodeActionContext context, IBinding parentType,
			List<CodeAction> codeActions, String... modifiers) throws CoreException {
		String label = getLabel(modifiers);
		ExtendedCodeAction codeAction = new ExtendedCodeAction(label);
		codeAction.setRelevance(0);
		codeAction.setKind(CodeActionKind.QuickFix);
		codeAction.setDiagnostics(Arrays.asList(diagnostic));
		Map<String, Object> extendedData = new HashMap<String, Object>();
		extendedData.put(MODIFIERS_KEY, Arrays.asList(modifiers));
		codeAction.setData(new CodeActionResolveData(context.getUri(), getParticipantId(),
				context.getParams().getRange(), extendedData, context.getParams().isResourceOperationSupported(),
				context.getParams().isCommandConfigurationUpdateSupported(), getCodeActionId()));

		codeActions.add(codeAction);
	}

	/**
	 * Returns the label associated with the input modifier.
	 *
	 * @param modifier The modifier to remove.
	 * @return The label associated with the input modifier.
	 */
	private String getLabel(String... modifier) {
		return MessageFormat.format(CODE_ACTION_LABEL, modifier[0]);
	}

	@SuppressWarnings("restriction")
	protected IBinding getBinding(ASTNode node) {
		if (node.getParent() instanceof VariableDeclarationFragment) {
			return ((VariableDeclarationFragment) node.getParent()).resolveBinding();
		} else if (node.getParent() instanceof MethodDeclaration) {
			return ((MethodDeclaration) node.getParent()).resolveBinding();
		}
		return org.eclipse.jdt.internal.corext.dom.Bindings.getBindingOfParentType(node);
	}

	/**
	 * Returns the id for this code action.
	 *
	 * @return the id for this code action
	 */
	protected abstract ICodeActionId getCodeActionId();
}
