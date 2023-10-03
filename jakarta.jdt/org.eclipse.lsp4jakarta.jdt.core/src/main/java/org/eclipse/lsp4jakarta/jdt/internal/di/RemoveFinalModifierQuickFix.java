package org.eclipse.lsp4jakarta.jdt.internal.di;

import org.eclipse.lsp4jakarta.commons.codeaction.JakartaCodeActionId;
import org.eclipse.lsp4jakarta.jdt.core.java.codeaction.RemoveModifierConflictQuickFix;

/**
 * Removes the final annotation.
 */
public class RemoveFinalModifierQuickFix extends RemoveModifierConflictQuickFix {
	public RemoveFinalModifierQuickFix() {
		super(false, "final");
	}

	@Override
	public String getParticipantId() {
		return RemoveFinalModifierQuickFix.class.getName();
	}

	@Override
	protected JakartaCodeActionId getCodeActionId() {
		return JakartaCodeActionId.DIRemoveFinalModifier;
	}
}
