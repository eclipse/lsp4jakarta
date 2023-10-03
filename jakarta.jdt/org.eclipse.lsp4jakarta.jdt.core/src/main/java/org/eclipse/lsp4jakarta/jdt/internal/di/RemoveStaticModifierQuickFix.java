package org.eclipse.lsp4jakarta.jdt.internal.di;

import org.eclipse.lsp4jakarta.commons.codeaction.JakartaCodeActionId;
import org.eclipse.lsp4jakarta.jdt.core.java.codeaction.RemoveModifierConflictQuickFix;

/**
 * Removes the final annotation.
 */
public class RemoveStaticModifierQuickFix extends RemoveModifierConflictQuickFix {
	public RemoveStaticModifierQuickFix() {
		super(false, "static");
	}

	@Override
	public String getParticipantId() {
		return RemoveStaticModifierQuickFix.class.getName();
	}

	@Override
	protected JakartaCodeActionId getCodeActionId() {
		return JakartaCodeActionId.DIRemoveStaticModifier;
	}
}
