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

import org.eclipse.lsp4jakarta.jdt.codeAction.proposal.quickfix.RemoveAnnotationConflictQuickFix;

/**
 * Quick fix for removing @JsonbTransient annotations when more than
 * one occur in a class
 * 
 * @author Adit Rada
 *
 */
public class JsonbTransientAnnotationQuickFix extends RemoveAnnotationConflictQuickFix {
    public JsonbTransientAnnotationQuickFix() {
        super("jakarta.json.bind.annotation.JsonbTransient");
    }
}
