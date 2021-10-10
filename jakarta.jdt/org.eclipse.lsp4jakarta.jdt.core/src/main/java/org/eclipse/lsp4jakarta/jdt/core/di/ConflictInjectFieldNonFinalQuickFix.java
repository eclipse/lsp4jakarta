package org.eclipse.lsp4jakarta.jdt.core.di;

import org.eclipse.lsp4jakarta.jdt.codeAction.proposal.quickfix.RemoveAnnotationConflictQuickFix;

public class ConflictInjectFieldNonFinalQuickFix extends RemoveAnnotationConflictQuickFix{
	public ConflictInjectFieldNonFinalQuickFix() {
        super(false, "jakarta.inject.Inject");
    }
}
