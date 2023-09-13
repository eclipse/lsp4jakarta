/*******************************************************************************
* Copyright (c) 2019 Red Hat Inc. and others.
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
package org.eclipse.lsp4jakarta.ls.api;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.eclipse.lsp4jakarta.commons.JakartaJavaCodeActionParams;

/**
 * MicroProfile Java code action provider.
 *
 * @author Angelo ZERR
 *
 */
public interface JakartaJavaCodeActionProvider {

	@JsonRequest("jakarta/java/codeAction")
	CompletableFuture<List<CodeAction>> getJavaCodeAction(JakartaJavaCodeActionParams javaParams);

}
