/*******************************************************************************
* Copyright (c) 2021 IBM Corporation and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
* which is available at https://www.apache.org/licenses/LICENSE-2.0.
*
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
*
* Contributors:
*     IBM Corporation, Jianing Xu - initial API and implementation
*******************************************************************************/

package org.eclipse.lsp4jakarta.jdt.core.cdi;

import org.jakarta.codeAction.proposal.quickfix.RemoveAnnotationConflictQuickFix;

/**
 * 
 * Quick fix for removing @Produces/@Inject when they are used for the same field
 * or property
 * 
 * @author Jianing Xu
 *
 */
public class ConflictProducesInjectQuickFix extends RemoveAnnotationConflictQuickFix {
    
    public ConflictProducesInjectQuickFix() {
        super(false, "jakarta.enterprise.inject.Produces", "jakarta.inject.Inject");
    }
    
}
