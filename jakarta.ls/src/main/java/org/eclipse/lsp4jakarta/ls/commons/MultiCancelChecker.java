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

import org.eclipse.lsp4j.jsonrpc.CancelChecker;

/**
 * Multiple cancel checker.
 *
 * @author Angelo ZERR
 *
 */
public class MultiCancelChecker implements CancelChecker {

	private CancelChecker[] checkers;

	public MultiCancelChecker(CancelChecker... checkers) {
		this.checkers = checkers;
	}

	@Override
	public void checkCanceled() {
		for (CancelChecker cancelChecker : checkers) {
			cancelChecker.checkCanceled();
		}
	}
}