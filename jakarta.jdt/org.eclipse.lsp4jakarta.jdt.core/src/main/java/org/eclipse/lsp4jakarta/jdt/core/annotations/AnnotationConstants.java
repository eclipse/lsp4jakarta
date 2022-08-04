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
	
	/* @Generated */
	public static final String GENERATED = "Generated";
	public static final String GENERATED_FQ_NAME = "jakarta.annotation.Generated";
	public static final String ISO_8601_REGEX = "^([\\+-]?\\d{4}(?!\\d{2}\\b))((-?)((0[1-9]|1[0-2])(\\3([12]\\d|0[1-9]|3[01]))?|W([0-4]\\d|5[0-2])(-?[1-7])?|(00[1-9]|0[1-9]\\d|[12]\\d{2}|3([0-5]\\d|6[1-6])))([T\\s]((([01]\\d|2[0-3])((:?)[0-5]\\d)?|24\\:?00)([\\.,]\\d+(?!:))?)?(\\17[0-5]\\d([\\.,]\\d+)?)?([zZ]|([\\+-])([01]\\d|2[0-3]):?([0-5]\\d)?)?)?)?$";
	
	/* @Resource */
	public static final String RESOURCE = "Resource";
	public static final String RESOURCE_FQ_NAME = "jakarta.annotation.Resource";
	
	/* @PostConstruct */
	public static final String POST_CONSTRUCT = "PostConstruct";
	public static final String POST_CONSTRUCT_FQ_NAME = "jakarta.annotation.PostConstruct";
	
	/* @PreDesotroy */
	public static final String PRE_DESTROY = "PreDestroy";
	public static final String PRE_DESTROY_FQ_NAME = "jakarta.annotation.PreDestroy";
	
	/* Diagnostics fields constants */
	public static final String DIAGNOSTIC_SOURCE = "jakarta-annotations";
	public static final String DIAGNOSTIC_CODE_DATE_FORMAT = "InvalidDateFormat";
	public static final String DIAGNOSTIC_CODE_RESOURCE_RETURN_TYPE = "IncorrectResourceReturnType";
	public static final String DIAGNOSTIC_CODE_MISSING_RESOURCE_TYPE_ATTRIBUTE = "MissingResourceTypeAttribute";
	public static final String DIAGNOSTIC_CODE_MISSING_RESOURCE_NAME_ATTRIBUTE = "MissingResourceNameAttribute";
	public static final String DIAGNOSTIC_CODE_POSTCONSTRUCT_PARAMS = "PostConstructParams";
	public static final String DIAGNOSTIC_CODE_POSTCONSTRUCT_RETURN_TYPE = "PostConstructReturnType";
	public static final String DIAGNOSTIC_CODE_POSTCONSTRUCT_EXCEPTION = "PostConstructException";
	public static final String DIAGNOSTIC_CODE_PREDESTROY_PARAMS = "PreDestroyParams";
	public static final String DIAGNOSTIC_CODE_PREDESTROY_EXCEPTION = "PreDestroyException";
	public static final String DIAGNOSTIC_CODE_PREDESTROY_STATIC = "PreDestroyStatic";
	public static final DiagnosticSeverity ERROR = DiagnosticSeverity.Error;
	public static final DiagnosticSeverity WARNING = DiagnosticSeverity.Warning;
}
