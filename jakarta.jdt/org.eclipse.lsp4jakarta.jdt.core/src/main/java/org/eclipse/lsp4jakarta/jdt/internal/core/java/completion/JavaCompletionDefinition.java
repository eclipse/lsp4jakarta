/*******************************************************************************
* Copyright (c) 2021, 2023 Red Hat Inc. and others.
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
package org.eclipse.lsp4jakarta.jdt.internal.core.java.completion;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4jakarta.jdt.core.java.completion.IJavaCompletionParticipant;
import org.eclipse.lsp4jakarta.jdt.core.java.completion.JavaCompletionContext;
import org.eclipse.lsp4jakarta.jdt.internal.core.java.AbstractJavaFeatureDefinition;

/**
 * Wrap the completion participant in try/catch
 * 
 * Based on:
 * https://github.com/eclipse/lsp4mp/blob/0.9.0/microprofile.jdt/org.eclipse.lsp4mp.jdt.core/src/main/java/org/eclipse/lsp4mp/jdt/internal/core/java/completion/JavaCompletionDefinition.java
 *
 * @author datho7561
 */
public class JavaCompletionDefinition extends AbstractJavaFeatureDefinition<IJavaCompletionParticipant>
		implements IJavaCompletionParticipant {

	private static final Logger LOGGER = Logger.getLogger(JavaCompletionDefinition.class.getName());

	public JavaCompletionDefinition(IConfigurationElement element) {
		super(element);
	}

	// -------------- Completion

	@Override
	public boolean isAdaptedForCompletion(JavaCompletionContext context, IProgressMonitor monitor) {
		try {
			return getParticipant().isAdaptedForCompletion(context, monitor);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error while calling isAdaptedForCompletion", e);
			return false;
		}
	}

	@Override
	public List<? extends CompletionItem> collectCompletionItems(JavaCompletionContext context,
			IProgressMonitor monitor) {
		try {
			List<? extends CompletionItem> completionItems = getParticipant().collectCompletionItems(context, monitor);
			return completionItems != null ? completionItems : Collections.emptyList();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error while calling collectCompletionItems", e);
			return Collections.emptyList();
		}
	}
}
