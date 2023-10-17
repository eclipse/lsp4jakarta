/*******************************************************************************
* Copyright (c) 2020 Red Hat Inc. and others.
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
package org.eclipse.lsp4jakarta.jdt.core.java.definition;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4jakarta.jdt.core.java.AbstractJavaContext;
import org.eclipse.lsp4jakarta.jdt.core.utils.IJDTUtils;

/**
 * Java definition context for a given compilation unit.
 *
 * @author Angelo ZERR
 *
 */
public class JavaDefinitionContext extends AbstractJavaContext {

    private final IJavaElement hyperlinkedElement;

    private final Position hyperlinkedPosition;

    public JavaDefinitionContext(String uri, ITypeRoot typeRoot, IJDTUtils utils, IJavaElement hyperlinkeElement,
                                 Position hyperlinkePosition) {
        super(uri, typeRoot, utils);
        this.hyperlinkedElement = hyperlinkeElement;
        this.hyperlinkedPosition = hyperlinkePosition;
    }

    /**
     * Returns the hyperlinked Java element.
     *
     * @return the hyperlinked Java element.
     */
    public IJavaElement getHyperlinkedElement() {
        return hyperlinkedElement;
    }

    /**
     * Returns the hyperlinked position.
     *
     * @return the hyperlinked position.
     */
    public Position getHyperlinkedPosition() {
        return hyperlinkedPosition;
    }

}
