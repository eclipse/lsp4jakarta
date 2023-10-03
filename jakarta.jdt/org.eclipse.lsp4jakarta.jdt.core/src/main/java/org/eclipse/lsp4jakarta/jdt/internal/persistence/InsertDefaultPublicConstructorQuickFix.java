package org.eclipse.lsp4jakarta.jdt.internal.persistence;

import org.eclipse.lsp4jakarta.commons.codeaction.ICodeActionId;
import org.eclipse.lsp4jakarta.commons.codeaction.JakartaCodeActionId;
import org.eclipse.lsp4jakarta.jdt.core.java.codeaction.InsertDefaultConstructorToClassQuickFix;

/**
 * Inserts a public default constructor the the active class.
 */
public class InsertDefaultPublicConstructorQuickFix extends InsertDefaultConstructorToClassQuickFix {

	public InsertDefaultPublicConstructorQuickFix() {
		super("public");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getParticipantId() {
		return InsertDefaultPublicConstructorQuickFix.class.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ICodeActionId getCodeActionId() {
		return JakartaCodeActionId.PersistenceInsertPublicCtrtToClass;
	}
}
