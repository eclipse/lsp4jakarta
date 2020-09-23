package io.microshed.jakartals.commons;

import java.io.File;
import java.util.logging.Logger;

/**
 * Loads in JakartaEE Specific Snippets
 * @author Ankush Sharma
 */
public class JakartaEESnippetRegistryLoader implements ISnippetRegistryLoader {
    private static final Logger LOGGER = Logger.getLogger(SnippetRegistry.class.getName());
	@Override
	public void load(SnippetRegistry registry) throws Exception {
		registry.registerSnippets(JakartaEESnippetRegistryLoader.class.getResourceAsStream("jax-rs.json"), SnippetContextForJava.TYPE_ADAPTER);
	}
    
}

