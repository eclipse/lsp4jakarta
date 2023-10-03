package org.eclipse.lsp4jakarta.jdt.internal.di;

import org.eclipse.lsp4jakarta.jdt.core.java.diagnostics.IJavaErrorCode;

/**
 * Bean validation diagnostics Error code.
 */
public enum ErrorCode implements IJavaErrorCode {
	InvalidInjectAnnotationOnFinalField,
	InvalidInjectAnnotationOnFinalMethod,
	InvalidInjectAnnotationOnAbstractMethod,
	InvalidInjectAnnotationOnStaticMethod,
	InvalidInjectAnnotationOnGenericMethod,
	InvalidInjectAnnotationOnMultipleConstructors;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getCode() {
		return name();
	}

}
