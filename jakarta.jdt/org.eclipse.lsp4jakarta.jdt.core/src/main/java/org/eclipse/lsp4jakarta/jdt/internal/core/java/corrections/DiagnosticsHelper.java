/*******************************************************************************
 * Copyright (c) 2017, 2023 Red Hat Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Red Hat Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.lsp4jakarta.jdt.internal.core.java.corrections;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4jakarta.jdt.core.utils.IJDTUtils;

/**
 * Helper methods for {@link Diagnostic}
 *
 * Based on:
 * https://github.com/eclipse/lsp4mp/blob/0.9.0/microprofile.jdt/org.eclipse.lsp4mp.jdt.core/src/main/java/org/eclipse/lsp4mp/jdt/internal/core/java/corrections/DiagnosticsHelper.java
 *
 * @author Gorkem Ercan
 *
 */
public class DiagnosticsHelper {

    /**
     * Gets the end offset for the diagnostic.
     *
     * @param unit
     * @param range
     * @return starting offset or negative value if can not be determined
     */
    public static int getEndOffset(ICompilationUnit unit, Range range, IJDTUtils utils) {
        try {
            return utils.toOffset(unit.getBuffer(), range.getEnd().getLine(), range.getEnd().getCharacter());
        } catch (JavaModelException e) {
            return -1;
        }
    }

    /**
     * Gets the start offset for the diagnostic.
     *
     * @param unit
     * @param range
     * @return starting offset or negative value if can not be determined
     */
    public static int getStartOffset(ICompilationUnit unit, Range range, IJDTUtils utils) {
        try {
            return utils.toOffset(unit.getBuffer(), range.getStart().getLine(), range.getStart().getCharacter());
        } catch (JavaModelException e) {
            return -1;
        }
    }

    /**
     * Returns the length of the diagnostic
     *
     * @param unit
     * @param diagnostic
     * @return length of the diagnostics range.
     */
    public static int getLength(ICompilationUnit unit, Range range, IJDTUtils utils) {
        int start = DiagnosticsHelper.getStartOffset(unit, range, utils);
        int end = DiagnosticsHelper.getEndOffset(unit, range, utils);
        return end - start;
    }

}
