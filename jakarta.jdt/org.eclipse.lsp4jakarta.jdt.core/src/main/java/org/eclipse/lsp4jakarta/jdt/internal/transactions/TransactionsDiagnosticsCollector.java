/*******************************************************************************
* Copyright (c) 2022 IBM Corporation.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0.
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     Lana Kang
*******************************************************************************/

package org.eclipse.lsp4jakarta.jdt.internal.transactions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4jakarta.jdt.core.java.diagnostics.IJavaDiagnosticsParticipant;
import org.eclipse.lsp4jakarta.jdt.core.java.diagnostics.JavaDiagnosticsContext;

public class TransactionsDiagnosticsCollector implements IJavaDiagnosticsParticipant {

	public TransactionsDiagnosticsCollector() {
		super();
	}

	protected String getDiagnosticSource() {
		return Constants.DIAGNOSTIC_SOURCE;
	}

	@Override
	public List<Diagnostic> collectDiagnostics(JavaDiagnosticsContext context, IProgressMonitor monitor) {
		// TODO: Implement.
		List<Diagnostic> diagnostics = new ArrayList<>();
		return diagnostics;
	}

}