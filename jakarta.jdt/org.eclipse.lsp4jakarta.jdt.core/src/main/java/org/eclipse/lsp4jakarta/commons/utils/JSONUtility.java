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
package org.eclipse.lsp4jakarta.commons.utils;

import java.util.HashMap;

import org.eclipse.lsp4j.jsonrpc.json.MessageJsonHandler;
import org.eclipse.lsp4j.jsonrpc.json.adapters.EitherTypeAdapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

/**
 * Utilities for working with JSON that has been converted to an Object using Gson.
 */
public class JSONUtility {

    private JSONUtility() {}

    private static final Gson LSP4J_GSON = new MessageJsonHandler(new HashMap<>()).getGson();

    private static final Gson EITHER_GSON = new GsonBuilder() //
                    .registerTypeAdapterFactory(new EitherTypeAdapter.Factory()).create();

    /**
     * Converts the given Object to the given class using lsp4j's GSON logic.
     *
     * @param <T> the class to convert the Object to
     * @param object the object to convert
     * @param clazz the class to convert the Object to
     * @return the given Object converted to the given class using lsp4j's GSON
     *         logic
     */
    public static <T> T toModel(Object object, Class<T> clazz) {
        return toModel(getLsp4jGson(), object, clazz);
    }

    /**
     * Converts the given Object to the given class using the given GSON instance.
     *
     * @param <T> the class to convert the Object to
     * @param gson the gson instance to use to perform the conversion
     * @param object the object to convert
     * @param clazz the class to convert the Object to
     * @return the given Object converted to the given class using the given GSON
     *         instance
     */
    public static <T> T toModel(Gson gson, Object object, Class<T> clazz) {
        if (object == null) {
            return null;
        }
        if (clazz == null) {
            throw new IllegalArgumentException("Class can not be null");
        }
        if (object instanceof JsonElement) {
            return gson.fromJson((JsonElement) object, clazz);
        }
        if (clazz.isInstance(object)) {
            return clazz.cast(object);
        }
        // if nothing else works, try serializing and deserializing again
        return gson.fromJson(gson.toJson(object), clazz);
    }

    /**
     * Returns a Gson instance configured similarly to the instance lsp4j uses.
     *
     * @return a Gson instance configured similarly to the instance lsp4j uses
     */
    public static Gson getLsp4jGson() {
        return LSP4J_GSON;
    }

    /**
     * Returns a Gson instance with most of the default options, but with the
     * ability to parse {@code org.eclipse.lsp4j.Either}.
     *
     * @return a Gson instance with most of the default options, but with the
     *         ability to parse {@code org.eclipse.lsp4j.Either}
     */
    public static Gson getEitherGson() {
        return EITHER_GSON;
    }
}