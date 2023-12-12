/*******************************************************************************
* Copyright (c) 2023 IBM Corporation and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
* which is available at https://www.apache.org/licenses/LICENSE-2.0.
*
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
*
* Contributors:
*     IBM Corporation - initial API and implementation
*******************************************************************************/
package org.eclipse.lsp4jakarta.settings;

/**
 * Jakarta trace settings.
 */
public class JakartaTraceSettings {

    /** trace setting options. */
    private static enum Setting {
        info, verbose;
    }

    /** Holds the server trace setting. */
    private String server;

    /**
     * Constructor.
     */
    public JakartaTraceSettings() {
        setServer(Setting.info.name());
    }

    /**
     * Returns the trace setting for the server.
     *
     * @return The trace setting for the server.
     */
    public String getServer() {
        return server;
    }

    /**
     * Sets the trace setting for the server.
     *
     * @param setting The trace setting for the server.
     */
    public void setServer(String setting) {
        if (Setting.info.name().equals(setting) ||
            Setting.verbose.name().equals(setting)) {
            this.server = setting;
        }
    }

    /**
     * Update the trace settings with the given new trace settings.
     *
     * @param newTrace the new trace settings.
     */
    public void update(JakartaTraceSettings newTrace) {
        this.setServer(newTrace.getServer());
    }
}