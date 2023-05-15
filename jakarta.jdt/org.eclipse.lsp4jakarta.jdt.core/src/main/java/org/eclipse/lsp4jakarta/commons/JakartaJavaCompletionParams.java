/*******************************************************************************
* Copyright (c) 2021 Red Hat Inc. and others.
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
*     IBM Corporation - convert for Jakarta
*******************************************************************************/
package org.eclipse.lsp4jakarta.commons;


import org.eclipse.lsp4j.Position;

/**
 * Parameters that are passed on completion trigger for MicroProfile projects in Java files
 *
 * @author datho7561
 */
public class JakartaJavaCompletionParams {

	private String uri;
	private Position position;

	public JakartaJavaCompletionParams() {

	}

	public JakartaJavaCompletionParams(String uri, Position position) {
		this();
		setUri(uri);
		setPosition(position);
	}

	/**
	 * Returns the java file uri.
	 *
	 * @return the java file uri.
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * Set the java file uri.
	 *
	 * @param uri the java file uri.
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}

	/**
	 * Returns the hover position
	 *
	 * @return the hover position
	 */
	public Position getPosition() {
		return position;
	}

	/**
	 * Sets the hover position
	 *
	 * @param position the hover position
	 */
	public void setPosition(Position position) {
		this.position = position;
	}

}