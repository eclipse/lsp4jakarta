package io.microshed.jakartals.api;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.HoverParams;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.eclipse.lsp4j.services.LanguageClient;

import io.microshed.jakartals.commons.JakartaDiagnosticsParams;

/**
 * API of the client consuming the Jakarta EE Language Server
 * Used to send messages back to the client to ask for information about the Java project 
 * Client then delegates that request to the IDEs built in java language support. 
 */
public interface JakartaLanguageClientAPI extends LanguageClient {
  @JsonRequest("jakarta/java/hover")
	default CompletableFuture<Hover> getJavaHover(HoverParams params) {
		return CompletableFuture.completedFuture(null);
	}

	@JsonRequest("jakarta/java/diagnostics")
	default CompletableFuture<List<PublishDiagnosticsParams>> getJavaDiagnostics(
			JakartaDiagnosticsParams javaParams) {
		return CompletableFuture.completedFuture(null);
	}
}
