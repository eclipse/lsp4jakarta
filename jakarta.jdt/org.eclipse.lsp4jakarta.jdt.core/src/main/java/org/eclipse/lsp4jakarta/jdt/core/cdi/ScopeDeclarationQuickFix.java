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
package org.eclipse.lsp4jakarta.jdt.core.cdi;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4jakarta.jdt.codeAction.JavaCodeActionContext;
import org.eclipse.lsp4jakarta.jdt.codeAction.proposal.quickfix.RemoveAnnotationConflictQuickFix;

import com.google.gson.JsonArray;

public class ScopeDeclarationQuickFix extends RemoveAnnotationConflictQuickFix {
    public ScopeDeclarationQuickFix() {
        // annotation list to be derived from the diagnostic passed to
        // `getCodeActions()`
        super();
    }

    @Override
    public List<? extends CodeAction> getCodeActions(JavaCodeActionContext context, Diagnostic diagnostic,
            IProgressMonitor monitor) throws CoreException {
        List<CodeAction> codeActions = new ArrayList<>();
        ASTNode node = context.getCoveredNode();
        IBinding parentType = getBinding(node);

        JsonArray diagnosticData = (JsonArray) diagnostic.getData();

        List<String> annotations = IntStream.range(0, diagnosticData.size())
                .mapToObj(idx -> diagnosticData.get(idx).getAsString()).collect(Collectors.toList());

        annotations.remove(ManagedBeanConstants.PRODUCES);

        if (parentType != null) {
            /**
             * for each annotation, choose the current annotation to keep and remove the
             * rest since we can have at most one scope annotation.
             */
            for (String annotation : annotations) {
                List<String> resultingAnnotations = new ArrayList<>(annotations);
                resultingAnnotations.remove(annotation);

                removeAnnotation(diagnostic, context, parentType, codeActions,
                        resultingAnnotations.toArray(new String[] {}));
            }

        }
        return codeActions;

    }
}
