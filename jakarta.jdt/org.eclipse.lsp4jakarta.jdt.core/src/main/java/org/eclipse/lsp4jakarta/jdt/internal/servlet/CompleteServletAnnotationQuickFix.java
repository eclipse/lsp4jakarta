/*******************************************************************************
* Copyright (c) 2020, 2023 IBM Corporation and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0.
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     IBM Corporation - initial implementation
*******************************************************************************/
package org.eclipse.lsp4jakarta.jdt.internal.servlet;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4jakarta.commons.codeaction.ICodeActionId;
import org.eclipse.lsp4jakarta.commons.codeaction.JakartaCodeActionId;
import org.eclipse.lsp4jakarta.jdt.core.java.codeaction.InsertAnnotationMissingQuickFix;
import org.eclipse.lsp4jakarta.jdt.core.java.codeaction.JavaCodeActionContext;
import org.eclipse.lsp4jakarta.jdt.core.java.corrections.proposal.ChangeCorrectionProposal;
import org.eclipse.lsp4jakarta.jdt.core.java.corrections.proposal.ModifyAnnotationProposal;
import org.eclipse.lsp4jakarta.jdt.internal.Messages;

/**
 * Adds/Removes missing/duplicate attributes (value/urlPatters) to/from
 * a @WebServlet annotation.
 *
 * @author Kathryn Kodama
 */
public class CompleteServletAnnotationQuickFix extends InsertAnnotationMissingQuickFix {

    /**
     * Constructor.
     */
    public CompleteServletAnnotationQuickFix() {
        super("jakarta.servlet.annotation.WebServlet");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getParticipantId() {
        return CompleteServletAnnotationQuickFix.class.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ICodeActionId getCodeActionId() {
        return JakartaCodeActionId.ServletCompleteServletAnnotation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void insertAnnotations(Diagnostic diagnostic, JavaCodeActionContext context,
                                     List<CodeAction> codeActions) throws CoreException {
        String[] annotations = getAnnotations();
        for (String annotation : annotations) {
            insertAndReplaceAnnotation(diagnostic, context, codeActions, annotation);
        }
    }

    private void insertAndReplaceAnnotation(Diagnostic diagnostic, JavaCodeActionContext context,
                                            List<CodeAction> codeActions, String annotation) throws CoreException {
        ASTNode node = context.getCoveringNode();
        IBinding parentType = getBinding(node);
        // Insert the annotation and the proper import by using JDT Core Manipulation
        // API

        // if missing an attribute, do value insertion
        if (diagnostic.getCode().getLeft().equals(ErrorCode.WebServletAnnotationMissingAttributes.getCode())) {
            ArrayList<String> attributes = new ArrayList<>();
            attributes.add("value");
            attributes.add("urlPatterns");
            // Code Action 1: add value attribute to the WebServlet annotation
            // Code Action 2: add urlPatterns attribute to the WebServlet annotation
            for (int i = 0; i < attributes.size(); i++) {
                String attribute = attributes.get(i);

                ArrayList<String> attributesToAdd = new ArrayList<>();
                attributesToAdd.add(attribute);
                String name = getLabel(annotation, attribute, "Add");
                ChangeCorrectionProposal proposal = new ModifyAnnotationProposal(name, context.getCompilationUnit(), context.getASTRoot(), parentType, 0, annotation, attributesToAdd);
                // Convert the proposal to LSP4J CodeAction
                CodeAction codeAction = context.convertToCodeAction(proposal, diagnostic);
                codeAction.setTitle(name);
                if (codeAction != null) {
                    codeActions.add(codeAction);
                }
            }
        }
        // if duplicate attributes exist in annotations, remove attributes from
        // annotation
        if (diagnostic.getCode().getLeft().equals(ErrorCode.WebServletAnnotationAttributeConflict.getCode())) {
            ArrayList<String> attributes = new ArrayList<>();
            attributes.add("value");
            attributes.add("urlPatterns");
            // Code Action 1: remove value attribute from the WebServlet annotation
            // Code Action 2: remove urlPatterns attribute from the WebServlet annotation
            for (int i = 0; i < attributes.size(); i++) {
                String attribute = attributes.get(i);

                ArrayList<String> attributesToRemove = new ArrayList<>();
                attributesToRemove.add(attribute);
                String name = getLabel(annotation, attribute, "Remove");
                ChangeCorrectionProposal proposal = new ModifyAnnotationProposal(name, context.getCompilationUnit(), context.getASTRoot(), parentType, 0, annotation, new ArrayList<String>(), attributesToRemove);
                // Convert the proposal to LSP4J CodeAction
                CodeAction codeAction = context.convertToCodeAction(proposal, diagnostic);
                codeAction.setTitle(name);
                if (codeAction != null) {
                    codeActions.add(codeAction);
                }
            }
        }
    }

    private static String getLabel(String annotation, String attribute, String labelType) {
        String annotationName = annotation.substring(annotation.lastIndexOf('.') + 1, annotation.length());
        annotationName = "@" + annotationName;
        if (labelType.equals("Remove")) {
            return Messages.getMessage("RemoveTheAttriubuteFrom", attribute, annotationName);
        }
        return Messages.getMessage("AddTheAttributeTo", attribute, annotationName);
    }
}