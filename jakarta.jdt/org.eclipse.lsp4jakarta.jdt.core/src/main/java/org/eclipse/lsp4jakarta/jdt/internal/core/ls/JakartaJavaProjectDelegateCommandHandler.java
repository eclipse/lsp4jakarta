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
package org.eclipse.lsp4jakarta.jdt.internal.core.ls;

import static org.eclipse.lsp4jakarta.jdt.internal.core.ls.ArgumentUtils.getFirst;
import static org.eclipse.lsp4jakarta.jdt.internal.core.ls.ArgumentUtils.getString;
import static org.eclipse.lsp4jakarta.jdt.internal.core.ls.ArgumentUtils.getStringList;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.lsp4jakarta.commons.JakartaJavaProjectLabelsParams;
import org.eclipse.lsp4jakarta.jdt.core.ProjectLabelManager;

/**
 * Delegate command handler for Java project information
 *
 */
public class JakartaJavaProjectDelegateCommandHandler extends AbstractJakartaDelegateCommandHandler {

    private static final String PROJECT_LABELS_COMMAND_ID = "jakarta/java/projectLabels";

    public JakartaJavaProjectDelegateCommandHandler() {}

    @Override
    public Object executeCommand(String commandId, List<Object> arguments, IProgressMonitor progress) throws Exception {
        switch (commandId) {
            case PROJECT_LABELS_COMMAND_ID:
                return getProjectLabelInfo(arguments, commandId, progress);
            default:
                throw new UnsupportedOperationException(String.format("Unsupported command '%s'!", commandId));
        }
    }

    private static Object getProjectLabelInfo(List<Object> arguments, String commandId, IProgressMonitor monitor) {
        Map<String, Object> obj = getFirst(arguments);
        if (obj == null) {
            throw new UnsupportedOperationException(String.format(
                                                                  "Command '%s' must be called with one MicroProfileJavaProjectLabelsParams argument!", commandId));
        }
        // Get project name from the java file URI
        String javaFileUri = getString(obj, "uri");
        if (javaFileUri == null) {
            throw new UnsupportedOperationException(String.format(
                                                                  "Command '%s' must be called with required MicroProfileJavaProjectLabelsParams.uri (java file URI)!",
                                                                  commandId));
        }
        List<String> types = getStringList(obj, "types");
        JakartaJavaProjectLabelsParams params = new JakartaJavaProjectLabelsParams();
        params.setUri(javaFileUri);
        params.setTypes(types);
        return ProjectLabelManager.getInstance().getProjectLabelInfo(params, JDTUtilsLSImpl.getInstance(), monitor);
    }
}
