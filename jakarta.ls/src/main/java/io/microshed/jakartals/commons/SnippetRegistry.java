package io.microshed.jakartals.commons;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;

import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.InsertTextFormat;
import org.eclipse.lsp4j.MarkupContent;
import org.eclipse.lsp4j.MarkupKind;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

/**
 * A registry that holds snippets using vscode snippet format
 * 
 * @author Ankush Sharma, credit to Angelo ZERR
 */
public class SnippetRegistry {
    private static final Logger LOGGER = Logger.getLogger(SnippetRegistry.class.getName());

    List<Snippet> snippets; // Hold all snippets in this list

    /**
     * Initialize the Snippet registry and create the array of Snippets
     */
    public SnippetRegistry() {
        snippets = new ArrayList<>();
        // Load all of the snippets into the registry
        JakartaEESnippetRegistryLoader loader = new JakartaEESnippetRegistryLoader();
        try {
            loader.load(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Register the provided snippet
     * @param snippet
     */
    public void registerSnippet(Snippet snippet) {
        snippets.add(snippet);
    }

    /**
     * Register snippets from a given JSON input stream
     * @param json
     * @throws IOException
     */
    public void registerSnippets(InputStream in ) throws IOException {
        registerSnippets(in, null);
    }

    /**
	 * Register the snippets from the given JSON stream with a context.
	 *
	 * @param json                  the JSON input stream which declares snippets with
	 *                            vscode snippet format.
	 * @param contextDeserializer the GSON context deserializer used to create Java
	 *                            context.
	 * @throws IOException
	 */
    public void registerSnippets(InputStream in, TypeAdapter<? extends ISnippetContext<?>> contextDeserializer)
    throws IOException {
            LOGGER.info(in.toString());
            LOGGER.info("Trying to register some snippets");
            registerSnippets(new InputStreamReader(in, StandardCharsets.UTF_8.name()), contextDeserializer);
    }

    /**
	 * Register the snippets from the given JSON reader.
	 *
	 * @param in the JSON reader which declares snippets with vscode snippet format.
	 * @throws IOException
	 */
    public void registerSnippets(Reader in) throws IOException {
        registerSnippets(in, null);
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
        // Read stream of tokens as JSON
        JsonReader reader = new JsonReader(in);
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            Snippet snippet = createSnippet(reader, contextDeserializer);
            if (snippet.getDescription() == null) {
                snippet.setDescription(name);
            }
            registerSnippet(snippet);
        }
    }


    /**
     * Build a snippet from a given JsonReader
     * @param reader
     * @param contextDeserializer
     * @return
     * @throws JsonIOException
     * @throws JsonSyntaxException
     */
    private static Snippet createSnippet(
        JsonReader reader,
        TypeAdapter<? extends ISnippetContext<?>> contextDeserializer)
    throws JsonIOException, JsonSyntaxException {
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
	 * @return the snippet completion items according to the context filter.
	 */
	public List<CompletionItem> getCompletionItem(final Range replaceRange, final String lineDelimiter,
			boolean canSupportMarkdown, Predicate<ISnippetContext<?>> contextFilter) {
        // TODO Add context based filtering
        return getSnippets().stream().map(snippet -> { 
            String label = snippet.getPrefixes().get(0);
            CompletionItem item = new CompletionItem();
            item.setLabel(label);
            item.setDetail(snippet.getDescription());
            String insertText = getInsertText(snippet, false, lineDelimiter);
            item.setKind(CompletionItemKind.Snippet);
            item.setDocumentation(Either.forRight(createDocumentation(snippet, canSupportMarkdown, lineDelimiter)));
            item.setFilterText(label);
			item.setTextEdit(new TextEdit(replaceRange, insertText));
            item.setInsertTextFormat(InsertTextFormat.Snippet);
            return item;
        }).collect(Collectors.toList());
	}

	private static MarkupContent createDocumentation(Snippet snippet, boolean canSupportMarkdown,
			String lineDelimiter) {
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
		String insertText = getInsertText(snippet, true, lineDelimiter);
		doc.append(insertText);
		if (canSupportMarkdown) {
			doc.append(System.lineSeparator());
			doc.append("```");
			doc.append(System.lineSeparator());
		}
		return new MarkupContent(canSupportMarkdown ? MarkupKind.MARKDOWN : MarkupKind.PLAINTEXT, doc.toString());
	}

	private static String getInsertText(Snippet snippet, boolean replace, String lineDelimiter) {
		StringBuilder text = new StringBuilder();
		int i = 0;
		List<String> body = snippet.getBody();
		if (body != null) {
			for (String bodyLine : body) {
				if (i > 0) {
					text.append(lineDelimiter);
				}
				if (replace) {
					bodyLine = replace(bodyLine);
				}
				text.append(bodyLine);
				i++;
			}
		}
		return text.toString();
	}

	private static String replace(String line) {
		return replace(line, 0, null);
	}

	private static String replace(String line, int offset, StringBuilder newLine) {
		int startExpr = line.indexOf("${", offset);
		if (startExpr == -1) {
			if (newLine == null) {
				return line;
			}
			newLine.append(line.substring(offset, line.length()));
			return newLine.toString();
		}
		int endExpr = line.indexOf("}", startExpr);
		if (endExpr == -1) {
			// Should never occur
			return line;
		}
		if (newLine == null) {
			newLine = new StringBuilder();
		}
		newLine.append(line.substring(offset, startExpr));
		// Parameter
		int startParam = startExpr + 2;
		int endParam = endExpr;
		boolean startsWithNumber = true;
		for (int i = startParam; i < endParam; i++) {
			char ch = line.charAt(i);
			if (Character.isDigit(ch)) {
				startsWithNumber = true;
			} else if (ch == ':') {
				if (startsWithNumber) {
					startParam = i + 1;
				}
				break;
			} else if (ch == '|') {
				if (startsWithNumber) {
					startParam = i + 1;
					int index = line.indexOf(',', startExpr);
					if (index != -1) {
						endParam = index;
					}
				}
				break;
			} else {
				break;
			}
		}
		newLine.append(line.substring(startParam, endParam));
		return replace(line, endExpr + 1, newLine);
	}

	protected static String findExprBeforeAt(String text, int offset) {
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
