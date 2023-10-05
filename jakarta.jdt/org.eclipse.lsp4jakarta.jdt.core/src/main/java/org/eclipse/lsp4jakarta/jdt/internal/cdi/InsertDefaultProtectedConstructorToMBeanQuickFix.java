package org.eclipse.lsp4jakarta.jdt.internal.cdi;

import org.eclipse.lsp4jakarta.commons.codeaction.ICodeActionId;
import org.eclipse.lsp4jakarta.commons.codeaction.JakartaCodeActionId;
import org.eclipse.lsp4jakarta.jdt.core.java.codeaction.InsertDefaultConstructorToClassQuickFix;

/**
 * Inserts a protected default constructor the active managed bean class.
 */
public class InsertDefaultProtectedConstructorToMBeanQuickFix extends InsertDefaultConstructorToClassQuickFix {

	/**
	 * Constructor.
	 */
	public InsertDefaultProtectedConstructorToMBeanQuickFix() {
		super("protected");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getParticipantId() {
		return InsertDefaultProtectedConstructorToMBeanQuickFix.class.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ICodeActionId getCodeActionId() {
		return JakartaCodeActionId.CDIInsertProtectedCtrtToClass;
	}
}
