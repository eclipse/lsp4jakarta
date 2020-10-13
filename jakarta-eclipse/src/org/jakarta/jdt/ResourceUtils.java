/*******************************************************************************
 * Copyright (c) 2016-2017 Red Hat Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Red Hat Inc. - initial API and implementation
 *******************************************************************************/
package org.jakarta.jdt;

import java.io.File;
import java.net.URI;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.URIUtil;

/**
 * This class is a copy/paste of JDT LS
 * https://github.com/eclipse/eclipse.jdt.ls/blob/master/org.eclipse.jdt.ls.core/src/org/eclipse/jdt/ls/core/internal/ResourceUtils.java
 * with deletions of some unnecessary methods for Jakarta LS project.
 *
 */
public class ResourceUtils {
	public static final String FILE_UNC_PREFIX = "file:////";
	
	/**
	 * Fix uris by adding missing // to single file:/ prefix
	 */
	public static String fixURI(URI uri) {
		if (uri == null) {
			return null;
		}
		if (Platform.OS_WIN32.equals(Platform.getOS()) && URIUtil.isFileURI(uri)) {
			uri = URIUtil.toFile(uri).toURI();
		}
		String uriString = uri.toString();
		return uriString.replaceFirst("file:/([^/])", "file:///$1");
	}

	public static File toFile(URI uri) {
		if (Platform.OS_WIN32.equals(Platform.getOS())) {
			return URIUtil.toFile(uri);
		}
		return new File(uri);
	}
	
	/**
	 * Format URIs to be consumed by clients. On Windows platforms, UNC (Universal
	 * Naming Convention) URIs are transformed to follow the <code>file://</code>
	 * pattern.
	 *
	 * @param uri
	 *            the String URI to transform.
	 * @return a String URI compatible with clients.
	 */
	public static String toClientUri(String uri) {
		if (uri != null && Platform.OS_WIN32.equals(Platform.getOS()) && uri.startsWith(FILE_UNC_PREFIX)) {
			uri = uri.replace(FILE_UNC_PREFIX, "file://");
		}
		return uri;
	}
}