package org.eclipse.lsp4jakarta.jdt.internal.cdi;

import org.eclipse.lsp4jakarta.commons.codeaction.ICodeActionId;
import org.eclipse.lsp4jakarta.commons.codeaction.JakartaCodeActionId;
import org.eclipse.lsp4jakarta.jdt.core.java.codeaction.RemoveAnnotationConflictQuickFix;

/**
 * Removes the @Inject annotation
 */
public class RemoveInjectAnnotationQuickFix extends RemoveAnnotationConflictQuickFix {

	/**
	 * Constructor.
	 */
	public RemoveInjectAnnotationQuickFix() {
		super(false, "jakarta.inject.Inject");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getParticipantId() {
		return RemoveInjectAnnotationQuickFix.class.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ICodeActionId getCodeActionId() {
		return JakartaCodeActionId.CDIRemoveInjectAnnotation;
	}
}
