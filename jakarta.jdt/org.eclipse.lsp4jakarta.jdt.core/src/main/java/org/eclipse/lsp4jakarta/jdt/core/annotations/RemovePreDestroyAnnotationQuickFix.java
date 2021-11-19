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

package org.eclipse.lsp4jakarta.jdt.core.annotations;

/**
 * Quickfix for annotation PreDestory 
 * 1. Removing static from a static method
 * 2. Removing all parameters from method
 * 
 * @author Zijian Pei
 *
 */

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.internal.corext.dom.Bindings;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4jakarta.jdt.codeAction.IJavaCodeActionParticipant;
import org.eclipse.lsp4jakarta.jdt.codeAction.JavaCodeActionContext;
import org.eclipse.lsp4jakarta.jdt.codeAction.proposal.ModifyModifiersProposal;
import org.eclipse.lsp4jakarta.jdt.codeAction.proposal.quickfix.RemoveAnnotationConflictQuickFix;


public class RemovePreDestroyAnnotationQuickFix extends RemoveAnnotationConflictQuickFix{
	public RemovePreDestroyAnnotationQuickFix() {
        super(false, "jakarta.annotation.PreDestroy");
    }

}
