package org.eclipse.lsp4jakarta.jdt.internal.persistence;

import org.eclipse.lsp4jakarta.commons.codeaction.ICodeActionId;
import org.eclipse.lsp4jakarta.commons.codeaction.JakartaCodeActionId;
import org.eclipse.lsp4jakarta.jdt.core.java.codeaction.InsertDefaultConstructorToClassQuickFix;

/**
 * Inserts a protected default constructor the the active class.
 */
public class InsertDefaultProtectedConstructorQuickFix extends InsertDefaultConstructorToClassQuickFix {

	/**
	 * Constructor.
	 */
	public InsertDefaultProtectedConstructorQuickFix() {
		super("protected");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getParticipantId() {
		return InsertDefaultProtectedConstructorQuickFix.class.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ICodeActionId getCodeActionId() {
		return JakartaCodeActionId.PersistenceInsertProtectedCtrtToClass;
	}
}
