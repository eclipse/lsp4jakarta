package org.eclipse.lsp4jakarta.jdt.internal.jsonp;

import org.eclipse.lsp4jakarta.jdt.core.java.diagnostics.IJavaErrorCode;

/**
 * JSON-P diagnostics Error code.
 */
public enum ErrorCode implements IJavaErrorCode {
	InvalidJsonCreatePointerTarget;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getCode() {
		return name();
	}
}
