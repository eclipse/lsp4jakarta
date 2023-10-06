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
package org.eclipse.lsp4jakarta.commons;

import org.eclipse.lsp4j.CodeActionContext;
import org.eclipse.lsp4j.CodeActionParams;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextDocumentIdentifier;

/**
 * Jakarta Java codeAction parameters.
 * 
 * Based on:
 * https://github.com/eclipse/lsp4mp/blob/0.9.0/microprofile.ls/org.eclipse.lsp4mp.ls/src/main/java/org/eclipse/lsp4mp/commons/MicroProfileJavaCodeActionParams.java
 * 
 * @author Angelo ZERR
 */
public class JakartaJavaCodeActionParams extends CodeActionParams {

	private boolean resourceOperationSupported;

	private boolean commandConfigurationUpdateSupported;

	private boolean resolveSupported;

	public JakartaJavaCodeActionParams() {
		super();
	}

	public JakartaJavaCodeActionParams(final TextDocumentIdentifier textDocument, final Range range,
			final CodeActionContext context) {
		super(textDocument, range, context);
	}

	public String getUri() {
		return getTextDocument().getUri();
	}

	public boolean isResourceOperationSupported() {
		return resourceOperationSupported;
	}

	public void setResourceOperationSupported(boolean resourceOperationSupported) {
		this.resourceOperationSupported = resourceOperationSupported;
	}

	public boolean isCommandConfigurationUpdateSupported() {
		return commandConfigurationUpdateSupported;
	}

	public void setCommandConfigurationUpdateSupported(boolean commandConfigurationUpdateSupported) {
		this.commandConfigurationUpdateSupported = commandConfigurationUpdateSupported;
	}

	public boolean isResolveSupported() {
		return this.resolveSupported;
	}

	public void setResolveSupported(boolean resolveSupported) {
		this.resolveSupported = resolveSupported;
	}

}
