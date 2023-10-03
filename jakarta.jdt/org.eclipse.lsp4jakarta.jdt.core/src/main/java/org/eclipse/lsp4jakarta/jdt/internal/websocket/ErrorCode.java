package org.eclipse.lsp4jakarta.jdt.internal.websocket;

import org.eclipse.lsp4jakarta.jdt.core.java.diagnostics.IJavaErrorCode;

/**
 * WebSockets diagnostics Error code.
 */
public enum ErrorCode implements IJavaErrorCode {
	InvalidOnOpenParams,
	InvalidOnCloseParams,
	PathParamsMissingFromParam,
	PathParamDoesNotMatchEndpointURI,
	OnMessageDuplicateMethod,
	InvalidEndpointPathWithNoStartingSlash,
	InvalidEndpointPathWithRelativePaths,
	InvalidEndpointPathNotTempleateOrPartialURI,
	InvalidEndpointPathDuplicateVariable;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getCode() {
		return name();
	}
}
