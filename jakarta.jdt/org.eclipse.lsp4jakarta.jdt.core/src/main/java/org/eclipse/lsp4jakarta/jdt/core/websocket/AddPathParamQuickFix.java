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

package org.eclipse.lsp4jakarta.jdt.core.websocket;
import org.eclipse.lsp4jakarta.jdt.codeAction.proposal.quickfix.InsertAnnotationQuickFix;

/**
 * Quick fix for adding the @PathParam annotation when one of more 
 * parameters on a method annotated endpoint class decorated with 
 * any of the annotations @OnMessage, @OnOpen, @OnClose, @OnError
 * 
 * @author Lidia Ataupillco Ramos
 */
public class AddPathParamQuickFix extends InsertAnnotationQuickFix {
    public AddPathParamQuickFix() {
        super("jakarta.websocket.server.PathParam", false, "value");
    }

    @Override
    protected String getLabel(String annotation, String... attributes) {
        StringBuilder name = new StringBuilder("Insert ");
        name.append("@");
        name.append(annotation);
        return name.toString();
    }
}
