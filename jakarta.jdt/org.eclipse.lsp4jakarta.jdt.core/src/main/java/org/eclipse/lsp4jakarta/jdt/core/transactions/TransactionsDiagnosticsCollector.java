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

import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4jakarta.jdt.core.AbstractDiagnosticsCollector;

public class TransactionsDiagnosticsCollector extends AbstractDiagnosticsCollector {

    public TransactionsDiagnosticsCollector() {
        super();
    }

    @Override
    protected String getDiagnosticSource() {
        return DIAGNOSTIC_SOURCE;
    }

    @Override
    public void collectDiagnostics(ICompilationUnit unit, List<Diagnostic> diagnostics) {

    }

}