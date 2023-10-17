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

import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.jsonrpc.json.adapters.JsonElementTypeAdapter;
import org.eclipse.lsp4jakarta.commons.utils.JSONUtility;

import com.google.gson.annotations.JsonAdapter;

/**
 * Represents all settings sent from the client
 *
 * { 'settings': { 'jakarta': {...}, 'http': {...} } }
 */
public class InitializationOptionsSettings {

    @JsonAdapter(JsonElementTypeAdapter.Factory.class)
    private Object settings;

    public Object getSettings() {
        return settings;
    }

    public void setSettings(Object settings) {
        this.settings = settings;
    }

    /**
     * Returns the "settings" section of
     * {@link InitializeParams#getInitializationOptions()}.
     *
     * Here a sample of initializationOptions
     *
     * <pre>
     * "initializationOptions": {
    		"settings": {
    			"jakarta": {
    				"tools": {
    					"trace": {
    					    "server: "verbose"
    					}
    				},
    				...
    			}
    		}
    	}
     * </pre>
     *
     * @param initializeParams
     * @return the "settings" section of
     *         {@link InitializeParams#getInitializationOptions()}.
     */
    public static Object getSettings(InitializeParams initializeParams) {
        InitializationOptionsSettings root = JSONUtility.toModel(initializeParams.getInitializationOptions(),
                                                                 InitializationOptionsSettings.class);
        return root != null ? root.getSettings() : null;
    }
}
