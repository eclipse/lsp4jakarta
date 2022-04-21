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
*     Lidia Ataupillco Ramos - initial API and implementation
*******************************************************************************/
package org.eclipse.lsp4jakarta.jdt.codeAction.proposal.quickfix;

/**
 * Quickfix for adding attributes to existing annotations
 * 
 * @author Lidia Ataupillco Ramos
 *
 */
public class InsertAnnotationAttributesQuickFix extends InsertAnnotationQuickFix {
    public InsertAnnotationAttributesQuickFix(String annotation, String... attributes) {
        this(annotation, false, attributes);
    }
    
    public InsertAnnotationAttributesQuickFix(String annotation, boolean generateOnlyOneCodeAction, String... attributes) {
        super(annotation, generateOnlyOneCodeAction, attributes);
    }

    @Override
    protected String getLabel(String annotation, String... attributes) {
        String type = "";
        String name = "Add " + attributes[0] + " to " + annotation;
        name = name.concat(type);
        return name;
    }
}
