/*******************************************************************************
* Copyright (c) 2012, 2024 IBM Corporation and others.
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

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Greeting {

    public String greet(String name) {
        return "Hello, " + name;
    }

    public class Message {
        public String title;
        public String content;

        public Message() {
            title = "";
            content = "";
        }
    }
}