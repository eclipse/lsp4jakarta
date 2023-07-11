package org.eclipse.lsp4jakarta.snippets;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import org.eclipse.lsp4jakarta.commons.JavaCursorContextKind;
import org.eclipse.lsp4jakarta.commons.JavaCursorContextResult;
import org.eclipse.lsp4jakarta.commons.ProjectLabelInfoEntry;
import org.eclipse.lsp4jakarta.commons.snippets.ISnippetContext;
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
		
		ISnippetContext<?> context = beanValidationSnippet.get().getContext();
		assertNotNull("@Email snippet has context", context);
		assertTrue("@Email snippet context is Java context", context instanceof SnippetContextForJava);
		
		
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
		
		
		ISnippetContext<?> context = persistcontextSnippet.get().getContext();
		assertNotNull("persist_context snippet has context", context);
		assertTrue("persist_context snippet context is Java context", context instanceof SnippetContextForJava);
		
		
	}
	
	
	/**
	 * Jakarta RESTful Web Services snippets - rest_class, rest_get
	 * rest_post, rest_put, rest_delete, rest_head  
	 */
	@Test
	public void restfulWebServicesSnippetsPrefixTest() {
        Optional<Snippet> restClassSnippet = findByPrefix("rest_class", registry);
		assertTrue("Tests has rest_class Java snippet", restClassSnippet.isPresent());
		
		Optional<Snippet> restGetSnippet = findByPrefix("rest_get", registry);
		assertTrue("Tests has rest_get Java snippet", restGetSnippet.isPresent());
		
		Optional<Snippet> restPostSnippet = findByPrefix("rest_post", registry);
		assertTrue("Tests has rest_post Java snippet", restPostSnippet.isPresent());
		
		Optional<Snippet> restPutSnippet = findByPrefix("rest_put", registry);
		assertTrue("Tests has rest_put Java snippet", restPutSnippet.isPresent());
		
		Optional<Snippet> restDeleteSnippet = findByPrefix("rest_delete", registry);
		assertTrue("Tests has rest_delete Java snippet", restDeleteSnippet.isPresent());
		
		Optional<Snippet> restHeadSnippet = findByPrefix("rest_head", registry);
		assertTrue("Tests has rest_head Java snippet", restHeadSnippet.isPresent());
		
		snippetsContextTest(restClassSnippet, "jakarta.ws.rs.GET", JavaCursorContextKind.IN_EMPTY_FILE);
		snippetsContextTest(restGetSnippet, "jakarta.ws.rs.GET", JavaCursorContextKind.BEFORE_METHOD);
		snippetsContextTest(restPostSnippet, "jakarta.ws.rs.POST", JavaCursorContextKind.BEFORE_METHOD);
		snippetsContextTest(restPutSnippet, "jakarta.ws.rs.PUT", JavaCursorContextKind.BEFORE_METHOD);
		snippetsContextTest(restDeleteSnippet, "jakarta.ws.rs.DELETE", JavaCursorContextKind.BEFORE_METHOD);
		snippetsContextTest(restHeadSnippet, "jakarta.ws.rs.HEAD", JavaCursorContextKind.BEFORE_METHOD);
				
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
		
		ISnippetContext<?> context = servletgenericSnippet.get().getContext();
		assertNotNull("servlet_generic snippet has context", context);
		assertTrue("servlet_generic snippet context is Java context", context instanceof SnippetContextForJava);

		
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
		
		
		ISnippetContext<?> context = txuserinjectSnippet.get().getContext();
		assertNotNull("tx_user_inject snippet has context", context);
		assertTrue("tx_user_inject snippet context is Java context", context instanceof SnippetContextForJava);

		
	}

	
	// Verify whether the snippet is present in the registry.
	private static Optional<Snippet> findByPrefix(String prefix, SnippetRegistry registry) {
		return registry.getSnippets().stream().filter(snippet -> snippet.getPrefixes().contains(prefix)).findFirst();
	}
	
	private void snippetsContextTest(Optional<Snippet> snippet, String contextType, JavaCursorContextKind javaCursorContextKind) {
		
		ISnippetContext<?> context = snippet.get().getContext();
		assertNotNull(snippet.get().getPrefixes() + " snippet has context", context);
		assertTrue(snippet.get().getPrefixes() + " snippet context is Java context", context instanceof SnippetContextForJava);
		
		ProjectLabelInfoEntry projectInfo = new ProjectLabelInfoEntry("", new ArrayList<>());
		boolean match = ((SnippetContextForJava) context).isMatch(context(projectInfo, javaCursorContextKind));
		assertFalse("Project has no javax.ws.rs.GET or jakarta.ws.rs.GET type", match);
		
		
		ProjectLabelInfoEntry projectInfo1 = new ProjectLabelInfoEntry("", Arrays.asList(contextType));
		boolean match1 = ((SnippetContextForJava) context).isMatch(context(projectInfo1, javaCursorContextKind ));
		assertTrue("Project has " + contextType + " type", match1);
		
		
	}
	
	private static JavaSnippetCompletionContext context(ProjectLabelInfoEntry projectInfo, JavaCursorContextKind javaCursorContext) {
		return context(projectInfo, javaCursorContext, "");
	}
	
	private static JavaSnippetCompletionContext context(ProjectLabelInfoEntry projectInfo, JavaCursorContextKind javaCursorContext, String prefix) {
		return new JavaSnippetCompletionContext(projectInfo, new JavaCursorContextResult(javaCursorContext, prefix));
	}
}
