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
        Optional<Snippet> persistcontextSnippet = findByPrefix("persist_context", registry);
		assertTrue("Tests has persist_context Java snippet", persistcontextSnippet.isPresent());
		
		Optional<Snippet> persistcontextextendedSnippet = findByPrefix("persist_context_extended", registry);
		assertTrue("Tests has persist_context_extended Java snippet", persistcontextextendedSnippet.isPresent());
		
		Optional<Snippet> persistcontextextendedunsyncSnippet = findByPrefix("persist_context_extended_unsync", registry);
		assertTrue("Tests has persist_context_extended_unsync Java snippet", persistcontextextendedunsyncSnippet.isPresent());
		
		Optional<Snippet> persistentitySnippet = findByPrefix("persist_entity", registry);
		assertTrue("Tests has persist_entity Java snippet", persistentitySnippet.isPresent());
		
	}
	
	
	/**
	 * Jakarta RESTful Web Services snippets - rest_class, rest_get
	 * rest_post, rest_put, rest_delete, rest_head  
	 */
	@Test
	public void RESTfulWebServicesSnippetsTest() {
        Optional<Snippet> restclassSnippet = findByPrefix("rest_class", registry);
		assertTrue("Tests has rest_class Java snippet", restclassSnippet.isPresent());
		
		
		Optional<Snippet> restgetSnippet = findByPrefix("rest_get", registry);
		assertTrue("Tests has rest_get Java snippet", restgetSnippet.isPresent());
		
		Optional<Snippet> restpostSnippet = findByPrefix("rest_post", registry);
		assertTrue("Tests has rest_post Java snippet", restpostSnippet.isPresent());
		
		Optional<Snippet> restputSnippet = findByPrefix("rest_put", registry);
		assertTrue("Tests has rest_put Java snippet", restputSnippet.isPresent());
		
		Optional<Snippet> restdeleteSnippet = findByPrefix("rest_delete", registry);
		assertTrue("Tests has rest_delete Java snippet", restdeleteSnippet.isPresent());
		
		Optional<Snippet> restheadSnippet = findByPrefix("rest_head", registry);
		assertTrue("Tests has rest_head Java snippet", restheadSnippet.isPresent());
		
	}
	
	
	
	/**
	 * Jakarta Servlet snippets - servlet_generic, servlet_doget
	 * servlet_dopost, servlet_webfilter
	 */
	@Test
	public void ServletSnippetsTest() {
        Optional<Snippet> servletgenericSnippet = findByPrefix("servlet_generic", registry);
		assertTrue("Tests has servlet_generic Java snippet", servletgenericSnippet.isPresent());
		
		Optional<Snippet> servletdogetSnippet = findByPrefix("servlet_doget", registry);
		assertTrue("Tests has servlet_doget Java snippet", servletdogetSnippet.isPresent());
		
		Optional<Snippet> servletdopostSnippet = findByPrefix("servlet_dopost", registry);
		assertTrue("Tests has servlet_dopost Java snippet", servletdopostSnippet.isPresent());
		
		Optional<Snippet> servletwebfilterSnippet = findByPrefix("servlet_webfilter", registry);
		assertTrue("Tests has servlet_webfilter Java snippet", servletwebfilterSnippet.isPresent());
	}
	
	
	/**
	 * Jakarta Transactions snippets -  tx_user_inject,
	 * tx_user_jndi, @ Transactional
	 */
	@Test
	public void TransactionsSnippetsTest() {
        Optional<Snippet> txuserinjectSnippet = findByPrefix("tx_user_inject", registry);
		assertTrue("Tests has tx_user_inject Java snippet", txuserinjectSnippet.isPresent());
		
		Optional<Snippet> txuserjndiSnippet = findByPrefix("tx_user_jndi", registry);
		assertTrue("Tests has tx_user_jndi Java snippet", txuserjndiSnippet.isPresent());
		
		Optional<Snippet> TransactionalSnippet = findByPrefix("@Transactional", registry);
		assertTrue("Tests has @Transactional Java snippet", TransactionalSnippet.isPresent());
	}


	
	
	

	
	// Verify whether the snippet is present in the registry.
	private static Optional<Snippet> findByPrefix(String prefix, SnippetRegistry registry) {
		return registry.getSnippets().stream().filter(snippet -> snippet.getPrefixes().contains(prefix)).findFirst();
	}
}
