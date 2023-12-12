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

/**
 * Shared settings.
 *
 * Based on: https://github.com/eclipse/lsp4mp/blob/0.9.0/microprofile.ls/org.eclipse.lsp4mp.ls/src/main/java/org/eclipse/lsp4mp/settings/SharedSettings.java
 *
 * @author Angelo ZERR
 */
public class SharedSettings {

    private final JakartaCompletionCapabilities completionCapabilities;
    private final JakartaHoverSettings hoverSettings;
    private final JakartaTraceSettings traceSettings;

    public SharedSettings() {
        this.completionCapabilities = new JakartaCompletionCapabilities();
        this.hoverSettings = new JakartaHoverSettings();
        this.traceSettings = new JakartaTraceSettings();
    }

    /**
     * Returns the completion capabilities.
     *
     * @return the completion capabilities.
     */
    public JakartaCompletionCapabilities getCompletionCapabilities() {
        return completionCapabilities;
    }

    /**
     * Returns the hover settings.
     *
     * @return the hover settings.
     */
    public JakartaHoverSettings getHoverSettings() {
        return hoverSettings;
    }

    /**
     * Returns the trace settings.
     *
     * @return the trace settings.
     */
    public JakartaTraceSettings getTraceSettings() {
        return traceSettings;
    }
}
