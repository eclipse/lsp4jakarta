package io.microshed.jakartals.commons;

/**
*Snippet context used to filter the snippet
* @param <T> the value type waited by the snipper
* @author Ankush Sharma, credit to Angelo ZERR
*/

public interface ISnippetContext<T> {
    /**
	 * Return true if the given value match the snippet context and false otherwise.
	 *
	 * @param value the value to check.
	 * @return true if the given value match the snippet context and false
	 *         otherwise.
	 */
    boolean isMatch(T value);
}
