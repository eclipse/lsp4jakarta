package org.eclipse.lsp4jakarta.jdt.internal.jaxrs;

import org.eclipse.lsp4jakarta.commons.codeaction.ICodeActionId;
import org.eclipse.lsp4jakarta.commons.codeaction.JakartaCodeActionId;
import org.eclipse.lsp4jakarta.jdt.core.java.codeaction.InsertDefaultConstructorToClassQuickFix;

/**
 * Inserts a default public constructor to the underlying class.
 */
public class InsertDefaultPublicConstructorQuickFix extends InsertDefaultConstructorToClassQuickFix {

	/**
	 * Constructor.
	 */
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
		return JakartaCodeActionId.jaxrsInsertPublicCtrtToClass;
	}
}
