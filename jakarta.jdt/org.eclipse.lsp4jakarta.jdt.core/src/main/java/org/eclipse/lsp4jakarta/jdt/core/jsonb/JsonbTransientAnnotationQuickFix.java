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
package org.eclipse.lsp4jakarta.jdt.core.jsonb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.lsp4jakarta.jdt.codeAction.proposal.RemoveMultipleAnnotations;

/**
 * Quick fix for removing @JsonbTransient annotations when more than
 * one occur in a class.
 * The getCodeActions method is overridden in order to make sure that
 * we return our custom quick fixes. There will be two quick fixes given
 * to the user: (1) either remove @JsonbTransient or (2) remove all other
 * Jsonb annotations.
 * 
 * @author Adit Rada
 *
 */
public class JsonbTransientAnnotationQuickFix extends RemoveMultipleAnnotations {
    @Override
    protected List<List<String>> getMultipleRemoveAnnotations(List<String> annotations) {
        List<List<String>> annotationsListsToRemove = new ArrayList<List<String>>();

        if (annotations.contains(JsonbConstants.JSONB_TRANSIENT)) {
            // Provide as one option: Remove JsonbTransient
            annotationsListsToRemove.add(Arrays.asList("jakarta.json.bind.annotation.JsonbTransient"));
        }
        
        // Provide as another option: Remove all other JsonbAnnotations
        annotations.remove(JsonbConstants.JSONB_TRANSIENT);
        if (annotations.size() > 0) {
            annotationsListsToRemove.add(annotations);
        }

        return annotationsListsToRemove;
    }
}