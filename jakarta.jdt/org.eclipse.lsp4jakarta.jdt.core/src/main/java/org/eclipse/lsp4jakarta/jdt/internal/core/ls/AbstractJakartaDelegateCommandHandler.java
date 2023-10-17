/*******************************************************************************
* Copyright (c) 2020 Red Hat Inc. and others.
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

import java.util.logging.Logger;

import org.eclipse.jdt.ls.core.internal.IDelegateCommandHandler;

/**
 * Abstract class for MicroProfile JDT LS command handler
 *
 * @author Angelo ZERR
 *
 */
public abstract class AbstractJakartaDelegateCommandHandler implements IDelegateCommandHandler {

    private static final Logger LOGGER = Logger.getLogger(AbstractJakartaDelegateCommandHandler.class.getName());

    private static boolean initialized;

    public AbstractJakartaDelegateCommandHandler() {
        initialize();
    }

    /**
     * Add MicroProfile properties changed listener if needed.
     */
    private static synchronized void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;
    }
}
