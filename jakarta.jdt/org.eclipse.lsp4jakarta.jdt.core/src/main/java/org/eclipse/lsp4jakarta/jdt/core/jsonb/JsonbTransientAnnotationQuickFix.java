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
*     IBM Corporation - initial API and implementation
*******************************************************************************/
package org.eclipse.lsp4jakarta.jdt.core.jsonb;

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

/**
 * Quick fix for removing @JsonbTransient annotations when more than
 * one occur in a class.
 * The getCodeActions method is overridden in order to make sure that
 * we return our custom quick fixes. There will be two quick fixes given
 * to the user: (1) either remove @JsonbTranient or (2) remove all other
 * Jsonb annotations.
 * 
 * @author Adit Rada
 *
 */
public class JsonbTransientAnnotationQuickFix extends RemoveAnnotationConflictQuickFix {

    public JsonbTransientAnnotationQuickFix() {
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

        if (parentType != null) {
            List<CodeAction> codeActions = new ArrayList<>();
            // We either keep, JsobTransient, or remove all other Jsonb annotations.
            removeAnnotation(diagnostic, context, parentType, codeActions,
                    "jakarta.json.bind.annotation.JsonbTransient");

            annotations.remove(JsonbConstants.JSONB_TRANSIENT);
            removeAnnotation(diagnostic, context, parentType, codeActions,
                    annotations.toArray(new String[] {}));       

            return codeActions;
        }
        return null;
    }
}
