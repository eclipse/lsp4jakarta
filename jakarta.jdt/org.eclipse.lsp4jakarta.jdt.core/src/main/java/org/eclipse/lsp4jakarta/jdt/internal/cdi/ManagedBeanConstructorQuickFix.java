package org.eclipse.lsp4jakarta.jdt.internal.cdi;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.lsp4jakarta.commons.codeaction.ICodeActionId;
import org.eclipse.lsp4jakarta.commons.codeaction.JakartaCodeActionId;
import org.eclipse.lsp4jakarta.jdt.core.java.codeaction.InsertAnnotationMissingQuickFix;

public class ManagedBeanConstructorQuickFix extends InsertAnnotationMissingQuickFix {

	/**
	 * Constructor.
	 */
	public ManagedBeanConstructorQuickFix() {
		super("jakarta.inject.Inject");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getParticipantId() {
		return ManagedBeanConstructorQuickFix.class.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ICodeActionId getCodeActionId() {
		return JakartaCodeActionId.CDIInsertInjectAnnotation;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("restriction")
	@Override
	protected IBinding getBinding(ASTNode node) {
		if (node.getParent() instanceof VariableDeclarationFragment) {
			return ((VariableDeclarationFragment) node.getParent()).resolveBinding();
		}
		if (node.getParent() instanceof MethodDeclaration) {
			MethodDeclaration methodDecl = (MethodDeclaration) node.getParent();
			return methodDecl.resolveBinding();
		}
		return org.eclipse.jdt.internal.corext.dom.Bindings.getBindingOfParentType(node);
	}
}
