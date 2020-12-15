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

package io.microshed.jakartals.commons;

import java.util.Collections;
import java.util.List;

/**
 * Stores labels for the project located at a specific project uri
 * Modified from https://github.com/eclipse/lsp4mp/blob/master/microprofile.ls/org.eclipse.lsp4mp.ls/src/main/java/org/eclipse/lsp4mp/commons/ProjectLabelInfoEntry.java
 *
 * @author Ankush Sharma, credit to dakwon
 *
 */
public class ProjectLabelInfoEntry {
    public static final ProjectLabelInfoEntry EMPTY_PROJECT_INFO = new ProjectLabelInfoEntry("",
            Collections.emptyList());

    private final String uri;
    private final List<String> labels;

    public ProjectLabelInfoEntry(String uri, List<String> labels) {
        this.uri = uri;
        this.labels = labels;
    }

    /**
     * Returns the project uri
     *
     * @return the project uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * Returns the labels for the current project uri
     *
     * @return the labels for the current project uri
     */
    public List<String> getLabels() {
        return labels;
    }

    /**
     * Returns true if the project has the given label and false otherwise.
     *
     * @param label the label.
     * @return true if the project has the given label and false otherwise.
     */
    public boolean hasLabel(String label) {
        return labels != null && labels.contains(label);
    }
}
