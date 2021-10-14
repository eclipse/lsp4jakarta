/*******************************************************************************
* Copyright (c) 2021 IBM Corporation and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0.
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     IBM Corporation - initial API and implementation
*******************************************************************************/

package org.eclipse.lsp4jakarta.jdt.core.annotations;

import org.eclipse.lsp4j.DiagnosticSeverity;

public class AnnotationConstants {

	/* @Resource */
	public static final String RESOURCE = "Resource";
	
	/* @PostConstruct */
	public static final String POST_CONSTRUCT = "PostConstruct";
	
	/* @PreDesotroy */
	public static final String PRE_DESTROY = "PreDestroy";
	
	/* Diagnostics fields constants */
	public static final String DIAGNOSTIC_SOURCE = "jakarta-annotations";
	public static final String DIAGNOSTIC_CODE_RESOURCE_RETURN_TYPE = "IncorrectResourceReturnType";
	public static final String DIAGNOSTIC_CODE_MISSING_RESOURCE_TYPE_ATTRIBUTE = "MissingResourceTypeAttribute";
	public static final String DIAGNOSTIC_CODE_MISSING_RESOURCE_NAME_ATTRIBUTE = "MissingResourceNameAttribute";
	public static final String DIAGNOSTIC_CODE_POSTCONSTRUCT_PARAMS = "PostConstructParams";
	public static final String DIAGNOSTIC_CODE_POSTCONSTRUCT_RETURN_TYPE = "PostConstructReturnType";
	public static final String DIAGNOSTIC_CODE_POSTCONSTRUCT_EXCEPTION = "PostConstructException";
	public static final String DIAGNOSTIC_CODE_PREDESTROY_PARAMS = "PreDestroyParams";
	public static final String DIAGNOSTIC_CODE_PREDESTROY_EXCEPTION = "PreDestroyException";
	public static final String DIAGNOSTIC_CODE_PREDESTROY_STATIC = "PreDestroyStatic";
	public static final DiagnosticSeverity SEVERITY = DiagnosticSeverity.Error;
}
