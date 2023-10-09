/*******************************************************************************
* Copyright (c) 2023 IBM Corporation and others.
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
package org.eclipse.lsp4jakarta.jdt.internal.persistence;

import org.eclipse.lsp4jakarta.commons.codeaction.ICodeActionId;
import org.eclipse.lsp4jakarta.commons.codeaction.JakartaCodeActionId;
import org.eclipse.lsp4jakarta.jdt.core.java.codeaction.InsertAnnotationAttributesQuickFix;
import org.eclipse.lsp4jakarta.jdt.internal.Messages;

/**
 * Inserts the @MapKeyJoinColumn along with its name and referencedColumnName
 * attributes if missing.
 */
public class InserMapKeyJoinColumAnnotationAttributesQuickFix extends InsertAnnotationAttributesQuickFix {

    /**
     * Constructor.
     */
    public InserMapKeyJoinColumAnnotationAttributesQuickFix() {
        super("jakarta.persistence.MapKeyJoinColumn", "name", "referencedColumnName");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getParticipantId() {
        return InserMapKeyJoinColumAnnotationAttributesQuickFix.class.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ICodeActionId getCodeActionId() {
        return JakartaCodeActionId.PersistenceInsertAttributesToMKJCAnnotation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getLabel(String annotation, String[] attributes) {
        return Messages.getMessage("InsertTheMissingAttributes");
    }
}
