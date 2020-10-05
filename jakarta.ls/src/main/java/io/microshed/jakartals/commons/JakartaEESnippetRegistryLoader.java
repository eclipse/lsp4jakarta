package io.microshed.jakartals.commons;

import java.io.File;
import java.util.logging.Logger;

/**
 * Loads in JakartaEE Specific Snippets
 * @author Ankush Sharma
 */
public class JakartaEESnippetRegistryLoader implements ISnippetRegistryLoader {
    private static final Logger LOGGER = Logger.getLogger(JakartaEESnippetRegistryLoader.class.getName());
	@Override
	public void load(SnippetRegistry registry) throws Exception {
        LOGGER.info("Loading snippets into registry...");
		registry.registerSnippets(JakartaEESnippetRegistryLoader.class.getClassLoader().getResourceAsStream("jax-rs.json"), SnippetContextForJava.TYPE_ADAPTER);
	}
    
}

