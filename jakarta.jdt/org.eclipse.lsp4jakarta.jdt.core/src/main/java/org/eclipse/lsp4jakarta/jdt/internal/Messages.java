/*******************************************************************************
* Copyright (c) 2022, 2023 IBM Corporation and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0.
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     IBM Corporation - initial implementation
*******************************************************************************/
package org.eclipse.lsp4jakarta.jdt.internal;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import org.eclipse.lsp4jakarta.jdt.core.JakartaCorePlugin;

/**
 * Message formatter.
 */
public final class Messages {
    private static ResourceBundle resourceBundle = null;

    private static synchronized void initializeBundles() {
        resourceBundle = ResourceBundle.getBundle("org.eclipse.lsp4jakarta.jdt.core.messages.messages",
                                                  Locale.getDefault());
    }

    /**
     * Returns message for the given key defined in resource bundle file.
     *
     * @param key the given key
     * @param args replacements
     * @return Returns message for the given key defined in resource bundle file
     */
    public static String getMessage(String key, Object... args) {
        if (resourceBundle == null) {
            initializeBundles();
        }
        String msg = null;
        try {
            msg = resourceBundle.getString(key);
            if (msg != null && args != null && args.length > 0) {
                msg = MessageFormat.format(msg, args);
            }
        } catch (Exception e) {
            JakartaCorePlugin.logException("Failed to get message for '" + key + "'", e);
        }
        return (msg == null) ? key : msg;
    }
}
