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

import jakarta.inject.Inject;
import jakarta.enterprise.inject.Produces;

import java.util.ArrayList;
import java.util.List;

public abstract class GreetingServlet {

    /**
     * UID.
     */
    private static final long serialVersionUID = 1L;

    // d1: test code for @Inject fields cannot be final
    @Inject
    private final Greeting greeting = new Greeting();

    @Produces
    public GreetingNoDefaultConstructor getInstance() {
        return new GreetingNoDefaultConstructor("Howdy");
    }

    // d2: test code for @Inject methods cannot be final
    @Inject
    public final void injectFinal() {
        return;
    }

    // d3: test code for @Inject methods cannot be abstract
    @Inject
    public abstract void injectAbstract();

    // d4: test code for @Inject methods cannot be static
    @Inject
    public static void injectStatic() {
        return;
    }

    // d5: test code for @Inject methods cannot be generic
    @Inject
    public <T> List<T> injectGeneric(T arg) {
        return new ArrayList<T>();
    };
}
