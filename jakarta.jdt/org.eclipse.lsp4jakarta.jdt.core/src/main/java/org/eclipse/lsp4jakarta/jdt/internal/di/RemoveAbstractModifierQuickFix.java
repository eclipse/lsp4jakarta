package org.eclipse.lsp4jakarta.jdt.internal.di;

import org.eclipse.lsp4jakarta.commons.codeaction.JakartaCodeActionId;
import org.eclipse.lsp4jakarta.jdt.core.java.codeaction.RemoveModifierConflictQuickFix;

/**
 * Removes the final annotation.
 */
public class RemoveAbstractModifierQuickFix extends RemoveModifierConflictQuickFix {
	public RemoveAbstractModifierQuickFix() {
		super(false, "abstract");
	}

	@Override
	public String getParticipantId() {
		return RemoveAbstractModifierQuickFix.class.getName();
	}

	@Override
	protected JakartaCodeActionId getCodeActionId() {
		return JakartaCodeActionId.DIRemoveAbstractModifier;
	}
}
