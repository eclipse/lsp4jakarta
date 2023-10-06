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

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;

/**
 * Wrapper class around <code>IProjectLabelProvider</code>
 * 
 * Based on: https://github.com/eclipse/lsp4mp/blob/0.9.0/microprofile.jdt/org.eclipse.lsp4mp.jdt.core/src/main/java/org/eclipse/lsp4mp/jdt/core/ProjectLabelDefinition.java
 */
public class ProjectLabelDefinition {
	private static final Logger LOGGER = Logger.getLogger(ProjectLabelDefinition.class.getName());
	private final IProjectLabelProvider projectLabelProvider;

	public ProjectLabelDefinition(IProjectLabelProvider projectLabelProvider) {
		this.projectLabelProvider = projectLabelProvider;
	}

	/**
	 * Returns a list of project labels ("maven", "jakarta", etc.) for the
	 * given <code>project</code>
	 * 
	 * @param project the Java project
	 * @return a list of project labels for the given <code>project</code>
	 */
	public List<String> getProjectLabels(IJavaProject project) {
		try {
			return projectLabelProvider.getProjectLabels(project);
		} catch (JavaModelException e) {
			LOGGER.log(Level.SEVERE, "Error while getting project labels", e);
			return Collections.emptyList();
		}
	}
}
