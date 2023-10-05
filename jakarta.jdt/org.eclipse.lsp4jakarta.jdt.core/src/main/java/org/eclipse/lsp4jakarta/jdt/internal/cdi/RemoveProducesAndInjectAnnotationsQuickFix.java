package org.eclipse.lsp4jakarta.jdt.internal.cdi;

import org.eclipse.lsp4jakarta.commons.codeaction.ICodeActionId;
import org.eclipse.lsp4jakarta.commons.codeaction.JakartaCodeActionId;
import org.eclipse.lsp4jakarta.jdt.core.java.codeaction.RemoveAnnotationConflictQuickFix;

/**
 * Removes the @Produces and the @inject annotations.
 */
public class RemoveProducesAndInjectAnnotationsQuickFix extends RemoveAnnotationConflictQuickFix {

	/**
	 * Constructor.
	 */
	public RemoveProducesAndInjectAnnotationsQuickFix() {
		super(false, "jakarta.enterprise.inject.Produces", "jakarta.inject.Inject");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getParticipantId() {
		return RemoveProducesAndInjectAnnotationsQuickFix.class.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ICodeActionId getCodeActionId() {
		return JakartaCodeActionId.CDIRemoveProducesAndInjectAnnotations;
	}
}
