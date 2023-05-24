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

package org.eclipse.lsp4jakarta.commons.snippets;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.InsertTextFormat;
import org.eclipse.lsp4j.MarkupContent;
import org.eclipse.lsp4j.MarkupKind;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4jakarta.commons.JavaCursorContextResult;
import org.eclipse.lsp4jakarta.snippets.JakartaEESnippetRegistryLoader;
import org.eclipse.lsp4jakarta.snippets.SnippetContextForJava;
import org.eclipse.lsp4jakarta.utils.Messages;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;

/**
 * A registry that holds snippets using vscode snippet format
 * 
 * @author Ankush Sharma, credit to Angelo ZERR
 */
public class SnippetRegistry {

    private static final String PACKAGE_NAME = "packagename";
    private static final String CLASS_NAME = "classname";
    private static final String[] RESOLVE_VARIABLES = { PACKAGE_NAME, CLASS_NAME };

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
     * 
     * @param snippet
     */
    public void registerSnippet(Snippet snippet) {
        snippets.add(snippet);
    }

    /**
     * Register snippets from a given JSON input stream
     * 
     * @param json
     * @throws IOException
     */
    public void registerSnippets(InputStream in) throws IOException {
        registerSnippets(in, null);
    }

    /**
     * Register the snippets from the given JSON stream with a context.
     *
     * @param json                the JSON input stream which declares snippets with
     *                            vscode snippet format.
     * @param contextDeserializer the GSON context deserializer used to create Java
     *                            context.
     * @throws IOException
     */
    public void registerSnippets(InputStream in, TypeAdapter<? extends ISnippetContext<?>> contextDeserializer)
            throws IOException {
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
     * 
     * @param reader
     * @param contextDeserializer
     * @return
     * @throws JsonIOException
     * @throws JsonSyntaxException
     */
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
     * @param context            the context filter.
     * @param prefix             completion prefix.
     * @return the snippet completion items according to the context filter.
     */
    public List<CompletionItem> getCompletionItem(final Range replaceRange, final String lineDelimiter,
            boolean canSupportMarkdown, List<String> context, JavaCursorContextResult cursorContext, String prefix) {
        List<Snippet> snippets = getSnippets();
        Map<String, String> values = new HashMap<String, String>();
        int size = context.size();
        if (size == snippets.size() + 2) { // the last 2 strings are package name and class name
            values.put(PACKAGE_NAME, context.get(size - 2));
            values.put(CLASS_NAME, context.get(size - 1));
        }
        String filter = (prefix != null) ? prefix.toLowerCase() : null;
        return snippets.stream()
            // filter list based on cursor context
            .filter(snippet -> 
            ((SnippetContextForJava) snippet.getContext())
            .snippetContentAppliesToContext(cursorContext))
            .map(snippet -> {
                String label = snippet.getPrefixes().get(0);
                if (context.get(snippets.indexOf(snippet)) == null
                        // in Eclipse, the filter is not working properly, have to add additional one
                        || (filter != null && filterLabel(filter, label.toLowerCase()) != true)) {
                    return null;
                }
                CompletionItem item = new CompletionItem();
                item.setLabel(label);
    //            item.setDetail(snippet.getDescription());
                item.setDetail(Messages.getMessage(snippet.getDescription()));
                String insertText = getInsertText(snippet, false, lineDelimiter, values);
                item.setKind(CompletionItemKind.Snippet);
                item.setDocumentation(
                        Either.forRight(createDocumentation(snippet, canSupportMarkdown, lineDelimiter, values)));
                item.setFilterText(label);

                TextEdit textEdit = new TextEdit(replaceRange, insertText);
                item.setTextEdit(Either.forLeft(textEdit));
                item.setInsertTextFormat(InsertTextFormat.Snippet);
                return item;
            }).filter(completionItems -> completionItems != null).collect(Collectors.toList());
    }
    
    /**
     * Returns all snippet completion items. This method does not take into account
     * the current context such as ClassPath, or imports, etc...
     * 
     * @param replaceRange       the replace range.
     * @param lineDelimeter      the line delimeter
     * @param canSupportMarkdown true if mardown is supported to generate
     *                           documentation and false otherwise
     * @return the snippet completion items irrespective of the current context.
     */

    public List<CompletionItem> getCompletionItemNoContext(final Range replaceRange, final String lineDelimeter,
            boolean canSupportMarkdown) {
        return getSnippets().stream().map(snippet -> {
            // To filter by context, I just need to provide document contexts, and then
            // perform a match and include or remove
            // List<String> snippetTypes = ((SnippetContextForJava)
            // snippet.getContext()).getTypes();
            String label = snippet.getPrefixes().get(0);
            CompletionItem item = new CompletionItem();
            item.setLabel(label);
            item.setDetail(snippet.getDescription());
            String insertText = getInsertText(snippet, false, lineDelimeter, null);

            item.setKind(CompletionItemKind.Snippet);
            item.setDocumentation(
                    Either.forRight(createDocumentation(snippet, canSupportMarkdown, lineDelimeter, null)));
            item.setFilterText(label);

            TextEdit textEdit = new TextEdit(replaceRange, insertText);
            item.setTextEdit(Either.forLeft(textEdit));
            item.setInsertTextFormat(InsertTextFormat.Snippet);
            return item;
        }).collect(Collectors.toList());
    }

    private static MarkupContent createDocumentation(Snippet snippet, boolean canSupportMarkdown,
            String lineDelimiter, Map<String, String> values) {
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
        String insertText = getInsertText(snippet, true, lineDelimiter, values);
        doc.append(insertText);
        if (canSupportMarkdown) {
            doc.append(System.lineSeparator());
            doc.append("```");
            doc.append(System.lineSeparator());
        }
        return new MarkupContent(canSupportMarkdown ? MarkupKind.MARKDOWN : MarkupKind.PLAINTEXT, doc.toString());
    }

    private static String getInsertText(Snippet snippet, boolean replace, String lineDelimiter,
            Map<String, String> values) {
        StringBuilder text = new StringBuilder();
        int i = 0;
        List<String> body = snippet.getBody();
        if (body != null) {
            Map<String, Set<String>> foundVars = new HashMap<String, Set<String>>();
            for (String bodyLine : body) {
                // resolve specific variables by values
                if (values != null && values.size() > 0) {
                    foundVars.clear();
                    // search for specific variables
                    getMatchedVariables(bodyLine, 0, RESOLVE_VARIABLES, foundVars);
                    if (foundVars.size() > 0) { // resolve specific variables by values
                        for (String key : foundVars.keySet()) {
                            String replacement = values.get(key);
                            if (replacement != null) {
                                Set<String> vars = foundVars.get(key);
                                for (Iterator<String> it = vars.iterator(); it.hasNext();) {
                                    bodyLine = bodyLine.replace(it.next(), replacement);
                                }
                            }
                        }
                    }
                }
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

    /**
     * Get all matched variables from given string line.
     * 
     * @param line    - given string to search
     * @param start   - position/index to start the search from
     * @param vars    - searching variables
     * @param matched - found variables
     */
    private static void getMatchedVariables(String line, int start, String[] vars,
            Map<String, Set<String>> matched) {
        int idxS = line.indexOf("${", start);
        if (idxS != -1) {
            int idxE = line.indexOf('}', idxS);
            if (idxE - 1 > idxS + 2) {
                String varStr = line.substring(idxS + 2, idxE).trim().toLowerCase();
                Arrays.stream(vars).forEach(var -> {
                    if (varStr.endsWith(var) == true) {
                        if (matched.containsKey(var) != true) {
                            matched.put(var, new HashSet<String>());
                        }
                        matched.get(var).add(line.substring(idxS, idxE + 1));
                    }
                });
                getMatchedVariables(line, idxE, vars, matched);
            }
        }
    }

    private boolean filterLabel(String filter, String label) {
        boolean pass = true;
        if (label.contains(filter) != true) {
            char[] chars = filter.toCharArray();
            int start = 0;
            for (char ch : chars) {
                start = label.indexOf(ch, start);
                if (start == -1) {
                    pass = false;
                    break;
                }
                start++;
            }
        }
        return pass;
    }
}
