package org.eclipse.lsp4jakarta.jdt.internal.cdi;

import org.eclipse.lsp4jakarta.commons.codeaction.ICodeActionId;
import org.eclipse.lsp4jakarta.commons.codeaction.JakartaCodeActionId;

/**
 * Removes the @Disposes, @Observes and @ObservesAsync annotations from
 * parameter.
 */
public class RemoveInvalidInjectParamAnnotationQuickFix extends RemoveMethodParamAnnotationQuickFix {

	public RemoveInvalidInjectParamAnnotationQuickFix() {
		super(Constants.INVALID_INJECT_PARAMS.toArray((String[]::new)));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getParticipantId() {
		return RemoveInvalidInjectParamAnnotationQuickFix.class.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ICodeActionId getCodeActionId() {
		return JakartaCodeActionId.CDIRemoveInvalidInjectAnnotations;
	}
}
