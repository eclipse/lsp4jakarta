/*******************************************************************************
* Copyright (c) 2020, 2022 Red Hat Inc. and others.
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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.function.BiPredicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.InsertTextFormat;
import org.eclipse.lsp4j.MarkupContent;
import org.eclipse.lsp4j.MarkupKind;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4jakarta.commons.utils.StringUtils;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;

/**
 * A registry for snippets which uses the same format than vscode snippet.
 *
 * @author Angelo ZERR
 *
 */
public class SnippetRegistry {

	private static final Logger LOGGER = Logger.getLogger(SnippetRegistry.class.getName());

	private final List<Snippet> snippets;

	public SnippetRegistry() {
		this(null);
	}

	public SnippetRegistry(String languageId) {
		this(languageId, true);
	}

	/**
	 * Snippet registry for a given language id.
	 * 
	 * @param languageId  the language id and null otherwise.
	 * @param loadDefault true if default snippets from SPI must be loaded and false
	 *                    otherwise.
	 */
	public SnippetRegistry(String languageId, boolean loadDefault) {
		snippets = new ArrayList<>();
		// Load snippets from SPI
		if (loadDefault) {
			ServiceLoader<ISnippetRegistryLoader> loaders = ServiceLoader.load(ISnippetRegistryLoader.class);
			loaders.forEach(loader -> {
				if (Objects.equals(languageId, loader.getLanguageId())) {
					try {
						loader.load(this);
					} catch (Exception e) {
						LOGGER.log(Level.SEVERE, "Error while consumming snippet loader " + loader.getClass().getName(),
								e);
					}
				}
			});
		}
	}

	/**
	 * Register the given snippet.
	 * 
	 * @param snippet the snippet to register.
	 */
	public void registerSnippet(Snippet snippet) {
		snippets.add(snippet);
	}

	/**
	 * Register the snippets from the given JSON input stream.
	 * 
	 * @param in the JSON input stream which declares snippets with vscode snippet
	 *           format.
	 * @throws IOException
	 */
	public void registerSnippets(InputStream in) throws IOException {
		registerSnippets(in, null, null);
	}

	/**
	 * Register the snippets from the given JSON stream with a context.
	 * 
	 * @param in                  the JSON input stream which declares snippets with
	 *                            vscode snippet format.
	 * @param contextDeserializer the GSON context deserializer used to create Java
	 *                            context.
	 * @throws IOException
	 */
	public void registerSnippets(InputStream in, TypeAdapter<? extends ISnippetContext<?>> contextDeserializer)
			throws IOException {
		registerSnippets(in, null, contextDeserializer);
	}

	/**
	 * Register the snippets from the given JSON stream with a context.
	 * 
	 * @param in             the JSON input stream which declares snippets with
	 *                       vscode snippet format.
	 * @param defaultContext the default context.
	 * @throws IOException
	 */
	public void registerSnippets(InputStream in, ISnippetContext<?> defaultContext) throws IOException {
		registerSnippets(in, defaultContext, null);
	}

	/**
	 * Register the snippets from the given JSON stream with a context.
	 * 
	 * @param in                  the JSON input stream which declares snippets with
	 *                            vscode snippet format.
	 * @param defaultContext      the default context.
	 * @param contextDeserializer the GSON context deserializer used to create Java
	 *                            context.
	 * @throws IOException
	 */
	public void registerSnippets(InputStream in, ISnippetContext<?> defaultContext,
			TypeAdapter<? extends ISnippetContext<?>> contextDeserializer) throws IOException {
		registerSnippets(new InputStreamReader(in, StandardCharsets.UTF_8.name()), defaultContext, contextDeserializer);
	}

	/**
	 * Register the snippets from the given JSON reader.
	 * 
	 * @param in the JSON reader which declares snippets with vscode snippet format.
	 * @throws IOException
	 */
	public void registerSnippets(Reader in) throws IOException {
		registerSnippets(in, null, null);
	}

	/**
	 * Register the snippets from the given JSON reader with a context.
	 * 
	 * @param in                  the JSON reader which declares snippets with
	 *                            vscode snippet format.
	 * @param contextDeserializer the GSON context deserializer used to create Java
	 *                            context.
	 * @throws IOException
	 */
	public void registerSnippets(Reader in, TypeAdapter<? extends ISnippetContext<?>> contextDeserializer)
			throws IOException {
		registerSnippets(in, null, contextDeserializer);
	}

	/**
	 * Register the snippets from the given JSON stream with a context.
	 * 
	 * @param in             the JSON reader which declares snippets with vscode
	 *                       snippet format.
	 * @param defaultContext the default context.
	 * @throws IOException
	 */
	public void registerSnippets(Reader in, ISnippetContext<?> defaultContext) throws IOException {
		registerSnippets(in, defaultContext, null);
	}

	/**
	 * Register the snippets from the given JSON stream with a context.
	 * 
	 * @param in                  the JSON reader which declares snippets with
	 *                            vscode snippet format.
	 * @param defaultContext      the default context.
	 * @param contextDeserializer the GSON context deserializer used to create Java
	 *                            context.
	 * @throws IOException
	 */
	public void registerSnippets(Reader in, ISnippetContext<?> defaultContext,
			TypeAdapter<? extends ISnippetContext<?>> contextDeserializer) throws IOException {
		JsonReader reader = new JsonReader(in);
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			Snippet snippet = createSnippet(reader, contextDeserializer);
			if (snippet.getDescription() == null) {
				snippet.setDescription(name);
			}
			if (snippet.getContext() == null) {
				snippet.setContext(defaultContext);
			}
			registerSnippet(snippet);
		}
		reader.endObject();
	}

	private static Snippet createSnippet(JsonReader reader,
			TypeAdapter<? extends ISnippetContext<?>> contextDeserializer) throws JsonIOException, JsonSyntaxException {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Snippet.class, new SnippetDeserializer(contextDeserializer));
		return builder.create().fromJson(reader, Snippet.class);
	}

	/**
	 * Returns all snippets.
	 * 
	 * @return all snippets.
	 */
	public List<Snippet> getSnippets() {
		return snippets;
	}

	/**
	 * Returns the snippet completion items according to the context filter.
	 * 
	 * @param replaceRange       the replace range.
	 * @param lineDelimiter      the line delimiter.
	 * @param canSupportMarkdown true if markdown is supported to generate
	 *                           documentation and false otherwise.
	 * @param contextFilter      the context filter.
	 * @param initialModel       the initial model.
	 * @return the snippet completion items according to the context filter.
	 */
	public List<CompletionItem> getCompletionItems(Range replaceRange, String lineDelimiter, boolean canSupportMarkdown,
			boolean snippetsSupported, BiPredicate<ISnippetContext<?>, Map<String, String>> contextFilter,
			Map<String, String> initialModel, ISuffixPositionProvider suffixProvider) {
		if (replaceRange == null) {
			return Collections.emptyList();
		}
		final Map<String, String> model = initialModel != null ? initialModel : new HashMap<>();
		return getSnippets().stream().filter(snippet -> {
			return snippet.match(contextFilter, model);
		}).map(snippet -> {
			CompletionItem item = new CompletionItem();
			String prefix = snippet.getPrefixes().get(0);
			String label = snippet.getLabel() != null ? snippet.getLabel() : prefix;
			item.setLabel(label);
			String insertText = getInsertText(snippet, model, snippetsSupported, lineDelimiter);
			item.setKind(CompletionItemKind.Snippet);
			item.setDocumentation(
					Either.forRight(createDocumentation(snippet, model, canSupportMarkdown, lineDelimiter)));
			item.setFilterText(prefix);
			item.setDetail(snippet.getDescription());
			Range range = replaceRange;
			if (!StringUtils.isEmpty(snippet.getSuffix()) && suffixProvider != null) {
				Position end = suffixProvider.findSuffixPosition(snippet.getSuffix());
				if (end != null) {
					range = new Range(replaceRange.getStart(), end);
				}
			}
			item.setTextEdit(Either.forLeft(new TextEdit(range, insertText)));
			item.setInsertTextFormat(InsertTextFormat.Snippet);
			item.setSortText(snippet.getSortText());
			return item;

		}).collect(Collectors.toList());
	}

	private static MarkupContent createDocumentation(Snippet snippet, Map<String, String> model,
			boolean canSupportMarkdown, String lineDelimiter) {
		StringBuilder doc = new StringBuilder();
		if (canSupportMarkdown) {
			doc.append(System.lineSeparator());
			doc.append("```");
			String scope = snippet.getScope();
			if (scope != null) {
				doc.append(scope);
			}
			doc.append(System.lineSeparator());
		}
		String insertText = getInsertText(snippet, model, false, lineDelimiter);
		doc.append(insertText);
		if (canSupportMarkdown) {
			doc.append(System.lineSeparator());
			doc.append("```");
			doc.append(System.lineSeparator());
		}
		return new MarkupContent(canSupportMarkdown ? MarkupKind.MARKDOWN : MarkupKind.PLAINTEXT, doc.toString());
	}

	private static String getInsertText(Snippet snippet, Map<String, String> model, boolean keepPlaceholders,
			String lineDelimiter) {
		StringBuilder text = new StringBuilder();
		int i = 0;
		List<String> body = snippet.getBody();
		if (body != null) {
			for (String bodyLine : body) {
				if (i > 0) {
					text.append(lineDelimiter);
				}
				replacePlaceholders(bodyLine, 0, model, keepPlaceholders, text);
				i++;
			}
		}
		return text.toString();
	}

	/**
	 * Replace place holders (ex : ${name}) from the given <code>line</code> by
	 * using the given context <code>model</code>.
	 * 
	 * @param line               the line which can have some place holders.
	 * @param offset             the start offset where the replace must be occured.
	 * @param model              the context model.
	 * @param keepDollarVariable true if place holder (ex : ${name}) must be kept
	 *                           (ex : ${name}) or not (ex : name)
	 * @param newLine            the replace line buffer result.
	 */
	private static void replacePlaceholders(String line, int offset, Map<String, String> model,
			boolean keepDollarVariable, StringBuilder newLine) {
		int dollarIndex = line.indexOf("$", offset);
		if (dollarIndex == -1 || dollarIndex == line.length() - 1) {
			newLine.append(line.substring(offset, line.length()));
			return;
		}
		char next = line.charAt(dollarIndex + 1);
		if (Character.isDigit(next)) {
			// ex: line = @RegistryType(type=$1)
			if (!keepDollarVariable) {
				newLine.append(line.substring(offset, dollarIndex));
			}
			int lastDigitOffset = dollarIndex + 1;
			while (line.length() < lastDigitOffset && Character.isDigit(line.charAt(lastDigitOffset))) {
				lastDigitOffset++;
			}
			if (keepDollarVariable) {
				newLine.append(line.substring(offset, lastDigitOffset));
			}
			replacePlaceholders(line, lastDigitOffset, model, keepDollarVariable, newLine);
		} else if (next == '{') {
			int startExpr = dollarIndex;
			int endExpr = line.indexOf("}", startExpr);
			if (endExpr == -1) {
				// Should never occur
				return;
			}
			newLine.append(line.substring(offset, startExpr));
			// Parameter
			int startParam = startExpr + 2;
			int endParam = endExpr;
			boolean onlyNumber = true;
			for (int i = startParam; i < endParam; i++) {
				char ch = line.charAt(i);
				if (!Character.isDigit(ch)) {
					onlyNumber = false;
					if (ch == ':') {
						startParam = i + 1;
						break;
					} else if (ch == '|') {
						startParam = i + 1;
						int index = line.indexOf(',', startExpr);
						if (index != -1) {
							endParam = index;
						}
						break;
					} else {
						break;
					}
				}
			}
			String paramName = line.substring(startParam, endParam);
			if (model.containsKey(paramName)) {
				paramName = model.get(paramName);
			} else if (keepDollarVariable) {
				paramName = line.substring(startExpr, endExpr + 1);
			}
			if (!(!keepDollarVariable && onlyNumber)) {
				newLine.append(paramName);
			}
			replacePlaceholders(line, endExpr + 1, model, keepDollarVariable, newLine);
		}
	}
}
