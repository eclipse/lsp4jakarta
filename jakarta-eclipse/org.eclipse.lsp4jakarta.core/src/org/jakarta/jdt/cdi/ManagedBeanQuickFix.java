/*******************************************************************************
* Copyright (c) 2020 Red Hat Inc. and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
* which is available at https://www.apache.org/licenses/LICENSE-2.0.
*
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
*
* Contributors:
*     Hani Damlaj
*******************************************************************************/

package org.jakarta.jdt.cdi;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;
import org.jakarta.codeAction.JavaCodeActionContext;
import org.jakarta.codeAction.proposal.ChangeCorrectionProposal;
import org.jakarta.codeAction.proposal.ReplaceAnnotationProposal;
import org.jakarta.jdt.servlet.InsertAnnotationMissingQuickFix;

import static org.jakarta.jdt.cdi.ManagedBeanConstants.*;

public class ManagedBeanQuickFix extends InsertAnnotationMissingQuickFix {
    public ManagedBeanQuickFix() {
        super("jakarta.enterprise.context.Dependent");
    }

    private static final String[] REMOVE_ANNOTATION_NAMES = new ArrayList<>(SCOPES).toArray(new String[SCOPES.size()]);

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
        // Diagnostic is reported on the variable declaration, however the
        // annotations that need to be replaced are on the type declaration (class
        // definition) containing the variable declaration. We retrieve the type
        // declaration container here.
        ASTNode parentNode = context.getASTRoot().findDeclaringNode(parentType);
        IBinding classBinding = getBinding(parentNode);

        // Insert the annotation and the proper import by using JDT Core Manipulation
        // API
        String name = getLabel(annotation);
        ChangeCorrectionProposal proposal = new ReplaceAnnotationProposal(name, context.getCompilationUnit(),
                context.getASTRoot(), classBinding, 0, annotation, REMOVE_ANNOTATION_NAMES);
        // Convert the proposal to LSP4J CodeAction
        CodeAction codeAction = context.convertToCodeAction(proposal, diagnostic);
        if (codeAction != null) {
            codeActions.add(codeAction);
        }
    }

    private static String getLabel(String annotation) {
        StringBuilder name = new StringBuilder("Replace current scope with ");
        String annotationName = annotation.substring(annotation.lastIndexOf('.') + 1, annotation.length());
        name.append("@");
        name.append(annotationName);
        return name.toString();
    }
}
