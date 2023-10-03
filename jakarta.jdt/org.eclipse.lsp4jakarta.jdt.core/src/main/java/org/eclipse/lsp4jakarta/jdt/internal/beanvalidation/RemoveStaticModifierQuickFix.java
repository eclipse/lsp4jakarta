package org.eclipse.lsp4jakarta.jdt.internal.beanvalidation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4jakarta.commons.codeaction.JakartaCodeActionId;
import org.eclipse.lsp4jakarta.jdt.core.java.codeaction.JavaCodeActionContext;
import org.eclipse.lsp4jakarta.jdt.core.java.codeaction.RemoveModifierConflictQuickFix;

/**
 * Removes a static modifier.
 */
public class RemoveStaticModifierQuickFix extends RemoveModifierConflictQuickFix {
	public RemoveStaticModifierQuickFix() {
		super("static");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getParticipantId() {
		return RemoveStaticModifierQuickFix.class.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected JakartaCodeActionId getCodeActionId() {
		return JakartaCodeActionId.BBRemoveStaticModifier;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<? extends CodeAction> getCodeActions(JavaCodeActionContext context, Diagnostic diagnostic,
			IProgressMonitor monitor) throws CoreException {
		List<? extends CodeAction> codeActions = new ArrayList<>();
		if (diagnostic.getCode().getLeft()
				.equals(ErrorCode.InvalidConstrainAnnotationOnStaticMethodOrField.getCode())) {
			codeActions = super.getCodeActions(context, diagnostic, monitor);
		}

		return codeActions;
	}
}
