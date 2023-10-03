package org.eclipse.lsp4jakarta.jdt.internal.servlet;

import org.eclipse.lsp4jakarta.jdt.core.java.diagnostics.IJavaErrorCode;

/**
 * Persistence diagnostics Error code.
 */
public enum ErrorCode implements IJavaErrorCode {
	ClassWebFilterAnnotatedNoFilterInterfaceImpl,
	WebFilterAnnotationMissingAttributes,
	WebFilterAnnotationAttributeConflict,
	WebFilterAnnotatedClassReqIfaceNoImpl,
	WebServletAnnotatedClassDoesNotExtendHttpServlet,
	WebServletAnnotatedClassUnknownSuperTypeDoesNotExtendHttpServlet,
	WebServletAnnotationMissingAttributes,
	WebServletAnnotationAttributeConflict;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getCode() {
		return name();
	}
}
