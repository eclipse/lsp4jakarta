/*******************************************************************************
* Copyright (c) 2021 IBM Corporation and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     IBM Corporation, Ananya Rao 
*******************************************************************************/
package org.eclipse.lsp4jakarta.jdt.core.di;

import org.eclipse.lsp4jakarta.jdt.codeAction.proposal.quickfix.RemoveAnnotationConflictQuickFix;

/**
 * 
 * Quick fix for removing @Inject when it is used with more than one constructor.
 * 
 * @author Ananya Rao
 *
 */

public class ConflictInjectMultipleConstructorQuickFix extends RemoveAnnotationConflictQuickFix {
	public ConflictInjectMultipleConstructorQuickFix(){
		 super(false, "jakarta.inject.Inject");
	}

}
