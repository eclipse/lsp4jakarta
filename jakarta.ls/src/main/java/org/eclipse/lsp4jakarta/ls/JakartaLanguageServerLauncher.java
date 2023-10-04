/*******************************************************************************
* Copyright (c) 2020 IBM Corporation and others.
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

package org.eclipse.lsp4jakarta.ls;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.jsonrpc.MessageConsumer;
import org.eclipse.lsp4j.launch.LSPLauncher.Builder;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.lsp4jakarta.ls.api.JakartaLanguageClientAPI;
import org.eclipse.lsp4jakarta.ls.commons.ParentProcessWatcher;

public class JakartaLanguageServerLauncher {
    public static void main(String[] args) {
        JakartaLanguageServer server = new JakartaLanguageServer();

        Function<MessageConsumer, MessageConsumer> wrapper;
        wrapper = it -> it;
        if ("true".equals(System.getProperty("runAsync")) ) {
            wrapper = it -> msg -> CompletableFuture.runAsync(() -> it.consume(msg));
        }
        if (!"false".equals(System.getProperty("watchParentProcess"))) {
            wrapper = new ParentProcessWatcher(server, wrapper);
        }
        Launcher<LanguageClient> launcher = createServerLauncher(server, System.in, System.out,
                Executors.newCachedThreadPool());

        server.setLanguageClient(launcher.getRemoteProxy());
        launcher.startListening();
    }

    /**
     * Create a new Launcher for a language server and an input and output stream.
     * Threads are started with the given executor service. The wrapper function is
     * applied to the incoming and outgoing message streams so additional message
     * handling such as validation and tracing can be included.
     *
     * @param server          - the server that receives method calls from the
     *                        remote client
     * @param in              - input stream to listen for incoming messages
     * @param out             - output stream to send outgoing messages
     * @param executorService - the executor service used to start threads
     * @param wrapper         - a function for plugging in additional message
     *                        consumers
     */
    public static Launcher<LanguageClient> createServerLauncher(LanguageServer server, InputStream in, OutputStream out,
            ExecutorService executorService) {
        return new Builder<LanguageClient>().setLocalService(server).setRemoteInterface(JakartaLanguageClientAPI.class)
                .setInput(in).setOutput(out).setExecutorService(executorService).create();
    }

}
