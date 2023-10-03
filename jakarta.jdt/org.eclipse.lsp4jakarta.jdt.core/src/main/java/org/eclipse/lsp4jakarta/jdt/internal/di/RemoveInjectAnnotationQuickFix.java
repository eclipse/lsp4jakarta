package org.eclipse.lsp4jakarta.jdt.internal.di;

import org.eclipse.lsp4jakarta.commons.codeaction.JakartaCodeActionId;
import org.eclipse.lsp4jakarta.jdt.core.java.codeaction.RemoveAnnotationConflictQuickFix;

/**
 * Removes the @Inject annotation.
 */
public class RemoveInjectAnnotationQuickFix extends RemoveAnnotationConflictQuickFix {
	public RemoveInjectAnnotationQuickFix() {
		super(false, "jakarta.inject.Inject");
	}

	@Override
	public String getParticipantId() {
		return RemoveInjectAnnotationQuickFix.class.getName();
	}

	@Override
	protected JakartaCodeActionId getCodeActionId() {
		return JakartaCodeActionId.DIRemoveInjectAnnotation;
	}
}
