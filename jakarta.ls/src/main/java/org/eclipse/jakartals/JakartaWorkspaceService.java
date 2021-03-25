/*******************************************************************************
* Copyright (c) 2020 IBM Corporation and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0.
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     IBM Corporation - initial API and implementation
*******************************************************************************/

package org.eclipse.jakartals;

import org.eclipse.lsp4j.DidChangeConfigurationParams;
import org.eclipse.lsp4j.DidChangeWatchedFilesParams;
import org.eclipse.lsp4j.services.WorkspaceService;

public class JakartaWorkspaceService implements WorkspaceService {

    private final JakartaLanguageServer jakartaLanguageServer;

    public JakartaWorkspaceService(JakartaLanguageServer jls) {
        this.jakartaLanguageServer = jls;
    }

    @Override
    public void didChangeConfiguration(DidChangeConfigurationParams params) {
        jakartaLanguageServer.updateSettings(params.getSettings());
    }

    @Override
    public void didChangeWatchedFiles(DidChangeWatchedFilesParams params) {
        // Do nothing
    }

}
