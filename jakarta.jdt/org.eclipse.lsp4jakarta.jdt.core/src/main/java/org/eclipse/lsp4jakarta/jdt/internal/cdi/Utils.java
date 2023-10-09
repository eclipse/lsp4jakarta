/*******************************************************************************
 * Copyright (c) 2021, 2023 IBM Corporation and others.
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
package org.eclipse.lsp4jakarta.jdt.internal.cdi;

import static org.eclipse.lsp4jakarta.jdt.internal.cdi.Constants.SCOPES;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.IAnnotatable;
import org.eclipse.jdt.core.IType;

/**
 * CDI Utilities.
 */
public class Utils {
    /**
     * Detects if a class is a managed bean by looking for a bean defining
     * annotation.
     *
     * @param type the type representing the potential bean class
     * @return true if the class has a bean defining annotation.
     */
    static boolean isManagedBean(IType type) {
        return getScopeAnnotations(type, SCOPES).size() > 0;
    }

    /**
     * Returns the list of recognised defining annotations applied to a
     * class.
     *
     * @param type the type representing the class
     * @param scopes list of defining annotations
     * @return list of recognised defining annotations applied to a class
     */
    public static List<String> getScopeAnnotations(IAnnotatable type, Set<String> scopes) {
        try {
            // Construct a stream of only the annotations applied to the type that are also
            // recognised annotations found in scopes.
            return Arrays.stream(type.getAnnotations()).map(annotation -> annotation.getElementName()).filter(scopes::contains).distinct().collect(Collectors.toList());

        } catch (Exception e) {
            return Collections.<String> emptyList();
        }
    }
}