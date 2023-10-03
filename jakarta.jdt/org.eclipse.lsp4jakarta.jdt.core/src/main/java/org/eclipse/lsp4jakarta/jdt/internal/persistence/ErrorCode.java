package org.eclipse.lsp4jakarta.jdt.internal.persistence;

import org.eclipse.lsp4jakarta.jdt.core.java.diagnostics.IJavaErrorCode;

/**
 * Persistence diagnostics Error code.
 */
public enum ErrorCode implements IJavaErrorCode {
	InvalidFinalMethodInEntityAnnotatedClass,
	InvalidPersistentFieldInEntityAnnotatedClass,
	InvalidConstructorInEntityAnnotatedClass,
	InvalidFinalModifierOnEntityAnnotatedClass,
	InvalidMapKeyAnnotationsOnSameMethod,
	InvalidMapKeyAnnotationsOnSameField,
	InvalidMethodWithMultipleMPJCAnnotations,
	InvalidFieldWithMultipleMPJCAnnotations;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getCode() {
		return name();
	}
}
