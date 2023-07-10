package org.eclipse.lsp4jakarta.snippets;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.eclipse.lsp4jakarta.commons.snippets.Snippet;
import org.eclipse.lsp4jakarta.commons.snippets.SnippetRegistry;
import org.junit.Test;

/**
 * Test for JakartaEE snippet registry.
 **/

public class JakartaSnippetRegistryTest {
	
	SnippetRegistry registry = new SnippetRegistry();

	@Test
	public void haveJavaSnippets() {
		assertFalse("Tests has Jakarta Java snippets", registry.getSnippets().isEmpty());
	}
	
	/**
	 * Jakarta Bean Validation snippets - @Email 
	 */
	@Test
	public void beanValidationSnippetsTest() {
        Optional<Snippet> beanValidationSnippet = findByPrefix("@Email", registry);
		assertTrue("Tests has @Email Java snippet", beanValidationSnippet.isPresent());
	}

	/**
	 * Jakarta Persistence snippets. - persist_context, persist_context_extended,
	 *  persist_context_extended_unsync, persist_entity.
	 */
	@Test
	public void persistenceSnippetsTest() {
        Optional<Snippet> persistenceSnippet = findByPrefix("persist_context", registry);
		assertTrue("Tests has persist_context Java snippet", persistenceSnippet.isPresent());
		
		
	}

	
	// Verify whether the snippet is present in the registry.
	private static Optional<Snippet> findByPrefix(String prefix, SnippetRegistry registry) {
		return registry.getSnippets().stream().filter(snippet -> snippet.getPrefixes().contains(prefix)).findFirst();
	}
}
