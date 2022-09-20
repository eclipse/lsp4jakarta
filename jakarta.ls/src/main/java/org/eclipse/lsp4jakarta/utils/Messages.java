/*******************************************************************************
* Copyright (c) 2022 IBM Corporation and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0.
*
* SPDX-License-Identifier: EPL-2.0
*******************************************************************************/
package org.eclipse.lsp4jakarta.utils;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public final class Messages {

//    private static final Logger LOGGER = Logger.getLogger(Messages.class.getName());

    private static ResourceBundle resourceBundle = null;

    private static synchronized void initializeBundles() {
        resourceBundle = ResourceBundle.getBundle("org.eclipse.lsp4jakarta.messages.messages", Locale.getDefault());
    }

    /**
     * Returns message for the given key defined in resource bundle file.
     * 
     * @param key  the given key
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
            // do nothing for now, turn the logger on for missing keys once all messages are
            // externalized
            // LOGGER.info("Failed to get message for '" + key + "'");
        }
        return (msg == null) ? key : msg;
    }
}
