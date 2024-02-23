/*******************************************************************************
* Copyright (c) 2021, 2024 IBM Corporation.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0.
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     Ananya Rao
*******************************************************************************/
package io.openliberty.sample.jakarta.di;

import io.openliberty.sample.jakarta.di.helpers.ValidManagedBeanDefaultConstructor;
import io.openliberty.sample.jakarta.di.helpers.ValidManagedBeanInjectedConstructor;
import jakarta.inject.Inject;

public class MultipleConstructorWithInject {
    private ValidManagedBeanDefaultConstructor bean1;
    private ValidManagedBeanInjectedConstructor bean2;

    @Inject
    public MultipleConstructorWithInject(ValidManagedBeanDefaultConstructor bean1) {
        this.bean1 = bean1;
    }

    @Inject
    public MultipleConstructorWithInject(ValidManagedBeanInjectedConstructor bean2) {
        this.bean2 = bean2;
    }

    @Inject
    protected MultipleConstructorWithInject(ValidManagedBeanDefaultConstructor bean1, ValidManagedBeanInjectedConstructor bean2) {
        this.bean1 = bean1;
        this.bean2 = bean2;
    }
}
