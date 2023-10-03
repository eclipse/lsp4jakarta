package org.eclipse.lsp4jakarta.jdt.internal.persistence;

import org.eclipse.lsp4jakarta.commons.codeaction.ICodeActionId;
import org.eclipse.lsp4jakarta.commons.codeaction.JakartaCodeActionId;
import org.eclipse.lsp4jakarta.jdt.core.java.codeaction.RemoveModifierConflictQuickFix;

public class RemoveFinalModifierQuickFix extends RemoveModifierConflictQuickFix {

	/**
	 * Constructor.
	 */
	public RemoveFinalModifierQuickFix() {
		super("final");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getParticipantId() {
		return RemoveFinalModifierQuickFix.class.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ICodeActionId getCodeActionId() {
		return JakartaCodeActionId.PersistenceRemoveFinalModifier;
	}

}
