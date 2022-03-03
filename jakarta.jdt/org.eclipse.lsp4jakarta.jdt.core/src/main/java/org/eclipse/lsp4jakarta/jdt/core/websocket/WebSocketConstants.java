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

import org.eclipse.lsp4j.DiagnosticSeverity;

public class WebSocketConstants {
    public static final String DIAGNOSTIC_SOURCE = "jakarta-websocket";
    public static final DiagnosticSeverity SEVERITY = DiagnosticSeverity.Error;
    public static final DiagnosticSeverity WARNING = DiagnosticSeverity.Warning;
    
    public static final String DIAGNOSTIC_ERR_MSG = "Cannot calculate WebSocket diagnostics";
    
    public static final String PATHPARAM_VALUE_WARN_MSG = "PathParam value does not match specified Endpoint URI";
    
    public static final String WEBSOCKET_SERVER_ANNOTATION = "ServerEndpoint";
    public static final String WEBSOCKET_CLIENT_ANNOTATION = "ClientEndpoint";
    
    public static final String ANNOTATION_VALUE = "value";
    
    public static final String URI_SEPARATOR = "/";
    public static final String CURLY_BRACE_START = "{";
    public static final String CURLY_BRACE_END = "}";
}
