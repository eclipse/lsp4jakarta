/*******************************************************************************
* Copyright (c) 2020 Red Hat Inc. and others.
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
package org.eclipse.lsp4jakarta.jdt.core;

/**
 * MicroProfile Config constants
 *
 * @author Angelo ZERR
 *
 */
public class JakartaConfigConstants {

	private JakartaConfigConstants() {
	}

	public static final String MICRO_PROFILE_CONFIG_DIAGNOSTIC_SOURCE = "microprofile-config";

	public static final String INJECT_JAVAX_ANNOTATION = "javax.inject.Inject";

	public static final String INJECT_JAKARTA_ANNOTATION = "jakarta.inject.Inject";

	// @ConfigProperty annotation

	public static final String CONFIG_PROPERTY_ANNOTATION = "org.eclipse.microprofile.config.inject.ConfigProperty";

	public static final String CONFIG_PROPERTY_ANNOTATION_NAME = "name";

	public static final String CONFIG_PROPERTY_ANNOTATION_DEFAULT_VALUE = "defaultValue";

	// @ConfigProperties annotation

	public static final String CONFIG_PROPERTIES_ANNOTATION = "org.eclipse.microprofile.config.inject.ConfigProperties";

	public static final String CONFIG_PROPERTIES_ANNOTATION_PREFIX = "prefix";

	public static final String CONFIG_PROPERTIES_ANNOTATION_UNCONFIGURED_PREFIX = "org.eclipse.microprofile.config.inject.configproperties.unconfiguredprefix";

	// @Asynchronous annotation

	public static final String FUTURE_TYPE_UTILITY = "java.util.concurrent.Future";

	public static final String COMPLETION_STAGE_TYPE_UTILITY = "java.util.concurrent.CompletionStage";

	// Diagnostic data

	public static final String DIAGNOSTIC_DATA_NAME = "name";

	public static final String UNI_TYPE_UTILITY = "io.smallrye.mutiny.Uni";
	
	// Jakarta
	public static final String JAKARTA_RS_GET = "jakarta.ws.rs.GET";
	

}
