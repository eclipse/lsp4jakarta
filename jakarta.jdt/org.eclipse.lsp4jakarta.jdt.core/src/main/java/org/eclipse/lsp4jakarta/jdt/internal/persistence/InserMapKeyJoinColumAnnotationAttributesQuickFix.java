package org.eclipse.lsp4jakarta.jdt.internal.persistence;

import java.text.MessageFormat;

import org.eclipse.lsp4jakarta.commons.codeaction.ICodeActionId;
import org.eclipse.lsp4jakarta.commons.codeaction.JakartaCodeActionId;
import org.eclipse.lsp4jakarta.jdt.core.java.codeaction.InsertAnnotationAttributesQuickFix;

public class InserMapKeyJoinColumAnnotationAttributesQuickFix extends InsertAnnotationAttributesQuickFix {
	private static final String CODE_ACTION_LABEL = "Insert the missing attributes to the @{0} annotation";

	public InserMapKeyJoinColumAnnotationAttributesQuickFix() {
		super("jakarta.persistence.MapKeyJoinColumn", "name", "referencedColumnName");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getParticipantId() {
		return InserMapKeyJoinColumAnnotationAttributesQuickFix.class.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ICodeActionId getCodeActionId() {
		return JakartaCodeActionId.PersistenceInsertAttributesToMKJCAnnotation;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getLabel(String annotation, String[] attributes) {
		String[] parts = annotation.split("\\.");
		String AnnotationName = (parts.length > 1) ? parts[parts.length - 1] : annotation;
		return MessageFormat.format(CODE_ACTION_LABEL, AnnotationName);
	}
}
