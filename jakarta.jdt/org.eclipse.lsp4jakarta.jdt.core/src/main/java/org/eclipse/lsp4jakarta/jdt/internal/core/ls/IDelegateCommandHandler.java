package org.eclipse.lsp4jakarta.jdt.internal.core.ls;

import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IDelegateCommandHandler {
    /**
	 * Language server to execute commands. One handler can handle multiple
	 * commands.
	 *
	 * @param commandId
	 *            the command ID for the execute command
	 * @param arguments
	 *            list of arguments passed to the delegate command handler
	 * @param monitor
	 *            monitor of the activity progress
	 * @return execute command result
	 * @throws Exception
	 *             the unhandled exception will be wrapped in
	 *             <code>org.eclipse.lsp4j.jsonrpc.ResponseErrorException</code>
	 *             and be wired back to the JSON-RPC protocol caller
	 */
	Object executeCommand(String commandId, List<Object> arguments, IProgressMonitor monitor) throws Exception;
}
