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
package org.eclipse.lsp4jakarta.jdt.internal.core.ls;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.eclipse.lsp4j.CodeActionContext;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextDocumentIdentifier;

/**
 * Arguments utilities.
 *
 * @author Angelo ZERR
 * 
 * Helper methods re-used from
 * https://github.com/eclipse/lsp4mp/blob/5cf8de4874bd0ec9904c411c871f4813b024ac96/microprofile.jdt/org.eclipse.lsp4mp.jdt.core/src/main/java/org/eclipse/lsp4mp/jdt/internal/core/ls/ArgumentUtils.java
 */
public class ArgumentUtils {
    
    private static final String DATA_PROPERTY = "data";
    private static final String SOURCE_PROPERTY = "source";
    private static final String MESSAGE_PROPERTY = "message";
    private static final String CODE_PROPERTY = "code";
    private static final String RANGE_PROPERTY = "range";
    private static final String DIAGNOSTICS_PROPERTY = "diagnostics";
    private static final String END_PROPERTY = "end";
    private static final String START_PROPERTY = "start";
    private static final String CHARACTER_PROPERTY = "character";
    private static final String LINE_PROPERTY = "line";
    private static final String URI_PROPERTY = "uri";
    

    public static Map<String, Object> getFirst(List<Object> arguments) {
        return arguments.isEmpty() ? null : (Map<String, Object>) arguments.get(0);
    }

    public static String getString(Map<String, Object> obj, String key) {
        return (String) obj.get(key);
    }

    @SuppressWarnings("unchecked")
    public static List<String> getStringList(Map<String, Object> obj, String key) {
        return (List<String>) obj.get(key);
    }

    public static boolean getBoolean(Map<String, Object> obj, String key) {
        Object result = obj.get(key);
        return result != null && result instanceof Boolean && ((Boolean) result).booleanValue();
    }

    public static int getInt(Map<String, Object> obj, String key) {
        Object result = obj.get(key);
        return result != null && result instanceof Number ? ((Number) result).intValue() : 0;
    }

    public static TextDocumentIdentifier getTextDocumentIdentifier(Map<String, Object> obj, String key) {
        Map<String, Object> textDocumentIdentifierObj = (Map<String, Object>) obj.get(key);
        if (textDocumentIdentifierObj == null) {
            return null;
        }
        String uri = getString(textDocumentIdentifierObj, URI_PROPERTY);
        return new TextDocumentIdentifier(uri);
    }

    public static Position getPosition(Map<String, Object> obj, String key) {
        Map<String, Object> positionObj = (Map<String, Object>) obj.get(key);
        if (positionObj == null) {
            return null;
        }
        int line = getInt(positionObj, LINE_PROPERTY);
        int character = getInt(positionObj, CHARACTER_PROPERTY);
        return new Position(line, character);
    }

    public static Range getRange(Map<String, Object> obj, String key) {
        Map<String, Object> rangeObj = (Map<String, Object>) obj.get(key);
        if (rangeObj == null) {
            return null;
        }
        Position start = getPosition(rangeObj, START_PROPERTY);
        Position end = getPosition(rangeObj, END_PROPERTY);
        return new Range(start, end);
    }

    public static CodeActionContext getCodeActionContext(Map<String, Object> obj, String key) {
        Map<String, Object> contextObj = (Map<String, Object>) obj.get(key);
        if (contextObj == null) {
            return null;
        }
        List<Map<String, Object>> diagnosticsObj = (List<Map<String, Object>>) contextObj.get(DIAGNOSTICS_PROPERTY);
        List<Diagnostic> diagnostics = diagnosticsObj.stream().map(diagnosticObj -> {
            Diagnostic diagnostic = new Diagnostic();
            diagnostic.setRange(getRange(diagnosticObj, RANGE_PROPERTY));
            diagnostic.setCode(getString(diagnosticObj, CODE_PROPERTY));
            diagnostic.setMessage(getString(diagnosticObj, MESSAGE_PROPERTY));
            diagnostic.setSource(getString(diagnosticObj, SOURCE_PROPERTY));
            // In Eclipse IDE (LSP client), the data is JsonObject, and in JDT-LS (ex :
            // vscode as LSP client) the data is a Map, we
            // convert the Map to a JsonObject to be consistent with any LSP clients.
            diagnostic.setData(getObjectAsJson(diagnosticObj, DATA_PROPERTY));
            return diagnostic;
        }).collect(Collectors.toList());
        List<String> only = null;
        return new CodeActionContext(diagnostics, only);
    }
    
    public static JsonObject getObjectAsJson(Map<String, Object> obj, String key) {
        Object child = obj.get(key);
        if (child != null && child instanceof Map<?, ?>) {
            Gson gson = new Gson();
            return (JsonObject) gson.toJsonTree(obj);
        }
        return null;
    }
    
}