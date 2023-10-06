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
*     IBM Corporation - initial API and implementation
*******************************************************************************/
package org.eclipse.lsp4jakarta.ls.api;

import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;

/**
 * Jakarta code action resolution (workspace edit) provider.
 */
public interface JakartaJavaCodeActionResolveProvider {

	@JsonRequest("jakarta/java/codeActionResolve")
	CompletableFuture<CodeAction> resolveCodeAction(CodeAction unresolved);

}
