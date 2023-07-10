package org.eclipse.lsp4jakarta.ls.snippet;

import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.eclipse.lsp4jakarta.commons.snippets.Snippet;
import org.eclipse.lsp4jakarta.commons.snippets.SnippetRegistry;
import org.junit.Test;

public class JakartaBeanValidationSnippetTest {

	 @Test
	    public void validFieldConstraints() throws Exception {
		 SnippetRegistry registry = new SnippetRegistry();
	        
	        Optional<Snippet> restClassSnippet = findByPrefix("@Email", registry);	        
			assertTrue("Tests has @Email Java snippet", restClassSnippet.isPresent());
	 }
	 
	 private static Optional<Snippet> findByPrefix(String prefix, SnippetRegistry registry) {
			return registry.getSnippets().stream().filter(snippet -> snippet.getPrefixes().contains(prefix)).findFirst();
		}
}
