package org.eclipse.lsp4jakarta.jdt.internal.jaxrs;

import org.eclipse.lsp4jakarta.jdt.core.java.diagnostics.IJavaErrorCode;

/**
 * JAXRS diagnostics Error code.
 */
public enum ErrorCode implements IJavaErrorCode {
	NonPublicResourceMethod,
	ResourceMethodMultipleEntityParams,
	UnusedConstructor,
	AmbiguousConstructors,
	NoPublicConstructors;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getCode() {
		return name();
	}

}
