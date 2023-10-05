package org.eclipse.lsp4jakarta.jdt.internal.cdi;

import org.eclipse.lsp4jakarta.jdt.core.java.diagnostics.IJavaErrorCode;

/**
 * Contexts and Dependency Injection (CDI) error code.
 */
public enum ErrorCode implements IJavaErrorCode {
	InvalidNumberOfScopedAnnotationsByManagedBean,
	InvalidManagedBeanWithNonStaticPublicField,
	InvalidNumberOfScopeAnnotationsByProducerField,
	InvalidFieldWithProducesAndInjectAnnotations,
	InvalidNumberOfScopeAnnotationsByProducerMethod,
	InvalidMethodWithProducesAndInjectAnnotations,
	InvalidManagedBeanWithInvalidConstructor,
	InvalidGenericManagedBeanClassWithNoDependentScope,
	InvalidDisposesAnnotationOnMultipleMethodParams,
	InvalidDisposerMethodParamAnnotation,
	InvalidProducerMethodParamAnnotation,
	InvalidInjectAnnotatedMethodParamAnnotation;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getCode() {
		return name();
	}

}
