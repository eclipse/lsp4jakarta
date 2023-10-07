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
package org.eclipse.lsp4jakarta.jdt.internal.core.java.diagnostics;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4jakarta.jdt.core.java.diagnostics.IJavaDiagnosticsParticipant;
import org.eclipse.lsp4jakarta.jdt.core.java.diagnostics.JavaDiagnosticsContext;
import org.eclipse.lsp4jakarta.jdt.internal.core.java.AbstractJavaFeatureDefinition;

/**
 * Wrapper class around java participants {@link IJavaDiagnosticsParticipant}.
 * 
 * Based on:
 * https://github.com/eclipse/lsp4mp/blob/0.9.0/microprofile.jdt/org.eclipse.lsp4mp.jdt.core/src/main/java/org/eclipse/lsp4mp/jdt/internal/core/java/diagnostics/JavaDiagnosticsDefinition.java
 */
public class JavaDiagnosticsDefinition extends AbstractJavaFeatureDefinition<IJavaDiagnosticsParticipant>
		implements IJavaDiagnosticsParticipant {
	private static final Logger LOGGER = Logger.getLogger(JavaDiagnosticsDefinition.class.getName());

	public JavaDiagnosticsDefinition(IConfigurationElement element) {
		super(element);
	}

	// -------------- Diagnostics

	@Override
	public boolean isAdaptedForDiagnostics(JavaDiagnosticsContext context, IProgressMonitor monitor) {
		try {
			return getParticipant().isAdaptedForDiagnostics(context, monitor);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error while calling isAdaptedForDiagnostics", e);
			return false;
		}
	}

	@Override
	public void beginDiagnostics(JavaDiagnosticsContext context, IProgressMonitor monitor) {
		try {
			getParticipant().beginDiagnostics(context, monitor);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error while calling beginDiagnostics", e);
		}
	}

	@Override
	public List<Diagnostic> collectDiagnostics(JavaDiagnosticsContext context, IProgressMonitor monitor) {
		try {
			return getParticipant().collectDiagnostics(context, monitor);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error while collecting diagnostics", e);
			return null;
		}
	}

	@Override
	public void endDiagnostics(JavaDiagnosticsContext context, IProgressMonitor monitor) {
		try {
			getParticipant().endDiagnostics(context, monitor);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error while calling endDiagnostics", e);
		}
	}

}
