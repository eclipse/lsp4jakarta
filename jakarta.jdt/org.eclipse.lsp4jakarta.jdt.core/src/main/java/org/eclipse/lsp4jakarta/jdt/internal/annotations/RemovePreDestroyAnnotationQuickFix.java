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
 * Quickfix for removing @PreDestory
 * 
 * @author Zijian Pei
 *
 */
public class RemovePreDestroyAnnotationQuickFix extends RemoveAnnotationConflictQuickFix {

	public RemovePreDestroyAnnotationQuickFix() {
		super(false, "jakarta.annotation.PreDestroy");
	}

	@Override
	public String getParticipantId() {
		return RemovePreDestroyAnnotationQuickFix.class.getName();
	}

	@Override
	protected JakartaCodeActionId getCodeActionId() {
		return JakartaCodeActionId.RemoveAnnotationPreDestroy;
	}
}
