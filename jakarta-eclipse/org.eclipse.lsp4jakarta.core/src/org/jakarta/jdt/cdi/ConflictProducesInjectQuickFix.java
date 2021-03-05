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

package org.jakarta.jdt.cdi;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;
import org.jakarta.codeAction.JavaCodeActionContext;
import org.jakarta.codeAction.proposal.ChangeCorrectionProposal;
import org.jakarta.codeAction.proposal.DeleteAnnotationProposal;
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
