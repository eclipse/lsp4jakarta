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

package org.eclipse.lsp4jakarta;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import org.eclipse.lsp4j.CompletionOptions;
import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.ServerCapabilities;
import org.eclipse.lsp4j.TextDocumentSyncKind;
import org.eclipse.lsp4j.jsonrpc.CompletableFutures;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;
import org.eclipse.lsp4jakarta.api.JakartaLanguageClientAPI;
import org.eclipse.lsp4mp.ls.commons.ParentProcessWatcher.ProcessLanguageServer;

public class JakartaLanguageServer implements LanguageServer, ProcessLanguageServer {

    private Integer parentProcessId;

    private static final Logger LOGGER = Logger.getLogger(JakartaLanguageServer.class.getName());

    private final WorkspaceService workspaceService;
    private final TextDocumentService textDocumentService;

    private JakartaLanguageClientAPI languageClient;

    public JakartaLanguageServer() {
        // Workspace service handles workspace settings changes and calls update
        // settings.
        workspaceService = new JakartaWorkspaceService(this);
        textDocumentService = new JakartaTextDocumentService(this);
    }

    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
        LOGGER.info("Initializing Jakarta EE server");
        this.parentProcessId = params.getProcessId();
        ServerCapabilities serverCapabilities = new ServerCapabilities();
        serverCapabilities.setTextDocumentSync(TextDocumentSyncKind.Incremental);

        InitializeResult initializeResult = new InitializeResult(serverCapabilities);
        // Provide Completion Capability to the LS
        initializeResult.getCapabilities().setCompletionProvider(new CompletionOptions());
        initializeResult.getCapabilities().setHoverProvider(true);
        initializeResult.getCapabilities().setCodeActionProvider(true);
        return CompletableFuture.completedFuture(initializeResult);
    }

    public synchronized void updateSettings(Object initializationOptionsSettings) {
        if (initializationOptionsSettings == null) {
            return;
        }
        // TODO: else update settings
    }

    @Override
    public CompletableFuture<Object> shutdown() {
        // when shutting down LS, TextDocumentService.didClose() may not be called
        // properly, need to clear existing diagnostics
        ((JakartaTextDocumentService) textDocumentService).cleanDiagnostics();
        return CompletableFutures.computeAsync(cc -> new Object());
    }

    @Override
    public void exit() {
        exit(0);
    }

    @Override
    public void exit(int exitCode) {
        System.exit(exitCode);
    }

    @Override
    public TextDocumentService getTextDocumentService() {
        return this.textDocumentService;
    }

    @Override
    public WorkspaceService getWorkspaceService() {
        return this.workspaceService;
    }

    public JakartaLanguageClientAPI getLanguageClient() {
        return this.languageClient;
    }

    public void setLanguageClient(LanguageClient languageClient) {
        this.languageClient = (JakartaLanguageClientAPI) languageClient;
    }

    @Override
    public long getParentProcessId() {
        return parentProcessId != null ? parentProcessId : 0;
    }

}
