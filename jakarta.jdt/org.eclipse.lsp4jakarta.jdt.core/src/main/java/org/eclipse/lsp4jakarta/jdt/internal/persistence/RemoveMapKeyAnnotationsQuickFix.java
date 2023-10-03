
package org.eclipse.lsp4jakarta.jdt.internal.persistence;

import org.eclipse.lsp4jakarta.commons.codeaction.JakartaCodeActionId;
import org.eclipse.lsp4jakarta.jdt.core.java.codeaction.RemoveAnnotationConflictQuickFix;

/**
 * Removes @MapKey and @MapKeyClass.
 */
public class RemoveMapKeyAnnotationsQuickFix extends RemoveAnnotationConflictQuickFix {

	public RemoveMapKeyAnnotationsQuickFix() {
		super(false, "jakarta.persistence.annotation.MapKeyClass", "jakarta.persistence.annotation.MapKey");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getParticipantId() {
		return RemoveMapKeyAnnotationsQuickFix.class.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected JakartaCodeActionId getCodeActionId() {
		return JakartaCodeActionId.PersistenceRemoveMapKeyAnnotation;
	}

}
