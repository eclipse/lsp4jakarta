/*******************************************************************************
* Copyright (c) 2020, 2023 Red Hat Inc. and others.
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
import org.eclipse.lsp4jakarta.jdt.internal.core.java.codeaction.JavaCodeActionDefinition;
import org.eclipse.lsp4jakarta.jdt.internal.core.java.completion.JavaCompletionDefinition;
import org.eclipse.lsp4jakarta.jdt.internal.core.java.diagnostics.JavaDiagnosticsDefinition;

/**
 * Registry to hold the extension point
 * "org.eclipse.lsp4mp.jdt.core.javaFeaturesParticipants".
 * 
 * Based on:
 * https://github.com/eclipse/lsp4mp/blob/0.9.0/microprofile.jdt/org.eclipse.lsp4mp.jdt.core/src/main/java/org/eclipse/lsp4mp/jdt/internal/core/java/JavaFeaturesRegistry.java
 *
 */
public class JavaFeaturesRegistry {

	private static final String EXTENSION_JAVA_FEATURE_PARTICIPANTS = "javaFeatureParticipants";

	private static final String CODEACTION_ELT = "codeAction";
	private static final String COMPLETION_ELT = "completion";
	private static final String DIAGNOSTICS_ELT = "diagnostics";

	private static final Logger LOGGER = Logger.getLogger(JavaFeaturesRegistry.class.getName());

	private static final JavaFeaturesRegistry INSTANCE = new JavaFeaturesRegistry();

	private final List<JavaCompletionDefinition> javaCompletionDefinitions;

	private final List<JavaDiagnosticsDefinition> javaDiagnosticsDefinitions;

	private final List<JavaCodeActionDefinition> javaCodeActionDefinitions;

	private boolean javaFeatureDefinitionsLoaded;

	public static JavaFeaturesRegistry getInstance() {
		return INSTANCE;
	}

	public JavaFeaturesRegistry() {
		javaFeatureDefinitionsLoaded = false;

		javaCompletionDefinitions = new ArrayList<>();
		javaDiagnosticsDefinitions = new ArrayList<>();
		javaCodeActionDefinitions = new ArrayList<>();
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
			case CODEACTION_ELT: {
				JavaCodeActionDefinition definition = new JavaCodeActionDefinition(ce);
				synchronized (javaCodeActionDefinitions) {
					javaCodeActionDefinitions.add(definition);
				}
				break;
			}
			case COMPLETION_ELT: {
				JavaCompletionDefinition definition = new JavaCompletionDefinition(ce);
				synchronized (javaCompletionDefinitions) {
					javaCompletionDefinitions.add(definition);
				}
				break;
			}
			case DIAGNOSTICS_ELT: {
				JavaDiagnosticsDefinition definition = new JavaDiagnosticsDefinition(ce);
				synchronized (javaDiagnosticsDefinitions) {
					javaDiagnosticsDefinitions.add(definition);
				}
				break;
			}
			default:
		}
	}

	/**
	 * Returns a list of diagnostics definition.
	 *
	 * @return a list of diagnostics definition.
	 */
	public List<JavaDiagnosticsDefinition> getJavaDiagnosticsDefinitions() {
		loadJavaFeatureDefinitions();
		return javaDiagnosticsDefinitions;
	}

	/**
	 * Returns a list of code action definition.
	 *
	 * @return a list of code action definition.
	 */
	public List<JavaCodeActionDefinition> getJavaCodeActionDefinitions(String codeActionKind) {
		loadJavaFeatureDefinitions();
		return javaCodeActionDefinitions.stream().filter(definition -> codeActionKind.startsWith(definition.getKind()))
				.collect(Collectors.toList());
	}

}
