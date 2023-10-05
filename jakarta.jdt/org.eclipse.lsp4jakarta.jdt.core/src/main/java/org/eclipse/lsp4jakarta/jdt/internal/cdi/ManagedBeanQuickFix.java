/*******************************************************************************
* Copyright (c) 2020, 2023 Red Hat Inc. and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
* which is available at https://www.apache.org/licenses/LICENSE-2.0.
*
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
*
* Contributors:
*     Hani Damlaj
*******************************************************************************/

package org.eclipse.lsp4jakarta.jdt.internal.cdi;

import static org.eclipse.lsp4jakarta.jdt.internal.cdi.Constants.SCOPES;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionKind;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4jakarta.commons.codeaction.CodeActionResolveData;
import org.eclipse.lsp4jakarta.commons.codeaction.ICodeActionId;
import org.eclipse.lsp4jakarta.commons.codeaction.JakartaCodeActionId;
import org.eclipse.lsp4jakarta.jdt.core.java.codeaction.ExtendedCodeAction;
import org.eclipse.lsp4jakarta.jdt.core.java.codeaction.InsertAnnotationMissingQuickFix;
import org.eclipse.lsp4jakarta.jdt.core.java.codeaction.JavaCodeActionContext;
import org.eclipse.lsp4jakarta.jdt.core.java.codeaction.JavaCodeActionResolveContext;
import org.eclipse.lsp4jakarta.jdt.core.java.corrections.proposal.ChangeCorrectionProposal;
import org.eclipse.lsp4jakarta.jdt.core.java.corrections.proposal.ReplaceAnnotationProposal;
import org.eclipse.lsp4jakarta.jdt.internal.Messages;

public class ManagedBeanQuickFix extends InsertAnnotationMissingQuickFix {
	private static final Logger LOGGER = Logger.getLogger(ManagedBeanQuickFix.class.getName());

	private static final String[] REMOVE_ANNOTATION_NAMES = new ArrayList<>(SCOPES).toArray(new String[SCOPES.size()]);

	public ManagedBeanQuickFix() {
		super("jakarta.enterprise.context.Dependent");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getParticipantId() {
		return ManagedBeanQuickFix.class.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ICodeActionId getCodeActionId() {
		return JakartaCodeActionId.CDIReplaceScopeAnnotations;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void insertAnnotations(Diagnostic diagnostic, JavaCodeActionContext context,
			List<CodeAction> codeActions) throws CoreException {
		String[] annotations = getAnnotations();
		for (String annotation : annotations) {
			insertAnnotation(diagnostic, context, codeActions, annotation);
		}
	}

	/**
	 * Adds a code action to insert the given annotation.
	 * 
	 * @param diagnostic  The diagnostic associated with this action.
	 * @param context     The context.
	 * @param codeActions The list of code actions.
	 * @param annotation  The annotation to insert.
	 * @throws CoreException
	 */
	protected void insertAnnotation(Diagnostic diagnostic, JavaCodeActionContext context, List<CodeAction> codeActions,
			String annotation) throws CoreException {
		String name = getLabel(annotation);
		ExtendedCodeAction codeAction = new ExtendedCodeAction(name);
		codeAction.setRelevance(0);
		codeAction.setDiagnostics(Collections.singletonList(diagnostic));
		codeAction.setKind(CodeActionKind.QuickFix);

		Map<String, Object> extendedData = new HashMap<>();
		extendedData.put(ANNOTATION_KEY, Arrays.asList(annotation));
		codeAction.setData(new CodeActionResolveData(context.getUri(), getParticipantId(),
				context.getParams().getRange(), extendedData, context.getParams().isResourceOperationSupported(),
				context.getParams().isCommandConfigurationUpdateSupported(), getCodeActionId()));

		codeActions.add(codeAction);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CodeAction resolveCodeAction(JavaCodeActionResolveContext context) {
		CodeAction toResolve = context.getUnresolved();

		// Diagnostic is reported on the variable declaration, however the
		// annotations that need to be replaced are on the type declaration (class
		// definition) containing the variable declaration. We retrieve the type
		// declaration container here.
		ASTNode node = context.getCoveredNode();
		IBinding parentType = getBinding(node);
		ASTNode parentNode = context.getASTRoot().findDeclaringNode(parentType);
		IBinding classBinding = getBinding(parentNode);

		CodeActionResolveData data = (CodeActionResolveData) toResolve.getData();
		List<String> resolveAnnotations = (List<String>) data.getExtendedDataEntry(ANNOTATION_KEY);
		String[] resolveAnnotationsArray = resolveAnnotations.toArray(String[]::new);

		// Only a single annotation insertion is expected.
		if (resolveAnnotationsArray.length == 1) {
			String annotation = resolveAnnotationsArray[0];
			String name = getLabel(annotation);
			ChangeCorrectionProposal proposal = new ReplaceAnnotationProposal(name, context.getCompilationUnit(),
					context.getASTRoot(), classBinding, 0, annotation, REMOVE_ANNOTATION_NAMES);
			try {
				toResolve.setEdit(context.convertToWorkspaceEdit(proposal));
			} catch (CoreException e) {
				LOGGER.log(Level.SEVERE, "Unable to create workspace edit for code action to insert missing annotation",
						e);
			}
		}

		return toResolve;
	}

	/**
	 * Returns the label associated with the input annotation.
	 *
	 * @param annotation The annotation name.
	 * 
	 * @return The label associated with the input annotation.
	 */
	private static String getLabel(String annotation) {
		String annotationName = annotation.substring(annotation.lastIndexOf('.') + 1, annotation.length());
		return Messages.getMessage("ReplaceCurrentScope", "@" + annotationName);
	}
}
