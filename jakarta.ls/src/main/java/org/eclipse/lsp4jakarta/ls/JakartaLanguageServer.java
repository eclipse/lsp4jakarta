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

package org.eclipse.lsp4jakarta.ls;

import java.util.List;
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
import org.eclipse.lsp4jakarta.commons.JakartaJavaFileInfo;
import org.eclipse.lsp4jakarta.commons.JakartaJavaFileInfoParams;
import org.eclipse.lsp4jakarta.commons.JakartaJavaProjectLabelsParams;
import org.eclipse.lsp4jakarta.commons.ProjectLabelInfoEntry;
import org.eclipse.lsp4jakarta.ls.api.JakartaJavaFileInfoProvider;
import org.eclipse.lsp4jakarta.ls.api.JakartaJavaProjectLabelsProvider;
import org.eclipse.lsp4jakarta.ls.api.JakartaLanguageClientAPI;
import org.eclipse.lsp4jakarta.ls.commons.ParentProcessWatcher.ProcessLanguageServer;
import org.eclipse.lsp4jakarta.ls.java.JakartaTextDocuments;

public class JakartaLanguageServer implements LanguageServer, ProcessLanguageServer, JakartaJavaProjectLabelsProvider, JakartaJavaFileInfoProvider {

    private Integer parentProcessId;

    private static final Logger LOGGER = Logger.getLogger(JakartaLanguageServer.class.getName());

    private final WorkspaceService workspaceService;
    private final TextDocumentService textDocumentService;
	private final JakartaTextDocuments javaDocuments;


    private JakartaLanguageClientAPI languageClient;

    public JakartaLanguageServer() {
        // Workspace service handles workspace settings changes and calls update
        // settings.
    	javaDocuments = new JakartaTextDocuments(this, this);
        workspaceService = new JakartaWorkspaceService(this);
        textDocumentService = new JakartaTextDocumentService(this, javaDocuments);
    }

    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
        LOGGER.info("Initializing Jakarta EE server");
        this.parentProcessId = params.getProcessId();
        ServerCapabilities serverCapabilities = new ServerCapabilities();
        serverCapabilities.setTextDocumentSync(TextDocumentSyncKind.Incremental);

        InitializeResult initializeResult = new InitializeResult(serverCapabilities);
        // Provide Completion Capability to the LS
        initializeResult.getCapabilities().setCompletionProvider(new CompletionOptions(false, null));				
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
    	//commenting out the following line until we get back to diagnostics etc int eh new JakartaLS
        //((JakartaTextDocumentService) textDocumentService).cleanDiagnostics();
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

	@Override
	public CompletableFuture<ProjectLabelInfoEntry> getJavaProjectLabels(
			JakartaJavaProjectLabelsParams javaParams) {
		return getLanguageClient().getJavaProjectLabels(javaParams);
	}

	@Override
	public CompletableFuture<List<ProjectLabelInfoEntry>> getAllJavaProjectLabels() {
		return getLanguageClient().getAllJavaProjectLabels();
	}

	@Override
	public CompletableFuture<JakartaJavaFileInfo> getJavaFileInfo(JakartaJavaFileInfoParams javaParams) {
		return getLanguageClient().getJavaFileInfo(javaParams);
	}

}
