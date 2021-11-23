/*******************************************************************************
* Copyright (c) 2021 IBM Corporation and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
<<<<<<< HEAD
* http://www.eclipse.org/legal/epl-2.0
=======
* http://www.eclipse.org/legal/epl-2.0.
>>>>>>> 571b6ec (Change name and path for PreDestroyAnnotationQuickfix2)
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
<<<<<<< HEAD
*     IBM Corporation, Himanshu Chotwani - initial API and implementation
=======
*     IBM Corporation - initial API and implementation
>>>>>>> 571b6ec (Change name and path for PreDestroyAnnotationQuickfix2)
*******************************************************************************/

package org.eclipse.lsp4jakarta.jdt.codeAction.proposal.quickfix;

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
