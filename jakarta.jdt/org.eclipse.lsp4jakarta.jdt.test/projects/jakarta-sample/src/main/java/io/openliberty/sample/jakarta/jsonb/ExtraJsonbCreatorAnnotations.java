/*******************************************************************************
 * Copyright (c) 2021 IBM Corporation and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package io.openliberty.sample.jakarta.jsonb;

import jakarta.json.bind.annotation.JsonbCreator;

public class ExtraJsonbCreatorAnnotations {
    @JsonbCreator
    public ExtraJsonbCreatorAnnotations() {}
    
    @JsonbCreator
    private static ExtraJsonbCreatorAnnotations factoryMethod() {
        return null;
    }
}