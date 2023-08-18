package org.eclipse.lsp4jakarta.ls.api;

import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;
import org.eclipse.lsp4jakarta.commons.JakartaJavaCompletionParams;
import org.eclipse.lsp4jakarta.commons.JakartaJavaCompletionResult;

/**
 * Interface for MicroProfile specific completion in Java files
 *
 * @author datho7561
 */
public interface JakartaJavaCompletionProvider {

	@JsonRequest("jakarta/java/completion")
	CompletableFuture<JakartaJavaCompletionResult> getJavaCompletion(JakartaJavaCompletionParams javaParams);

}
