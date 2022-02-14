/******************************************************************************* 
 * Copyright (c) 2022 Giancarlo Pernudi Segura and others. 
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v. 2.0 which is available at 
 * http://www.eclipse.org/legal/epl-2.0. 
 * 
 * SPDX-License-Identifier: EPL-2.0 
 * 
 * Contributors: 
 *     Giancarlo Pernudi Segura - initial API and implementation 
 *******************************************************************************/

package org.eclipse.lsp4jakarta.jdt.core.websocket;
import org.eclipse.lsp4jakarta.jdt.core.Diagnostic;
import org.eclipse.lsp4jakarta.jdt.core.DiagnosticsCollector;
import org.eclipse.lsp4jakarta.jdt.core.ICompilationUnit;

import java.util.List;


public class WebSocketDiagnosticsCollector implements DiagnosticsCollector {
    @Override
    public void completeDiagnostic(Diagnostic diagnostic) {

    }

    @Override
    public void collectDiagnostics(ICompilationUnit unit, List<Diagnostic> diagnostics) {

    }
}
