package org.eclipse.lsp4jakarta.jdt.internal.annotations;

import org.eclipse.lsp4jakarta.commons.codeaction.ICodeActionId;
import org.eclipse.lsp4jakarta.commons.codeaction.JakartaCodeActionId;
import org.eclipse.lsp4jakarta.jdt.core.java.codeaction.InsertAnnotationAttributesQuickFix;

/**
 * Inserts the type attribute to the @Resource annotation.
 */
public class InsertTypeAttributeToResourceAnnotation extends InsertAnnotationAttributesQuickFix {
	public InsertTypeAttributeToResourceAnnotation() {
		super("jakarta.annotation.Resource", "type");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getParticipantId() {
		return InsertTypeAttributeToResourceAnnotation.class.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ICodeActionId getCodeActionId() {
		return JakartaCodeActionId.InsertResourceAnnotationTypeAttribute;
	}
}
