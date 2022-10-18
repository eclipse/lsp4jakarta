/******************************************************************************* 
 * Copyright (c) 2021, 2022 IBM Corporation and others. 
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
package org.eclipse.lsp4jakarta.jdt.core.cdi;

import org.eclipse.jdt.core.IType;

import static org.eclipse.lsp4jakarta.jdt.core.cdi.ManagedBeanConstants.*;
import static org.eclipse.lsp4jakarta.jdt.core.AnnotationUtil.getScopeAnnotations;

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
}