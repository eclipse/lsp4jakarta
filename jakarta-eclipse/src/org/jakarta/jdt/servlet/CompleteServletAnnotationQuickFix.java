/*******************************************************************************
* Copyright (c) 2020, 2021 IBM Corporation and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
* which is available at https://www.apache.org/licenses/LICENSE-2.0.
*
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
*
* Contributors:
*     IBM Corporation - initial API and implementation
*******************************************************************************/

package org.jakarta.jdt.servlet;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;
import org.jakarta.codeAction.JavaCodeActionContext;
import org.jakarta.codeAction.proposal.ChangeCorrectionProposal;
import org.jakarta.codeAction.proposal.ModifyAnnotationProposal;

/**
 * QuickFix for fixing {@link ServletConstants#DIAGNOSTIC_CODE_MISSING_ATTRIBUTE} error
 * by providing several code actions:
 * 
 * <ul>
 * <li> Add the `value` attribute to the `@WebServlet` annotation
 * <li> Add the `urlPatterns` attribute to the `@WebServlet` annotatio
 * </ul>
 * 
 * @author Kathryn Kodama
 *
 */
public class CompleteServletAnnotationQuickFix extends InsertAnnotationMissingQuickFix {

    public CompleteServletAnnotationQuickFix() {
        super("jakarta.servlet.annotation.WebServlet");
    }

    @Override
    protected void insertAnnotations(Diagnostic diagnostic, JavaCodeActionContext context, IBinding parentType,
            List<CodeAction> codeActions) throws CoreException {
        String[] annotations = getAnnotations();
        for (String annotation : annotations) {
            insertAndReplaceAnnotation(diagnostic, context, parentType, codeActions, annotation);
        }
    }

    private static void insertAndReplaceAnnotation(Diagnostic diagnostic, JavaCodeActionContext context,
            IBinding parentType, List<CodeAction> codeActions, String annotation) throws CoreException {

        // Insert the annotation and the proper import by using JDT Core Manipulation
        // API

        // Code Action 1: Adding value attribute to the WebServlet annotation
        ArrayList<String> attributesToAdd = new ArrayList<>();
        attributesToAdd.add("value");
        String name = getLabel(annotation, "value");
        ChangeCorrectionProposal proposal = new ModifyAnnotationProposal(name, context.getCompilationUnit(),
                context.getASTRoot(), parentType, 0, annotation, attributesToAdd);
        // Convert the proposal to LSP4J CodeAction
        CodeAction codeAction = context.convertToCodeAction(proposal, diagnostic);
        codeAction.setTitle(name);
        if (codeAction != null) {
            codeActions.add(codeAction);
        }

        // TODO: Code Action 2: Adding urlPatterns attribute to the WebServlet
        // annotation
    }

    private static String getLabel(String annotation, String attribute) {
        StringBuilder name = new StringBuilder("Add the `" + attribute + "` attribute to ");
        String annotationName = annotation.substring(annotation.lastIndexOf('.') + 1, annotation.length());
        name.append("@");
        name.append(annotationName);
        return name.toString();
    }
}