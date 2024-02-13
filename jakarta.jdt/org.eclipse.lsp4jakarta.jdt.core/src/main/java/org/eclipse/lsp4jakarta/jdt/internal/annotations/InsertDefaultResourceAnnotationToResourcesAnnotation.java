/*******************************************************************************
* Copyright (c) 2024 IBM Corporation and others.
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
package org.eclipse.lsp4jakarta.jdt.internal.annotations;

import org.eclipse.lsp4jakarta.commons.codeaction.ICodeActionId;
import org.eclipse.lsp4jakarta.commons.codeaction.JakartaCodeActionId;
import org.eclipse.lsp4jakarta.jdt.core.java.codeaction.InsertAnnotationAttributesQuickFix;

/**
 * Inserts the name attribute to the @Resource annotation to the active element.
 */
public class InsertDefaultResourceAnnotationToResourcesAnnotation extends InsertAnnotationAttributesQuickFix {

    /**
     * Constructor.
     */
    public InsertDefaultResourceAnnotationToResourcesAnnotation() {
        super("jakarta.annotation.Resource", "name", "type");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getParticipantId() {
        return InsertDefaultResourceAnnotationToResourcesAnnotation.class.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ICodeActionId getCodeActionId() {
        return JakartaCodeActionId.InsertDefaultResourceAnnotationToResourcesAnnotation;
    }
}
