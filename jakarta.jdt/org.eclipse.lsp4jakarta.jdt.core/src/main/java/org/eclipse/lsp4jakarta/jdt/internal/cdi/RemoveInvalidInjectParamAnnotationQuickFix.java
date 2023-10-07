/*******************************************************************************
* Copyright (c) 2023 IBM Corporation and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0.
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     IBM Corporation - initial implementation
*******************************************************************************/
package org.eclipse.lsp4jakarta.jdt.internal.cdi;

import org.eclipse.lsp4jakarta.commons.codeaction.ICodeActionId;
import org.eclipse.lsp4jakarta.commons.codeaction.JakartaCodeActionId;

/**
 * Removes the @Disposes, @Observes and @ObservesAsync annotations from
 * the declaring element.
 */
public class RemoveInvalidInjectParamAnnotationQuickFix extends RemoveMethodParamAnnotationQuickFix {

	/**
	 * Constructor.
	 */
	public RemoveInvalidInjectParamAnnotationQuickFix() {
		super(Constants.INVALID_INJECT_PARAMS.toArray((String[]::new)));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getParticipantId() {
		return RemoveInvalidInjectParamAnnotationQuickFix.class.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ICodeActionId getCodeActionId() {
		return JakartaCodeActionId.CDIRemoveInvalidInjectAnnotations;
	}
}
