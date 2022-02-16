/******************************************************************************* 
 * Copyright (c) 2022 IBM Corporation and others. 
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

import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4jakarta.jdt.core.DiagnosticsCollector;
import org.eclipse.lsp4jakarta.jdt.core.websocket.WebSocketConstants;
import org.eclipse.jdt.core.ICompilationUnit;

import java.util.List;

public class WebSocketDiagnosticsCollector implements DiagnosticsCollector {
    public WebSocketDiagnosticsCollector() {
    }

    public void completeDiagnostic(Diagnostic diagnostic) {
        diagnostic.setSource(WebSocketConstants.DIAGNOSTIC_SOURCE);
        diagnostic.setSeverity(WebSocketConstants.SEVERITY);
    }

    public void collectDiagnostics(ICompilationUnit unit, List<Diagnostic> diagnostics) {

    }
}
