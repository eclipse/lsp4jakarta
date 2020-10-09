<<<<<<< HEAD
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
=======
/******************************************************************************* 
 * Copyright (c) 2019 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
>>>>>>> Added JDT tools pkg and Diagnostics on methods in JakartaLanguageClient
package org.jakarta.jdt;

import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.jakarta.lsp4e.Activator;
import org.eclipse.jdt.internal.ui.javaeditor.DocumentAdapter;

/**
 * This class is a copy/paste of JDT LS
 * https://github.com/eclipse/eclipse.jdt.ls/blob/master/org.eclipse.jdt.ls.core/src/org/eclipse/jdt/ls/core/internal/handlers/JsonRpcHelpers.java
 * with deletions of some unnecessary methods and modifications to logging for Jakarta LS project.
 *
 */
public class JsonRpcHelpers {
	/**
	 * Convert line, column to a document offset.
	 * @param buffer
	 * @param line
	 * @param column
	 * @return
	 */
	public static int toOffset(IBuffer buffer, int line, int column){
		if (buffer != null) {
			return toOffset(toDocument(buffer), line, column);
		}
		return -1;
	}

	/**
	 * Convert line, column to a document offset.
	 * 
	 * @param document
	 * @param line
	 * @param column
	 * @return
	 */
	public static int toOffset(IDocument document, int line, int column) {
		try {
			return document.getLineOffset(line) + column;
		} catch (BadLocationException e) {
			Activator.logException(e.getMessage(), e);
		}
		return -1;
	}

	/**
	 * Convert offset to line number and column.
	 * @param buffer
	 * @param line
	 * @param column
	 * @return
	 */
	public static int[] toLine(IBuffer buffer, int offset){
		return toLine(toDocument(buffer), offset);
	}
	
	/**
	 * Convert the document offset to line number and column.
	 *
	 * @param document
	 * @param line
	 * @return
	 */
	public static int[] toLine(IDocument document, int offset) {
		try {
			int line = document.getLineOfOffset(offset);
			int column = offset - document.getLineOffset(line);
			return new int[] { line, column };
		} catch (BadLocationException e) {
			Activator.logException(e.getMessage(), e);
		}
		return null;
	}

	
	/**
	 * Returns an {@link IDocument} for the given buffer.
	 * The implementation tries to avoid copying the buffer unless required.
	 * The returned document may or may not be connected to the buffer.
	 *
	 * @param buffer a buffer
	 * @return a document with the same contents as the buffer or <code>null</code> is the buffer is <code>null</code>
	 */
	public static IDocument toDocument(IBuffer buffer) {
		if (buffer == null) {
			return null;
		}
		if (buffer instanceof IDocument) {
			return (IDocument) buffer;
		} else if (buffer instanceof DocumentAdapter) {
			IDocument document = ((DocumentAdapter) buffer).getDocument();
			if (document != null) {
				return document;
			}
		}
		return new org.eclipse.jdt.internal.core.DocumentAdapter(buffer);
	}



}