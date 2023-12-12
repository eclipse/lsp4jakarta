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
package org.eclipse.lsp4jakarta.ls.commons.client;

import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4jakarta.commons.utils.JSONUtility;

/**
 * Represents all extended client capabilities sent from the server
 *
 * <pre>
 * "extendedClientCapabilities": {
 *      ...
 *      }
 *  }
 * </pre>
 */
public class InitializationOptionsExtendedClientCapabilities {

    private ExtendedClientCapabilities extendedClientCapabilities;

    public ExtendedClientCapabilities getExtendedClientCapabilities() {
        return extendedClientCapabilities;
    }

    public void setExtendedClientCapabilities(ExtendedClientCapabilities extendedClientCapabilities) {
        this.extendedClientCapabilities = extendedClientCapabilities;
    }

    /**
     * Returns the "settings" section of
     * {@link InitializeParams#getInitializationOptions()}.
     *
     * Here a sample of initializationOptions
     *
     * <pre>
     * "extendedClientCapabilities": {
     *      ...
     *      }
     *  }
     * </pre>
     *
     * @param initializeParams
     * @return the "extendedClientCapabilities" section of
     *         {@link InitializeParams#getInitializationOptions()}.
     */
    public static ExtendedClientCapabilities getExtendedClientCapabilities(InitializeParams initializeParams) {
        InitializationOptionsExtendedClientCapabilities root = JSONUtility.toModel(
                                                                                   initializeParams.getInitializationOptions(),
                                                                                   InitializationOptionsExtendedClientCapabilities.class);
        return root != null ? root.getExtendedClientCapabilities() : null;
    }
}
