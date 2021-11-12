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
*     IBM Corporation, Himanshu Chotwani - initial API and implementation
*******************************************************************************/

package org.eclipse.lsp4jakarta.jdt.core.di;

import org.eclipse.lsp4jakarta.jdt.codeAction.proposal.quickfix.RemoveModifierConflictQuickFix;


/**
 * 
 * Quick fix for removing static modifier when it is used for a method with @Inject
 * 
 * @author Himanshu Chotwani
 *
 */
public class RemoveStaticModifierQuickFix extends RemoveModifierConflictQuickFix {
    
    public RemoveStaticModifierQuickFix() {
        super(false, "static");
    }
}
