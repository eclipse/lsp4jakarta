package org.eclipse.lsp4jakarta.jdt.internal.cdi;

import org.eclipse.lsp4jakarta.commons.codeaction.ICodeActionId;
import org.eclipse.lsp4jakarta.commons.codeaction.JakartaCodeActionId;
import org.eclipse.lsp4jakarta.jdt.core.java.codeaction.RemoveAnnotationConflictQuickFix;

/**
 * Removes the @Produces annotation
 */
public class RemoveProduceAnnotationQuickFix extends RemoveAnnotationConflictQuickFix {

	/**
	 * Constructor.
	 */
	public RemoveProduceAnnotationQuickFix() {
		super(false, "jakarta.enterprise.inject.Produces");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getParticipantId() {
		return RemoveProduceAnnotationQuickFix.class.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ICodeActionId getCodeActionId() {
		return JakartaCodeActionId.CDIRemoveProducesAnnotation;
	}
}
