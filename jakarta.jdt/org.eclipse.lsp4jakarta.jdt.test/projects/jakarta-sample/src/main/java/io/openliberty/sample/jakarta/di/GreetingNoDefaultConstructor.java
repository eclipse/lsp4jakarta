/*******************************************************************************
* Copyright (c) 2021, 2024 IBM Corporation and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0.
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     IBM Corporation - initial implementation
*******************************************************************************/
package io.openliberty.sample.jakarta.di;

public class GreetingNoDefaultConstructor {

    private String greeting;

    public GreetingNoDefaultConstructor(String greeting) {
        this.greeting = greeting;
    }

    public String greet(String name) {
        return greeting + " " + name;
    }
}