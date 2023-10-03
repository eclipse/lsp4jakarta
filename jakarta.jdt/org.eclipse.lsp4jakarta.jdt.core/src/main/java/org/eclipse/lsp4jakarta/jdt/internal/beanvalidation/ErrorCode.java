package org.eclipse.lsp4jakarta.jdt.internal.beanvalidation;

import org.eclipse.lsp4jakarta.jdt.core.java.diagnostics.IJavaErrorCode;

/**
 * Bean validation diagnostics Error code.
 */
public enum ErrorCode implements IJavaErrorCode {
	InvalidConstrainAnnotationOnStaticMethodOrField,
	InvalidAnnotationOnNonBooleanMethodOrField,
	InvalidAnnotationOnNonBigDecimalCharByteShortIntLongMethodOrField,
	InvalidAnnotationOnNonDateTimeMethodOrField,
	InvalidAnnotationOnNonMinMaxMethodOrField,
	InvalidAnnotationOnNonPositiveMethodOrField,
	InvalidAnnotationOnNonStringMethodOrField;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getCode() {
		return name();
	}

}
