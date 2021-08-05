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
package org.eclipse.lsp4jakarta.jdt.core.persistence;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;
import org.jakarta.codeAction.IJavaCodeActionParticipant;
import org.jakarta.codeAction.JavaCodeActionContext;
import org.jakarta.codeAction.proposal.ChangeCorrectionProposal;
import org.jakarta.codeAction.proposal.ModifyAnnotationProposal;
import org.jakarta.codeAction.proposal.quickfix.InsertAnnotationMissingQuickFix;


/**
 * QuickFix for fixing {@link PersistenceConstants#DIAGNOSTIC_CODE_MISSING_ATTRIBUTES} error
 * by providing several code actions to add the missing elements to the existing annotations:
 * 
 * {@link PersistenceConstants#DIAGNOSTIC_CODE_MISSING_ATTRIBUTES}
 * <ul>
 * <li> Add the `name` attribute to the `@MapKeyJoinColumn` annotation
 * <li> Add the `referencedColumnName` attribute to the `@MapKeyJoinColumn` annotation
 * </ul>
 *
 * @author Leslie Dawson (lamminade)
 *
 */
public class PersistenceAnnotationQuickFix extends InsertAnnotationMissingQuickFix {

    public PersistenceAnnotationQuickFix() {
        super("jakarta.persistence.MapKeyJoinColumn");
    }

    @Override
    protected void insertAnnotations(Diagnostic diagnostic, JavaCodeActionContext context, IBinding parentType,
            List<CodeAction> codeActions) throws CoreException {
        String[] annotations = getAnnotations();
        insertAndReplaceAnnotation(diagnostic, context, parentType, codeActions, annotations);
    }

    private static void insertAndReplaceAnnotation(Diagnostic diagnostic, JavaCodeActionContext context,
            IBinding parentType, List<CodeAction> codeActions, String... annotations) throws CoreException {
        ArrayList<String> attributes = new ArrayList<>();
        attributes.add("name"); 
        attributes.add("referencedColumnName");
        String name = "Add the missing attributes to the @MapKeyJoinColumn annotation";
        
        ChangeCorrectionProposal proposal = new ModifyAnnotationProposal(name, context.getCompilationUnit(),
                context.getASTRoot(), parentType, 0, attributes, annotations);

        // Convert the proposal to LSP4J CodeAction
        CodeAction codeAction = context.convertToCodeAction(proposal, diagnostic);
        codeAction.setTitle(name);
        if (codeAction != null) {
            codeActions.add(codeAction);
        }
    }
}