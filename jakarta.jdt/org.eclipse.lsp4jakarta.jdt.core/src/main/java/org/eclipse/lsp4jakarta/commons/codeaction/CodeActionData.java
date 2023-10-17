/*******************************************************************************
* Copyright (c) 2023 Red Hat Inc. and others.
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
package org.eclipse.lsp4jakarta.commons.codeaction;

import java.util.Objects;

/**
 * Represents the data that all code actions have.
 */
public class CodeActionData {

    private String id;

    public CodeActionData() {
        this(null);
    }

    public CodeActionData(ICodeActionId id) {
        setCodeActionId(id);
    }

    /**
     * Returns the id of this code action as a string.
     *
     * Each type of code action has an id that represents it, so that it's easy to
     * associate a given code action to the code that generated it.
     *
     * @return the id of this code action
     */
    public String getCodeActionId() {
        return id;
    }

    /**
     * Sets the id of this code action.
     *
     * @param id the new value for the id of this code action
     */
    public void setCodeActionId(ICodeActionId id) {
        if (id != null) {
            this.id = id.getId();
        } else {
            this.id = null;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof CodeActionData)) {
            return false;
        }
        CodeActionData other = (CodeActionData) obj;
        return Objects.equals(id, other.id);
    }

    @Override
    public String toString() {
        return "CodeActionData [id=" + id + "]";
    }

}
