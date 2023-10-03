package org.eclipse.lsp4jakarta.jdt.internal.annotations;

import org.eclipse.lsp4jakarta.commons.codeaction.JakartaCodeActionId;
import org.eclipse.lsp4jakarta.jdt.core.java.codeaction.RemoveModifierConflictQuickFix;

public class RemoveStaticModifierQuickFix extends RemoveModifierConflictQuickFix {
	public RemoveStaticModifierQuickFix() {
		super("static");
	}

	@Override
	public String getParticipantId() {
		return RemoveStaticModifierQuickFix.class.getName();
	}

	@Override
	protected JakartaCodeActionId getCodeActionId() {
		return JakartaCodeActionId.AnnotationRemoveStaticModifier;
	}
}
