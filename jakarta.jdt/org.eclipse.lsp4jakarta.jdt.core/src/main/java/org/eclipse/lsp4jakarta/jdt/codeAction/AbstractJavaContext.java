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

package org.eclipse.lsp4jakarta.jdt.codeAction;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.core.manipulation.dom.ASTResolving;

import org.eclipse.lsp4jakarta.jdt.core.JDTUtils;

/**
 * Abstract class for Java context for a given compilation unit. Reused from
 * https://github.com/eclipse/lsp4mp/blob/master/microprofile.jdt/org.eclipse.lsp4mp.jdt.core/src/main/java/org/eclipse/lsp4mp/jdt/core/java/AbtractJavaContext.java
 *
 * @author credit to Angelo ZERR
 *
 */
public abstract class AbstractJavaContext {

    private final String uri;

    private final ITypeRoot typeRoot;

    private final JDTUtils utils;

    private Map<String, Object> cache;

    private CompilationUnit fASTRoot;

    public AbstractJavaContext(String uri, ITypeRoot typeRoot, JDTUtils utils) {
        this.uri = uri;
        this.typeRoot = typeRoot;
        this.utils = utils;
        this.fASTRoot = null;
    }

    public String getUri() {
        return uri;
    }

    public ITypeRoot getTypeRoot() {
        return typeRoot;
    }

    public IJavaProject getJavaProject() {
        return getTypeRoot().getJavaProject();
    }

    public JDTUtils getUtils() {
        return utils;
    }

    /**
     * Associates the specified value with the specified key in the cache.
     *
     * @param key   the key.
     * @param value the value.
     */
    public void put(String key, Object value) {
        if (cache == null) {
            cache = new HashMap<>();
        }
        cache.put(key, value);
    }

    /**
     * Returns the value to which the specified key is mapped, or {@code null} if
     * this map contains no mapping for the key.
     *
     * @param key the key.
     * @return the value to which the specified key is mapped, or {@code null} if
     *         this map contains no mapping for the key.
     */
    public Object get(String key) {
        if (cache == null) {
            return null;
        }
        return cache.get(key);
    }

    public CompilationUnit getASTRoot() {
        if (fASTRoot == null) {
            fASTRoot = ASTResolving.createQuickFixAST((ICompilationUnit) getTypeRoot(), null);
        }
        return fASTRoot;
    }

    /**
     * @param root The ASTRoot to set.
     */
    public void setASTRoot(CompilationUnit root) {
        fASTRoot = root;
    }

}