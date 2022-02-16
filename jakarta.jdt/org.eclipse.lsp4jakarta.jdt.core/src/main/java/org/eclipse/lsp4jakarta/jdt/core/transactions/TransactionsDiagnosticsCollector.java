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

package org.eclipse.lsp4jakarta.jdt.core.transactions;

import static org.eclipse.lsp4jakarta.jdt.core.transactions.TransactionsConstants.DIAGNOSTIC_SOURCE;
import static org.eclipse.lsp4jakarta.jdt.core.transactions.TransactionsConstants.SEVERITY;

import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4jakarta.jdt.core.DiagnosticsCollector;

public class TransactionsDiagnosticsCollector implements DiagnosticsCollector {
	
    public void completeDiagnostic(Diagnostic diagnostic) {
        diagnostic.setSource(DIAGNOSTIC_SOURCE);
        diagnostic.setSeverity(SEVERITY);
    }

    public void collectDiagnostics(ICompilationUnit unit, List<Diagnostic> diagnostics) {

    }

}