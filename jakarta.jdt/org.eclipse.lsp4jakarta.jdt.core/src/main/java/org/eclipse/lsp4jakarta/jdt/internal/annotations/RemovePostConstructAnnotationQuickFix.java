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

package org.eclipse.lsp4jakarta.jdt.internal.annotations;

import org.eclipse.lsp4jakarta.commons.codeaction.JakartaCodeActionId;
import org.eclipse.lsp4jakarta.jdt.core.java.codeaction.RemoveAnnotationConflictQuickFix;

/**
 * Quickfix for removing @PostConstruct
 * 
 * @author Zijian Pei
 *
 */
public class RemovePostConstructAnnotationQuickFix extends RemoveAnnotationConflictQuickFix {

	public RemovePostConstructAnnotationQuickFix() {
		super(false, "jakarta.annotation.PostConstruct");
	}

	@Override
	public String getParticipantId() {
		return RemovePostConstructAnnotationQuickFix.class.getName();
	}

	@Override
	protected JakartaCodeActionId getCodeActionId() {
		return JakartaCodeActionId.RemoveAnnotationPostConstruct;
	}

}
