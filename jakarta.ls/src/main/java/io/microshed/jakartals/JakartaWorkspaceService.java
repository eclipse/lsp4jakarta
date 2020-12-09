package io.microshed.jakartals;

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
