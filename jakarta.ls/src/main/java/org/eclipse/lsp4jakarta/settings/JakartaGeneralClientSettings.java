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

import org.eclipse.lsp4jakarta.commons.utils.JSONUtility;

/**
 * Class to hold all settings from the client side.
 *
 *
 * This class is created through the deserialization of a JSON object. Each
 * internal setting must be represented by a class and have:
 *
 * 1) A constructor with no parameters
 *
 * 2) The JSON key/parent for the settings must have the same name as a
 * variable.
 *
 * eg: {"trace" : {...}}
 *
 * Based on: https://github.com/eclipse/lsp4mp/blob/0.9.0/microprofile.ls/org.eclipse.lsp4mp.ls/src/main/java/org/eclipse/lsp4mp/settings/MicroProfileGeneralClientSettings.java
 */
public class JakartaGeneralClientSettings {

    private JakartaTraceSettings trace;

    /**
     * Returns the trace settings.
     *
     * @return the trace settings.
     */
    public JakartaTraceSettings getTrace() {
        return trace;
    }

    /**
     * Set the validation settings.
     *
     * @param trace the trace settings.
     */
    public void setTrace(JakartaTraceSettings trace) {
        this.trace = trace;
    }

    /**
     * Returns the general settings from the given initialization options
     *
     * @param initializationOptionsSettings the initialization options
     * @return the general settings from the given initialization options
     */
    public static JakartaGeneralClientSettings getGeneralJakartaSettings(Object initializationOptionsSettings) {
        return JSONUtility.toModel(initializationOptionsSettings, JakartaGeneralClientSettings.class);
    }
}