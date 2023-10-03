package org.eclipse.lsp4jakarta.jdt.internal.jsonb;

import org.eclipse.lsp4jakarta.jdt.core.java.diagnostics.IJavaErrorCode;

/**
 * JSON-B diagnostics Error code.
 */
public enum ErrorCode implements IJavaErrorCode {
	InvalidNumerOfJsonbCreatorAnnotationsInClass,
	InvalidJSonBindindAnnotationWithJsonbTransientOnField,
	InvalidJSonBindindAnnotationWithJsonbTransientOnAccessor;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getCode() {
		return name();
	}
}
