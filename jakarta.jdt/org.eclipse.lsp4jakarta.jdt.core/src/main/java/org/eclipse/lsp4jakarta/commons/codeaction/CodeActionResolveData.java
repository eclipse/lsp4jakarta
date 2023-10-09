/*******************************************************************************
* Copyright (c) 2022 Red Hat Inc. and others.
* All rights reserved. This program and the accompanying materials
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v20.html
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     Red Hat Inc. - initial API and implementation
*******************************************************************************/
package org.eclipse.lsp4jakarta.commons.codeaction;

import java.util.Map;
import java.util.Objects;

import org.eclipse.lsp4j.Range;

/**
 * Represents additional data that is needed to resolve a code action.
 *
 * @author datho7561
 */
public class CodeActionResolveData extends CodeActionData {

    private String participantId;
    private String documentUri;

    private Range range;

    private Map<String, Object> extendedData;

    private boolean resourceOperationSupported;
    private boolean commandConfigurationUpdateSupported;

    /**
     * Make code action data will all fields nulled/false
     *
     * Needed for Gson
     */
    public CodeActionResolveData() {
        this(null, null, null, null, false, false, null);
    }

    public CodeActionResolveData(String documentUri, String participantId, Range range,
                                 Map<String, Object> extendedData, boolean resourceOperationSupported,
                                 boolean commandConfigurationUpdateSupported, ICodeActionId id) {
        super(id);
        this.documentUri = documentUri;
        this.participantId = participantId;
        this.range = range;
        this.extendedData = extendedData;
        this.resourceOperationSupported = resourceOperationSupported;
        this.commandConfigurationUpdateSupported = commandConfigurationUpdateSupported;
    }

    /**
     * Returns the uri of the document that this code action applies to.
     *
     * @return the uri of the document that this code action applies to
     */
    public String getDocumentUri() {
        return this.documentUri;
    }

    /**
     * Returns the unique id of the IJavaCodeActionParticipant that can resolve the
     * text edits for this code action.
     *
     * @return the unique id of the IJavaCodeActionParticipant that can resolve the
     *         text edits for this code action
     */
    public String getParticipantId() {
        return this.participantId;
    }

    /**
     * Returns the range for which this CodeAction is applicable for.
     *
     * @return the range for which this CodeAction is applicable for
     */
    public Range getRange() {
        return this.range;
    }

    /**
     * Returns the entry in the extended data for the given key
     *
     * @param key the key to get the entry for
     * @return the entry in the extended data for the given key
     */
    public Object getExtendedDataEntry(String key) {
        return extendedData.get(key);
    }

    /**
     * Returns true if the client supports resource operations and false otherwise.
     *
     * @return true if the client supports resource operations and false otherwise
     */
    public boolean isResourceOperationSupported() {
        return this.resourceOperationSupported;
    }

    /**
     * Returns true if the client implements a command that allows the language
     * server to update preferences, and false otherwise.
     *
     * @return true if the client implements a command that allows the language
     *         server to update preferences, and false otherwise
     */
    public boolean isCommandConfigurationUpdateSupported() {
        return this.commandConfigurationUpdateSupported;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CodeActionResolveData)) {
            return false;
        }
        CodeActionResolveData that = (CodeActionResolveData) other;
        return Objects.equals(this.documentUri, that.documentUri)
               && Objects.equals(this.participantId, that.participantId) && Objects.equals(this.range, that.range)
               && Objects.equals(this.extendedData, that.extendedData)
               && Objects.equals(this.resourceOperationSupported, that.resourceOperationSupported)
               && Objects.equals(this.commandConfigurationUpdateSupported, that.commandConfigurationUpdateSupported);
    }

}
