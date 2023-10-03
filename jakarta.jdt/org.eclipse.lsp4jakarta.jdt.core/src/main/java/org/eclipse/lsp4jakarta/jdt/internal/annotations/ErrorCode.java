package org.eclipse.lsp4jakarta.jdt.internal.annotations;

import org.eclipse.lsp4jakarta.jdt.core.java.diagnostics.IJavaErrorCode;

/**
 * Annotations diagnostics Error code.
 */
public enum ErrorCode implements IJavaErrorCode {
	InvalidDateFormat,
	MissingResourceNameAttribute,
	MissingResourceTypeAttribute,
	PostConstructParams,
	PostConstructReturnType,
	PostConstructException,
	PreDestroyParams,
	PreDestroyStatic,
	PreDestroyException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getCode() {
		return name();
	}

}
