/*******************************************************************************
* Copyright (c) 2019-2020 Red Hat Inc. and others.
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
package org.eclipse.lsp4jakarta.settings.capabilities;

import static org.eclipse.lsp4jakarta.settings.capabilities.ServerCapabilitiesConstants.CODE_ACTION_ID;
import static org.eclipse.lsp4jakarta.settings.capabilities.ServerCapabilitiesConstants.COMPLETION_ID;
import static org.eclipse.lsp4jakarta.settings.capabilities.ServerCapabilitiesConstants.DEFAULT_CODEACTION_OPTIONS;
import static org.eclipse.lsp4jakarta.settings.capabilities.ServerCapabilitiesConstants.DEFAULT_COMPLETION_OPTIONS;
import static org.eclipse.lsp4jakarta.settings.capabilities.ServerCapabilitiesConstants.TEXT_DOCUMENT_CODE_ACTION;
import static org.eclipse.lsp4jakarta.settings.capabilities.ServerCapabilitiesConstants.TEXT_DOCUMENT_COMPLETION;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.lsp4j.ClientCapabilities;
import org.eclipse.lsp4j.Registration;
import org.eclipse.lsp4j.RegistrationParams;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4jakarta.ls.commons.client.ExtendedClientCapabilities;

/**
 * Manages dynamic capabilities
 *
 * Based on:
 * https://github.com/eclipse/lsp4mp/blob/0.9.0/microprofile.ls/org.eclipse.lsp4mp.ls/src/main/java/org/eclipse/lsp4mp/settings/capabilities/MicroProfileCapabilityManager.java
 */
public class JakartaCapabilityManager {

    private final Set<String> registeredCapabilities = new HashSet<>(3);
    private final LanguageClient languageClient;

    private ClientCapabilitiesWrapper clientWrapper;

    public JakartaCapabilityManager(LanguageClient languageClient) {
        this.languageClient = languageClient;
    }

    /**
     * Registers all dynamic capabilities.
     */
    public void initializeCapabilities() {
        if (this.getClientCapabilities().isCodeActionDynamicRegistered()) {
            registerCapability(CODE_ACTION_ID, TEXT_DOCUMENT_CODE_ACTION, DEFAULT_CODEACTION_OPTIONS);
        }
        if (this.getClientCapabilities().isCompletionDynamicRegistrationSupported()) {
            registerCapability(COMPLETION_ID, TEXT_DOCUMENT_COMPLETION, DEFAULT_COMPLETION_OPTIONS);
        }
    }

    public void setClientCapabilities(ClientCapabilities clientCapabilities,
                                      ExtendedClientCapabilities extendedClientCapabilities) {
        this.clientWrapper = new ClientCapabilitiesWrapper(clientCapabilities, extendedClientCapabilities);
    }

    public ClientCapabilitiesWrapper getClientCapabilities() {
        if (this.clientWrapper == null) {
            this.clientWrapper = new ClientCapabilitiesWrapper();
        }
        return this.clientWrapper;
    }

    public Set<String> getRegisteredCapabilities() {
        return registeredCapabilities;
    }

    private void registerCapability(String id, String method, Object options) {
        if (registeredCapabilities.add(id)) {
            Registration registration = new Registration(id, method, options);
            RegistrationParams registrationParams = new RegistrationParams(Collections.singletonList(registration));
            languageClient.registerCapability(registrationParams);
        }
    }
}