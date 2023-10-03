package org.eclipse.lsp4jakarta.jdt.internal.servlet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.internal.corext.dom.Bindings;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionKind;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4jakarta.commons.codeaction.CodeActionResolveData;
import org.eclipse.lsp4jakarta.commons.codeaction.JakartaCodeActionId;
import org.eclipse.lsp4jakarta.jdt.core.java.codeaction.ExtendedCodeAction;
import org.eclipse.lsp4jakarta.jdt.core.java.codeaction.IJavaCodeActionParticipant;
import org.eclipse.lsp4jakarta.jdt.core.java.codeaction.JavaCodeActionContext;
import org.eclipse.lsp4jakarta.jdt.core.java.codeaction.JavaCodeActionResolveContext;
import org.eclipse.lsp4jakarta.jdt.core.java.corrections.proposal.ChangeCorrectionProposal;
import org.eclipse.lsp4jakarta.jdt.core.java.corrections.proposal.ImplementInterfaceProposal;
import org.eclipse.lsp4jakarta.jdt.internal.Messages;

/**
 * Inserts Filter implementation.
 */
public class FilterImplementationQuickFix implements IJavaCodeActionParticipant {
	private static final Logger LOGGER = Logger.getLogger(FilterImplementationQuickFix.class.getName());

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getParticipantId() {
		return FilterImplementationQuickFix.class.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<? extends CodeAction> getCodeActions(JavaCodeActionContext context, Diagnostic diagnostic,
			IProgressMonitor monitor) throws CoreException {
		ASTNode node = context.getCoveredNode();
		ITypeBinding parentType = Bindings.getBindingOfParentType(node);
		List<CodeAction> codeActions = new ArrayList<>();
		if (parentType != null) {
			ExtendedCodeAction codeAction = new ExtendedCodeAction(getLabel(Constants.FILTER, parentType.getName()));
			codeAction.setRelevance(0);
			codeAction.setKind(CodeActionKind.QuickFix);
			codeAction.setDiagnostics(Arrays.asList(diagnostic));
			codeAction.setData(new CodeActionResolveData(context.getUri(), getParticipantId(),
					context.getParams().getRange(), null, context.getParams().isResourceOperationSupported(),
					context.getParams().isCommandConfigurationUpdateSupported(),
					JakartaCodeActionId.ServletFilterImplementation));
			codeActions.add(codeAction);
		}

		return codeActions;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CodeAction resolveCodeAction(JavaCodeActionResolveContext context) {
		CodeAction toResolve = context.getUnresolved();
		ASTNode node = context.getCoveredNode();
		ITypeBinding parentType = Bindings.getBindingOfParentType(node);
		String label = getLabel(Constants.FILTER, parentType.getName());

		ChangeCorrectionProposal proposal = new ImplementInterfaceProposal(label,
				context.getCompilationUnit(), parentType,
				context.getASTRoot(), "jakarta.servlet.Filter", 0);
		try {
			toResolve.setEdit(context.convertToWorkspaceEdit(proposal));
		} catch (CoreException e) {
			LOGGER.log(Level.SEVERE, "Unable to resolve code action edit to implement Filter.",
					e);
		}

		return toResolve;
	}

	/**
	 * Returns the code action label.
	 * 
	 * @param interfaceName The interface name.
	 * @param interfaceType The type interface type.
	 * 
	 * @return The code action label.
	 */
	@SuppressWarnings("restriction")
	private String getLabel(String interfaceName, String interfaceType) {
		return Messages.getMessage("LetClassImplement",
				org.eclipse.jdt.internal.core.manipulation.util.BasicElementLabels.getJavaElementName(interfaceType),
				org.eclipse.jdt.internal.core.manipulation.util.BasicElementLabels
						.getJavaElementName(interfaceName));
	}
}