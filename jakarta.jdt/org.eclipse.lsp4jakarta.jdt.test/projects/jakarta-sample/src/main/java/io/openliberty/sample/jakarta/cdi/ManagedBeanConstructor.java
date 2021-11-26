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
*     Hani Damlaj
*******************************************************************************/

package io.openliberty.sample.jakarta.cdi;
                                           
import jakarta.enterprise.context.Dependent;

@Dependent
public class ManagedBeanConstructor {
	private int a;
	
	public ManagedBeanConstructor(int a) {
		this.a = a;
	}
}
