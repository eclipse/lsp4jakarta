package org.eclipse.lsp4jakarta.jdt.internal.jsonb;

import org.eclipse.lsp4jakarta.commons.codeaction.JakartaCodeActionId;
import org.eclipse.lsp4jakarta.jdt.core.java.codeaction.RemoveAnnotationConflictQuickFix;

/**
 * Removes the @JsonbCreator annotation.
 */
public class RemoveJsonbCreatorAnnotationQuickFix extends RemoveAnnotationConflictQuickFix {
	public RemoveJsonbCreatorAnnotationQuickFix() {
		super("jakarta.json.bind.annotation.JsonbCreator");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getParticipantId() {
		return RemoveJsonbCreatorAnnotationQuickFix.class.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected JakartaCodeActionId getCodeActionId() {
		return JakartaCodeActionId.JSONBRemoveJsonbCreatorAnnotation;
	}
}
