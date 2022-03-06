/*******************************************************************************
* Copyright (c) 2020 IBM Corporation, Pengyu Xiong and others.
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

package org.eclipse.lsp4jakarta.jdt.core;

import org.eclipse.jdt.core.IAnnotatable;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Collections;

public class AnnotationUtil {
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
            return Arrays.stream(type.getAnnotations()).map(annotation -> annotation.getElementName())
                    .filter(scopes::contains).distinct().collect(Collectors.toList());

        } catch (Exception e) {
            return Collections.<String>emptyList();
        }
    }
}
