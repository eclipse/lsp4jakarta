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
package org.jakarta.jdt.beanvalidation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.internal.corext.dom.Bindings;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;
import org.jakarta.codeAction.IJavaCodeActionParticipant;
import org.jakarta.codeAction.JavaCodeActionContext;
import org.jakarta.codeAction.proposal.ChangeCorrectionProposal;
import org.jakarta.codeAction.proposal.DeleteAnnotationProposal;
import org.jakarta.codeAction.proposal.ModifyModifiersProposal;

/**
 * Quickfix for fixing {@link BeanValidationConstants#DIAGNOSTIC_CODE_Static} error by either action 
 * 1. Removing constraint annotation on static field or method
 * 2. Removing static modifier from field or method
 * 
 * @author Leslie Dawson (lamminade)
 *
 */
public class BeanValidationQuickFix implements IJavaCodeActionParticipant {

    @Override
    public List<? extends CodeAction> getCodeActions(JavaCodeActionContext context, Diagnostic diagnostic,
            IProgressMonitor monitor) throws CoreException {
        ASTNode node = context.getCoveredNode();
        IBinding parentType = getBinding(node);

        List<CodeAction> codeActions = new ArrayList<>();
        codeActions.add(removeConstraintAnnotations(diagnostic, context, parentType));

        if (diagnostic.getCode().getLeft().equals(BeanValidationConstants.DIAGNOSTIC_CODE_STATIC)) {
            codeActions.add(removeStaticModifier(diagnostic, context, parentType));
        }

        return codeActions;
    }

    private CodeAction removeConstraintAnnotations(Diagnostic diagnostic, JavaCodeActionContext context, IBinding parentType) throws CoreException {
        String annotationName = diagnostic.getData().toString().replace("\"", "");
        String name = "Remove constraint annotation " + annotationName + " from element";
        ChangeCorrectionProposal proposal = new DeleteAnnotationProposal(name, context.getCompilationUnit(),
                context.getASTRoot(), parentType, 0, context.getCoveredNode().getParent(), annotationName);
        CodeAction codeAction = context.convertToCodeAction(proposal, diagnostic);
        if (codeAction != null) {
            return codeAction;
        }
        return null;
    }

    private CodeAction removeStaticModifier(Diagnostic diagnostic, JavaCodeActionContext context, IBinding parentType) throws CoreException {
        String name = "Remove static modifier from element";
        ModifyModifiersProposal proposal = new ModifyModifiersProposal(name, context.getCompilationUnit(), 
                context.getASTRoot(), parentType, 0, context.getCoveredNode().getParent(), new ArrayList<>(), Arrays.asList("static"));
        CodeAction codeAction = context.convertToCodeAction(proposal, diagnostic);

        if (codeAction != null) {
            return codeAction;
        }
        return null;
    }

    protected IBinding getBinding(ASTNode node) {
        if (node.getParent() instanceof VariableDeclarationFragment) {
            return ((VariableDeclarationFragment) node.getParent()).resolveBinding();
        }
        return Bindings.getBindingOfParentType(node);
    }
}