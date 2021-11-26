/*******************************************************************************
* Copyright (c) 2021 IBM Corporation.
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

import jakarta.inject.Inject;

public class productIdentity{
    private int productNum;
    private String productDesc;
	
    @Inject
    public productIdentity(int productNum) {
        this.productNum = productNum;
	}
    @Inject
    public productIdentity(String productDesc) {
        this.productDesc = productDesc;
	}

    @Inject
    protected productIdentity(int productNum, String productDesc) {
        this.productNum = productNum;
        this.productDesc = productDesc;
	}
}

