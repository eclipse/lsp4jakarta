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
package org.eclipse.lsp4jakarta.jdt.internal.core.java;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.lsp4jakarta.jdt.core.JakartaCorePlugin;

import org.eclipse.lsp4jakarta.jdt.internal.core.java.completion.JavaCompletionDefinition;

/**
 * Registry to hold the extension point
 * "org.eclipse.lsp4mp.jdt.core.javaFeaturesParticipants".
 *
 */
public class JavaFeaturesRegistry {

	private static final String EXTENSION_JAVA_FEATURE_PARTICIPANTS = "javaFeatureParticipants";
	
	private static final String COMPLETION_ELT = "completion";

	private static final Logger LOGGER = Logger.getLogger(JavaFeaturesRegistry.class.getName());

	private static final JavaFeaturesRegistry INSTANCE = new JavaFeaturesRegistry();


	private final List<JavaCompletionDefinition> javaCompletionDefinitions;

	private boolean javaFeatureDefinitionsLoaded;

	public static JavaFeaturesRegistry getInstance() {
		return INSTANCE;
	}

	public JavaFeaturesRegistry() {
		javaFeatureDefinitionsLoaded = false;

		javaCompletionDefinitions = new ArrayList<>();
	}

	/**
	 * Returns a list of completion definition
	 *
	 * @return a list of completion definition
	 */
	public List<JavaCompletionDefinition> getJavaCompletionDefinitions() {
		loadJavaFeatureDefinitions();
		return javaCompletionDefinitions;
	}

	private synchronized void loadJavaFeatureDefinitions() {
		if (javaFeatureDefinitionsLoaded)
			return;

		// Immediately set the flag, as to ensure that this method is never
		// called twice
		javaFeatureDefinitionsLoaded = true;

		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(JakartaCorePlugin.PLUGIN_ID,
				EXTENSION_JAVA_FEATURE_PARTICIPANTS);
		addJavaFeatureDefinition(cf);
	}
	
	private void addJavaFeatureDefinition(IConfigurationElement[] cf) {
		for (IConfigurationElement ce : cf) {
			try {
				createAndAddDefinition(ce);
			} catch (Throwable t) {
				LOGGER.log(Level.SEVERE, "Error while collecting java features extension contributions", t);
			}
		}
	}
	
	private void createAndAddDefinition(IConfigurationElement ce) throws CoreException {
		switch (ce.getName()) {
		case COMPLETION_ELT: {
			JavaCompletionDefinition definition = new JavaCompletionDefinition(ce);
			synchronized (javaCompletionDefinitions) {
				javaCompletionDefinitions.add(definition);
			}
			break;
		}
		default:
		}
	}

}
