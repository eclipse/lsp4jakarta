package io.microshed.jakartals;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import org.eclipse.lsp4j.CompletionOptions;
import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.ServerCapabilities;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;
import org.eclipse.lsp4mp.ls.commons.ParentProcessWatcher.ProcessLanguageServer;

import io.microshed.jakartals.api.JakartaLanguageClientAPI;

public class JakartaLanguageServer implements LanguageServer, ProcessLanguageServer {

  private Integer parentProcessId;

  private static final Logger LOGGER = Logger.getLogger(JakartaLanguageServer.class.getName());

  private final WorkspaceService workspaceService;
  private final TextDocumentService textDocumentService;

  private JakartaLanguageClientAPI languageClient;


  public JakartaLanguageServer() {
    // Workspace service handles workspace settings changes and calls update settings. 
    workspaceService = new JakartaWorkspaceService(this);
    textDocumentService = new JakartaTextDocumentService(this);
  }


  @Override
  public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
    LOGGER.info("Initializing Jakarta EE server");
    this.parentProcessId = params.getProcessId();
    ServerCapabilities serverCapabilities = new ServerCapabilities();
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
    // TODO Auto-generated method stub
    return null;
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
