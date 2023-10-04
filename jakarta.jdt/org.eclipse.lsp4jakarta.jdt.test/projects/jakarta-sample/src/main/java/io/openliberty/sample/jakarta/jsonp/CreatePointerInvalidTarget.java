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
package io.openliberty.sample.jakarta.jsonp;

import jakarta.json.Json;
import jakarta.json.JsonPointer;

public class CreatePointerInvalidTarget {

    public static void makePointers() {
        JsonPointer doubleSlashPointer = Json.createPointer("//");
        JsonPointer noSlashPrefixPointer = Json.createPointer("name/1");
        JsonPointer slashSuffixPointer = Json.createPointer("/skills/languages/");
    }
}
