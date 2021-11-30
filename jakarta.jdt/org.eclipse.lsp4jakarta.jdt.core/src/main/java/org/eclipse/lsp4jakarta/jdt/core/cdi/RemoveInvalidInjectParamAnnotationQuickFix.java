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
*     Red Hat Inc. - initial API and implementation
*******************************************************************************/
package org.eclipse.lsp4jakarta.jdt.core.cdi;

import org.eclipse.lsp4jakarta.jdt.codeAction.proposal.quickfix.RemoveParamAnnotationQuickFix;

/**
 * QuickFix for deleting any of @Disposes, @Observes and @ObservesAsync annotation for parameters
 */
public class RemoveInvalidInjectParamAnnotationQuickFix extends RemoveParamAnnotationQuickFix {

    public RemoveInvalidInjectParamAnnotationQuickFix() {
    	super(ManagedBeanConstants.INVALID_INJECT_PARAMS.toArray((String[]::new)));
    }

}
