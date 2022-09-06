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
 *     Yijia Jing
 *******************************************************************************/

package org.eclipse.lsp4jakarta.jdt.core.jsonp;

public class JsonpConstants {

    /* Source */
    public static final String DIAGNOSTIC_SOURCE = "jakarta-jsonp";
    
    /* Constants */
    public static final String CREATE_POINTER = "createPointer";
    public static final String JSON_FQ_NAME = "jakarta.json.Json";
    public static final String DIAGNOSTIC_CODE_CREATE_POINTER = "InvalidCreatePointerArg";
    public static final String CREATE_POINTER_ERROR_MESSAGE = "Json.createPointer target must be a sequence of '/' prefixed tokens or an emtpy String";
}
