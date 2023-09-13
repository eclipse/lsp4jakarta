/*******************************************************************************
* Copyright (c) 2022 Red Hat Inc. and others.
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
package org.eclipse.lsp4jakarta.ls.commons;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Validate a given document with delay.
 *
 * @param <T>
 */
public class ValidatorDelayer<T extends TextDocument> {

    public static final long DEFAULT_VALIDATION_DELAY_MS = 500;

    private final ScheduledExecutorService executorService;

    private final Consumer<T> validator;

    private final Map<String, Future<?>> pendingValidationRequests;

    private final long validationDelayMs;

    public ValidatorDelayer(Consumer<T> validator) {
        this(Executors.newScheduledThreadPool(2), validator, DEFAULT_VALIDATION_DELAY_MS);
    }

    public ValidatorDelayer(ScheduledExecutorService executorService, Consumer<T> validator, long validationDelayMs) {
        this.executorService = executorService;
        this.validator = validator;
        this.pendingValidationRequests = new HashMap<>();
        this.validationDelayMs = validationDelayMs;
    }

    /**
     * Validate the given model <code>document</code> identified by the given <code>uri</code> with a delay.
     *
     * @param uri the document URI.
     * @param document the document model to validate.
     */
    public void validateWithDelay(T document) {
        String uri = document.getUri();
        cleanPendingValidation(uri);
        int version = document.getVersion();
        Future<?> request = executorService.schedule(() -> {
            synchronized (pendingValidationRequests) {
                pendingValidationRequests.remove(uri);
            }
            if (version == document.getVersion()) {
                validator.accept(document);
            }
        }, validationDelayMs, TimeUnit.MILLISECONDS);
        synchronized (pendingValidationRequests) {
            pendingValidationRequests.put(uri, request);
        }
    }

    public void cleanPendingValidation(String uri) {
        synchronized (pendingValidationRequests) {
            Future<?> request = pendingValidationRequests.get(uri);
            if (request != null) {
                request.cancel(true);
                pendingValidationRequests.remove(uri);
            }
        }
    }

    /**
     * Returns true if the document has a revalidation pending and false otherwise.
     *
     * @param uri the uri of the document to check
     * 
     * @return true if the document has a revalidation pending and false otherwise
     */
    public boolean isRevalidating(String uri) {
        synchronized (pendingValidationRequests) {
            return pendingValidationRequests.containsKey(uri);
        }
    }
}
