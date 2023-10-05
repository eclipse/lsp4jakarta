/*******************************************************************************
* Copyright (c) 2023 Red Hat Inc. and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
* which is available at https://www.apache.org/licenses/LICENSE-2.0.
*
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
*
* Contributors:
*     Red Hat Inc. - initial API and implementation
*******************************************************************************/
package org.eclipse.lsp4jakarta.commons.codeaction;

/**
 * Represents id of an lsp4mp code action.
 */
public enum JakartaCodeActionId implements ICodeActionId {
	IgnoreUnknownProperty,
	// JAXRS
	jaxrsInsertPublicCtrtToClass,
	MakeConstructorPublic,
	MakeMethodPublic,
	RemoveAllEntityParametersExcept,
	// Annotations
	ChangeReturnTypeToVoid,
	InsertResourceAnnotationTypeAttribute,
	InsertResourceAnnotationNameAttribute,
	RemoveAllParameters,
	RemoveAnnotationPreDestroy,
	RemoveAnnotationPostConstruct,
	AnnotationRemoveStaticModifier,
	// Bean validation
	RemoveConstraintAnnotation,
	BBRemoveStaticModifier,
	// Dependency injection
	DIRemoveInjectAnnotation,
	DIRemoveFinalModifier,
	DIRemoveAbstractModifier,
	DIRemoveStaticModifier,
	// JSON-B
	JSONBRemoveJsonbCreatorAnnotation,
	JSONBRemoveJsonbTransientAnnotation,
	JSONBRemoveAllButJsonbTransientAnnotation,
	// Persistence
	PersistenceRemoveFinalModifier,
	PersistenceRemoveMapKeyAnnotation,
	PersistenceInsertAttributesToMKJCAnnotation,
	PersistenceInsertPublicCtrtToClass,
	PersistenceInsertProtectedCtrtToClass,
	// WebSockets
	WBInsertPathParamAnnotationWithValueAttrib,
	// Servlet
	ServletCompleteWebFilterAnnotation,
	ServletCompleteServletAnnotation,
	ServletFilterImplementation,
	ServletExtendClass,
	ServletListenerImplementation,
	// CDI
	CDIRemoveProducesAndInjectAnnotations,
	CDIInsertInjectAnnotation,
	CDIInsertProtectedCtrtToClass,
	CDIInsertPublicCtrtToClass,
	CDIRemoveInvalidInjectAnnotations,
	CDIRemoveProducesAnnotation,
	CDIRemoveInjectAnnotation,
	CDIRemoveScopeDeclarationAnnotationsButOne,
	CDIReplaceScopeAnnotations;

	@Override
	public String getId() {
		return name();
	}
}