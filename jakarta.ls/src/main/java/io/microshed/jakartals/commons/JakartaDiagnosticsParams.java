package io.microshed.jakartals.commons;

import org.eclipse.lsp4mp.commons.DocumentFormat;
import java.util.List;

public class JakartaDiagnosticsParams {
  
	private List<String> uris;

	private DocumentFormat documentFormat;

	public JakartaDiagnosticsParams() {
		this(null);
	}

	public JakartaDiagnosticsParams(List<String> uris) {
		setUris(uris);
	}

	/**
	 * Returns the java file uris list.
	 *
	 * @return the java file uris list.
	 */
	public List<String> getUris() {
		return uris;
	}

	/**
	 * Set the java file uris list.
	 *
	 * @param uris the java file uris list.
	 */
	public void setUris(List<String> uris) {
		this.uris = uris;
	}

	public DocumentFormat getDocumentFormat() {
		return documentFormat;
	}

	public void setDocumentFormat(DocumentFormat documentFormat) {
		this.documentFormat = documentFormat;
	}
}
