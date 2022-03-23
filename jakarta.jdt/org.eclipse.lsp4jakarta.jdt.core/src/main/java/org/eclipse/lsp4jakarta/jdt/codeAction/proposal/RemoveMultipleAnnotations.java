/*******************************************************************************
* Copyright (c) 2022 IBM Corporation and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0.
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     IBM Corporation, Adit Rada - initial API and implementation
*******************************************************************************/
package org.eclipse.lsp4jakarta.jdt.codeAction.proposal;

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


public abstract class RemoveMultipleAnnotations extends RemoveAnnotationConflictQuickFix {
    
    public RemoveMultipleAnnotations() {
        // annotation list to be derived from the diagnostic passed to
        // `getCodeActions()`
        super();
    }
    
    @Override
    public List<? extends CodeAction> getCodeActions(JavaCodeActionContext context, Diagnostic diagnostic,
            IProgressMonitor monitor) throws CoreException {
        ASTNode node = context.getCoveredNode();
        IBinding parentType = getBinding(node);

        JsonArray diagnosticData = (JsonArray) diagnostic.getData();

        List<String> annotations = IntStream.range(0, diagnosticData.size())
                .mapToObj(idx -> diagnosticData.get(idx).getAsString()).collect(Collectors.toList());

        List<CodeAction> codeActions = new ArrayList<>();
        
        List<List<String>> annotationsListsToRemove = getMultipleRemoveAnnotations(annotations);
        for (List<String> annotationList : annotationsListsToRemove) {
            String[] annotaions = annotationList.toArray(new String[annotationList.size()]);
            removeAnnotation(diagnostic, context, parentType, codeActions, annotaions);
        }
        return codeActions;
    }
    
    /**
     * Each List in the returned List of Lists should be a set of annotations that
     * will be removed at one go.
     * 
     * @author Adit Rada
     *
     */
    protected abstract List<List<String>> getMultipleRemoveAnnotations(List<String> annotations);
}
