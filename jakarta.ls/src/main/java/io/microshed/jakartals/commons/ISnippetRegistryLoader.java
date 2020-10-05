package io.microshed.jakartals.commons;

import io.microshed.jakartals.commons.SnippetRegistry;

/**
 * Loader used to load snippets in a given registry for a language id
 * 
 * @author Ankush Sharma, credit to Angelo ZERR
 */
public interface ISnippetRegistryLoader {
    /**
     * Register a given snippet in the register
     * @param registry <SnippetRegistry>
     * @throws Exception
     */
    void load(SnippetRegistry registry) throws Exception;

}
