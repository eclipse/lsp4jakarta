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
package org.eclipse.lsp4jakarta.settings;

import org.eclipse.lsp4j.jsonrpc.json.adapters.JsonElementTypeAdapter;
import org.eclipse.lsp4jakarta.commons.utils.JSONUtility;

import com.google.gson.annotations.JsonAdapter;

/**
 * Represents all settings under the 'jakarta' key
 *
 * { 'jakarta': {...} }
 */
public class AllJakartaSettings {

    private static class ToolsSettings {

        @JsonAdapter(JsonElementTypeAdapter.Factory.class)
        private Object tools;

        public Object getTools() {
            return tools;
        }

    }

    @JsonAdapter(JsonElementTypeAdapter.Factory.class)
    private Object jakarta;

    /**
     * @return the jakarta capabilities
     */
    public Object getJakarta() {
        return jakarta;
    }

    /**
     * Sets the client specified Jakarta capabilities.
     *
     * @param jakarta The client specified Jakarta capabilities
     */
    public void setJakarta(Object jakarta) {
        this.jakarta = jakarta;
    }

    /**
     * Returns the client specific tool settings under root->settings->jakarta->tools
     *
     * @param initializationOptionsSettings The client specific settings.
     *
     * @return The client specific tool settings under root->settings->jakarta->tools
     */
    public static Object getJakartaToolsSettings(Object initializationOptionsSettings) {
        AllJakartaSettings rootSettings = JSONUtility.toModel(initializationOptionsSettings,
                                                              AllJakartaSettings.class);
        if (rootSettings == null) {
            return null;
        }
        ToolsSettings jakartaSettings = JSONUtility.toModel(rootSettings.getJakarta(), ToolsSettings.class);
        return jakartaSettings != null ? jakartaSettings.getTools() : null;
    }
}