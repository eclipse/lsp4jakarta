package org.jakarta.lsp4e;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.lsp4e.LanguageClientImpl;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.HoverParams;
import org.eclipse.lsp4j.MarkedString;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.CancelChecker;
import org.eclipse.lsp4j.jsonrpc.CompletableFutures;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import io.microshed.jakartals.api.JakartaLanguageClientAPI;
import io.microshed.jakartals.commons.JakartaDiagnosticsParams;

public class JakartaLanguageClient extends LanguageClientImpl implements JakartaLanguageClientAPI {
    
    public JakartaLanguageClient() {
        // do nothing
    }
    
    private IProgressMonitor getProgressMonitor(CancelChecker cancelChecker) {
        IProgressMonitor monitor = new NullProgressMonitor() {
            public boolean isCanceled() {
                cancelChecker.checkCanceled();
                return false;
            };
        };
        return monitor;
    }
    
    @Override
    public CompletableFuture<Hover> getJavaHover(HoverParams params) {
    	// return dummy test hover object
    	Activator.log(new Status(IStatus.INFO, "hover request received", "hover request received"));
    	return CompletableFutures.computeAsync((cancelChecker) -> {
    	    IProgressMonitor monitor = getProgressMonitor(cancelChecker);
    	    Hover testHover = new Hover();
    	    List<Either<String, MarkedString>> contents = new ArrayList<>();
    	    contents.add(Either.forLeft("this is test hover"));
    	    testHover.setContents(contents);
    	    return testHover;
    	 });
    }

    @Override
    public CompletableFuture<List<PublishDiagnosticsParams>> getJavaDiagnostics(
            JakartaDiagnosticsParams javaParams) {
        Activator.log(new Status(IStatus.INFO, "diagnostic request received", "diagnostic request receieved"));
        
        // creating a test diagnostic
        return CompletableFutures.computeAsync((cancelChecker) -> {
            IProgressMonitor monitor = getProgressMonitor(cancelChecker);
                List<PublishDiagnosticsParams> publishDiagnostics = new ArrayList<PublishDiagnosticsParams>();
                List<String> uris = javaParams.getUris();
                for (String uri : uris) {
                    List<Diagnostic> diagnostics = new ArrayList<>();
                    Range diagRange = new Range(new Position(2, 4), new Position(2, 10));
                    diagnostics.add(new Diagnostic(diagRange, "testDiagnostic"));
                    PublishDiagnosticsParams publishDiagnostic = new PublishDiagnosticsParams(uri, diagnostics);
                    publishDiagnostics.add(publishDiagnostic);
                }

                return publishDiagnostics;
        });
    }

}
