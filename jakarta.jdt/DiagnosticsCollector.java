/*******************************************************************************
* Copyright (c) 2020 IBM Corporation, Pengyu Xiong and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0.
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     IBM Corporation, Pengyu Xiong - initial API and implementation
*******************************************************************************/

package org.jakarta.jdt;

import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.lsp4j.Diagnostic;

/**
 * Diagnostics Collector interface
 * @author Pengyu Xiong
 *
 */
public interface DiagnosticsCollector {
    public void completeDiagnostic(Diagnostic diagnostic);

    public void collectDiagnostics(ICompilationUnit unit, List<Diagnostic> diagnostics);
}
