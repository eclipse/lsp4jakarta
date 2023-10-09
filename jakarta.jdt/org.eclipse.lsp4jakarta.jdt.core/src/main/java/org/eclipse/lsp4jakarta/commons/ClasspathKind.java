/*******************************************************************************
* Copyright (c) 2019 Red Hat Inc. and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
* which is available at https://www.apache.org/licenses/LICENSE-2.0.
*
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
*
* Contributors:
*     Red Hat Inc. - initial API and implementation
*******************************************************************************/
package org.eclipse.lsp4jakarta.commons;

/**
 * Classpath kind where application.properties is stored:
 *
 * <ul>
 * <li>not in classpath</li>
 * <li>in /java/main/src classpath</li>
 * <li>in /java/main/test classpath</li>
 * </ul>
 *
 * @author Angelo ZERR
 *
 */
public enum ClasspathKind {

    NONE(1), SRC(2), TEST(3);

    private final int value;

    ClasspathKind(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ClasspathKind forValue(int value) {
        ClasspathKind[] allValues = ClasspathKind.values();
        if (value < 1 || value > allValues.length)
            throw new IllegalArgumentException("Illegal enum value: " + value);
        return allValues[value - 1];
    }

}
