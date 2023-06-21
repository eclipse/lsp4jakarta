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
*     Lidia Ataupillco Ramos
*******************************************************************************/

package org.eclipse.lsp4jakarta.jdt.codeAction.proposal.quickfix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.internal.corext.dom.Bindings;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4jakarta.jdt.codeAction.IJavaCodeActionParticipant;
import org.eclipse.lsp4jakarta.jdt.codeAction.JavaCodeActionContext;
import org.eclipse.lsp4jakarta.jdt.codeAction.proposal.ChangeCorrectionProposal;
import org.eclipse.lsp4jakarta.jdt.codeAction.proposal.ModifyAnnotationProposal;
import org.eclipse.lsp4jakarta.jdt.core.Messages;

/**
 * Quickfix for adding new annotations with or without attributes
 * 
 * @author Zijian Pei
 * @author Lidia Ataupillco Ramos
 *
 */
public class InsertAnnotationQuickFix implements IJavaCodeActionParticipant {

    private final String[] attributes;

    private final String annotation;

    protected final boolean generateOnlyOneCodeAction;

    public InsertAnnotationQuickFix(String annotation, String... attributes) {
        this(annotation, false, attributes);
    }

    /**
     * Constructor for add missing attributes quick fix.
     *
     * @param generateOnlyOneCodeAction true if the participant must generate a
     *                                  CodeAction which add the list of attributes
     *                                  and false otherwise.
     * @param attributes                list of attributes to add.
     */
    public InsertAnnotationQuickFix(String annotation, boolean generateOnlyOneCodeAction,
            String... attributes) {
        this.annotation = annotation;
        this.generateOnlyOneCodeAction = generateOnlyOneCodeAction;
        this.attributes = attributes;
    }

    @Override
    public List<? extends CodeAction> getCodeActions(JavaCodeActionContext context, Diagnostic diagnostic,
            IProgressMonitor monitor) throws CoreException {
        ASTNode node = context.getCoveredNode();
        IBinding parentType = getBinding(node);

        List<CodeAction> codeActions = new ArrayList<>();
        addAttributes(diagnostic, context, parentType, codeActions, annotation);

        return codeActions;
    }

    protected void addAttributes(Diagnostic diagnostic, JavaCodeActionContext context, IBinding parentType,
            List<CodeAction> codeActions, String annotation) throws CoreException {
        if (generateOnlyOneCodeAction) {
            addAttribute(diagnostic, context, parentType, codeActions, annotation, attributes);
        } else {
            for (String attribute : attributes) {
                addAttribute(diagnostic, context, parentType, codeActions, annotation, attribute);
            }
        }
    }

    /**
     * use setData() API with diagnostic to pass in ElementType in diagnostic
     * collector class.
     *
     */
    private void addAttribute(Diagnostic diagnostic, JavaCodeActionContext context, IBinding parentType,
            List<CodeAction> codeActions, String annotation, String... attributes) throws CoreException {
        // Remove the modifier and the proper import by using JDT Core Manipulation
        // API
        ASTNode coveredNode = context.getCoveredNode().getParent();
        String name = getLabel(annotation, attributes);
        ChangeCorrectionProposal proposal = new ModifyAnnotationProposal(name, context.getCompilationUnit(),
                context.getASTRoot(), parentType, 0, annotation, Arrays.asList(attributes));
        CodeAction codeAction = context.convertToCodeAction(proposal, diagnostic);

        if (codeAction != null) {
            codeActions.add(codeAction);
        }
    }

    protected IBinding getBinding(ASTNode node) {
        // handle annotation insertions for a single variable declaration
        if (node.getParent() instanceof SingleVariableDeclaration) {
            return ((SingleVariableDeclaration) node.getParent()).resolveBinding();
        }
        
        if (node.getParent() instanceof VariableDeclarationFragment) {
            return ((VariableDeclarationFragment) node.getParent()).resolveBinding();
        }
        return Bindings.getBindingOfParentType(node);
    }

    protected String getLabel(String annotation, String... attributes) {
        return Messages.getMessage("InsertItem", "@" + annotation); // uses Java syntax
    }

}
