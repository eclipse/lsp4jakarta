package org.eclipse.lsp4jakarta.jdt.core.annotations;

import org.eclipse.lsp4jakarta.jdt.codeAction.proposal.quickfix.RemoveModifierConflictQuickFix;

public class PreDestroyAnnotationQuickFix2 extends RemoveModifierConflictQuickFix {
	public PreDestroyAnnotationQuickFix2() {
        super(false, "static");
    }
}
