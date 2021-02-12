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

package org.jakarta.jdt.persistence;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;
import org.jakarta.codeAction.JavaCodeActionContext;
import org.jakarta.codeAction.proposal.ChangeCorrectionProposal;
import org.jakarta.codeAction.proposal.DeleteAnnotationProposal;

/**
 * 
 * Quick fix for removing @MapKey when @MapKeyClass is used for the same field or property
 * 
 * @author Jianing Xu
 *
 */
public class DeleteConflictMapKeyQuickFix extends RemoveAnnotationConflictQuickFix {
	
	public DeleteConflictMapKeyQuickFix() {
		super("jakarta.persistence.annotation.MapKey");
	}
	
	@Override
	protected void removeAnnotations(Diagnostic diagnostic, JavaCodeActionContext context, IBinding parentType,
            List<CodeAction> codeActions) throws CoreException {
		String[] annotations = getAnnotations();
		if(diagnostic.getCode().getLeft().equals(PersistenceConstants.DIAGNOSTIC_CODE_INVALID_ANNOTATION)) {
			String name = getLabel(annotations);
	        ChangeCorrectionProposal proposal = new DeleteAnnotationProposal(name, context.getCompilationUnit(),
	                context.getASTRoot(), parentType, 0, context.getCoveredNode().getParent(), annotations);
	        // Convert the proposal to LSP4J CodeAction
	        CodeAction codeAction = context.convertToCodeAction(proposal, diagnostic);
	        codeAction.setTitle(name);
	        if (codeAction != null) {
	            codeActions.add(codeAction);
	        }
		}
	}
	
	private static String getLabel(String[] annotations) {
        StringBuilder name = new StringBuilder("Remove ");
        for (int i = 0; i < annotations.length; i++) {
            String annotation = annotations[i];
            String annotationName = annotation.substring(annotation.lastIndexOf('.') + 1, annotation.length());
            if (i > 0) {
                name.append(", ");
            }
            name.append("@");
            name.append(annotationName);
        }
        return name.toString();
    }
}
