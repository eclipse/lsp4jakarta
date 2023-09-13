/*******************************************************************************
* Copyright (c) 2023 Red Hat Inc. and others.
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
package org.eclipse.lsp4jakarta.commons.codeaction;

/**
 * Represents id of an lsp4mp code action.
 */
public enum JakartaCodeActionId implements ICodeActionId {
	IgnoreUnknownProperty,
	// JAXRS
	NonPublicResourceMethod, MakeConstructorPublic, NoargPublicConstructor, RemoveAllEntityParametersExcept;

	@Override
	public String getId() {
		return name();
	}
}