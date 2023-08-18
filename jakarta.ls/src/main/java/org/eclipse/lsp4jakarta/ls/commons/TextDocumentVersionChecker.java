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
package org.eclipse.lsp4jakarta.ls.commons;

import java.util.concurrent.CancellationException;

import org.eclipse.lsp4j.jsonrpc.CancelChecker;

/**
 * A {@link CancelChecker} implementation to throw a
 * {@link CancellationException} when version of {@link TextDocument} changed.
 *
 * @author Angelo ZERR
 *
 */
public class TextDocumentVersionChecker implements CancelChecker {

	private final TextDocument textDocument;

	private final int version;

	public TextDocumentVersionChecker(TextDocument textDocument, int version) {
		this.textDocument = textDocument;
		this.version = version;
	}

	@Override
	public void checkCanceled() {
		if (textDocument.getVersion() != version) {
			// the text document version has changed
			throw new CancellationException("Text document version '" + version + "' has changed to version '"
					+ textDocument.getVersion() + ".");
		}
	}

}