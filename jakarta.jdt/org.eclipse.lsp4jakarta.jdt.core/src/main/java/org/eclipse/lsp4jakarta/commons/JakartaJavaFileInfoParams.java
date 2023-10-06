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

/**
 * Jakarta Java file information parameters.
 *
 * @author Angelo ZERR
 *
 */
public class JakartaJavaFileInfoParams {

	private String uri;

	/**
	 * Returns the Java file uri.
	 *
	 * @return the Java file uri
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * Set the Java file uri.
	 *
	 * @param uri the Java file uri.
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}

}
