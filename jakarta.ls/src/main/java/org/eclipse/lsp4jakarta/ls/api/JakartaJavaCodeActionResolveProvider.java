/*******************************************************************************
* Copyright (c) 2022 Red Hat Inc. and others.
* All rights reserved. This program and the accompanying materials
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v20.html
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     Red Hat Inc. - initial API and implementation
*******************************************************************************/
package org.eclipse.lsp4jakarta.ls.api;

import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;

/**
 * Represents a provider that can resolve workspace edits for an unresolved code
 * action.
 *
 * @author datho7561
 */
public interface JakartaJavaCodeActionResolveProvider {

	@JsonRequest("jakarta/java/codeActionResolve")
	CompletableFuture<CodeAction> resolveCodeAction(CodeAction unresolved);

}
