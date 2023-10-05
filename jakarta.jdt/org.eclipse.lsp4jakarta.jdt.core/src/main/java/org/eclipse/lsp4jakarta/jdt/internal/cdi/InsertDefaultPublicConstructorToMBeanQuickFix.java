package org.eclipse.lsp4jakarta.jdt.internal.cdi;

import org.eclipse.lsp4jakarta.commons.codeaction.ICodeActionId;
import org.eclipse.lsp4jakarta.commons.codeaction.JakartaCodeActionId;
import org.eclipse.lsp4jakarta.jdt.core.java.codeaction.InsertDefaultConstructorToClassQuickFix;

/**
 * Inserts a public default constructor the active managed bean class.
 */
public class InsertDefaultPublicConstructorToMBeanQuickFix extends InsertDefaultConstructorToClassQuickFix {

	public InsertDefaultPublicConstructorToMBeanQuickFix() {
		super("public");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getParticipantId() {
		return InsertDefaultPublicConstructorToMBeanQuickFix.class.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ICodeActionId getCodeActionId() {
		return JakartaCodeActionId.CDIInsertPublicCtrtToClass;
	}
}
