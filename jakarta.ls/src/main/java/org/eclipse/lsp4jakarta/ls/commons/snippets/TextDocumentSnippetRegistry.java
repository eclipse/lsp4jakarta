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
package org.eclipse.lsp4jakarta.ls.commons.snippets;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4jakarta.ls.commons.BadLocationException;
import org.eclipse.lsp4jakarta.ls.commons.TextDocument;

/**
 * Snippet registry which works with {@link TextDocument}.
 *
 * @author Angelo ZERR
 *
 */
public class TextDocumentSnippetRegistry extends SnippetRegistry {

	private static final Logger LOGGER = Logger.getLogger(TextDocumentSnippetRegistry.class.getName());

	public TextDocumentSnippetRegistry() {
		this(null);
	}

	public TextDocumentSnippetRegistry(String languageId, boolean loadDefault) {
		super(languageId, loadDefault);
	}

	public TextDocumentSnippetRegistry(String languageId) {
		super(languageId, true);
	}

	/**
	 * Returns the snippet completion items for the given completion offset and
	 * context filter.
	 *
	 * @param document           the text document.
	 * @param completionOffset   the completion offset.
	 * @param canSupportMarkdown true if markdown is supported to generate
	 *                           documentation and false otherwise.
	 * @param contextFilter      the context filter.
	 * @param model              the context model used to replace some place
	 *                           holder.
	 * @return the snippet completion items for the given completion offset and
	 *         context filter.
	 */
	public List<CompletionItem> getCompletionItems(TextDocument document, int completionOffset,
			boolean canSupportMarkdown, boolean snippetsSupported,
			BiPredicate<ISnippetContext<?>, Map<String, String>> contextFilter, Map<String, String> model) {
		try {
			String lineDelimiter = getLineDelimiter(document, completionOffset);
			Range replaceRange = getReplaceRange(document, completionOffset);
			return super.getCompletionItems(replaceRange, lineDelimiter, canSupportMarkdown, snippetsSupported,
					contextFilter, model, null);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error while computing snippet completion items", e);
			return Collections.emptyList();
		}
	}

	private static String getLineDelimiter(TextDocument document, int completionOffset) throws BadLocationException {
		int lineNumber = document.positionAt(completionOffset).getLine();
		return document.lineDelimiter(lineNumber);
	}

	public Range getReplaceRange(TextDocument document, int completionOffset) throws BadLocationException {
		String expr = getExpr(document, completionOffset);
		if (expr == null) {
			return null;
		}
		int startOffset = completionOffset - expr.length();
		int endOffset = completionOffset;
		return getReplaceRange(startOffset, endOffset, document);
	}

	protected String getExpr(TextDocument document, int completionOffset) {
		return findExprBeforeAt(document.getText(), completionOffset);
	}

	private Range getReplaceRange(int replaceStart, int replaceEnd, TextDocument document) throws BadLocationException {
		return new Range(document.positionAt(replaceStart), document.positionAt(replaceEnd));
	}

	private static String findExprBeforeAt(String text, int offset) {
		if (offset < 0 || offset > text.length()) {
			return null;
		}
		if (offset == 0) {
			return "";
		}
		StringBuilder expr = new StringBuilder();
		int i = offset - 1;
		for (; i >= 0; i--) {
			char ch = text.charAt(i);
			if (Character.isWhitespace(ch)) {
				break;
			} else {
				expr.insert(0, ch);
			}
		}
		return expr.toString();
	}

}
