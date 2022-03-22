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
 *     Adit Rada, Yijia Jing - initial API and implementation
 *******************************************************************************/

package io.openliberty.sample.jakarta.jsonb;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsobTransient;
import jakarta.json.bind.annotation.JsonbProperty;

public class JsonbTransientDiagnostic {
    @JsonbProperty("name")
    @JsonbTransient
    private String name;

    @JsonbTransient
    private int id;

    @JsonbProperty("fav_lang")
    private String favoriteLanguage;
    
    @JsonbProperty("person-id")
    private int getId() { 
        // A diagnostic is expected on getId because as a getter, it is annotated with other 
        // Jsonb annotations while its corresponding field id is annotated with JsonbTransient
        return id;
    }
    
    @JsonbCreator
    private void setId(int id) {
        // A diagnostic is expected on setId because as a setter, it is annotated with other 
        // Jsonb annotations while its corresponding field id is annotated with JsonbTransient
        this.id = id;
    }
}
