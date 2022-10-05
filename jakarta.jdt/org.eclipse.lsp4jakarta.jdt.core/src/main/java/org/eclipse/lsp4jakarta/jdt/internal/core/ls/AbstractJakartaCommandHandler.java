package org.eclipse.lsp4jakarta.jdt.internal.core.ls;

public abstract class AbstractJakartaCommandHandler implements IDelegateCommandHandler {
    
	// private static final Logger LOGGER = Logger.getLogger(AbstractJakartaCommandHandler.class.getName());

	// /**
	//  * MicroProfile client commands
	//  */
	// private static final String MICROPROFILE_PROPERTIES_CHANGED_COMMAND = "microprofile/propertiesChanged";

	// private static final IMicroProfilePropertiesChangedListener LISTENER = (event) -> {
	// 	try {
	// 		// Execute client command with a timeout of 5 seconds to avoid blocking jobs.
	// 		JavaLanguageServerPlugin.getInstance().getClientConnection().executeClientCommand(
	// 				Duration.of(5, ChronoUnit.SECONDS), MICROPROFILE_PROPERTIES_CHANGED_COMMAND, event);
	// 	} catch (Exception e) {
	// 		LOGGER.log(Level.SEVERE, "Error while sending 'microprofile/propertiesChanged' event to the client", e);
	// 	}
	// };

	// private static boolean initialized;

	// public AbstractMicroProfileDelegateCommandHandler() {
	// 	initialize();
	// }

	// /**
	//  * Add MicroProfile properties changed listener if needed.
	//  */
	// private static synchronized void initialize() {
	// 	if (initialized) {
	// 		return;
	// 	}
	// 	// Add a classpath changed listener to execute client command
	// 	// "microprofile/propertiesChanged"
	// 	MicroProfilePropertiesListenerManager.getInstance().addMicroProfilePropertiesChangedListener(LISTENER);
	// 	initialized = true;
	// }
}
