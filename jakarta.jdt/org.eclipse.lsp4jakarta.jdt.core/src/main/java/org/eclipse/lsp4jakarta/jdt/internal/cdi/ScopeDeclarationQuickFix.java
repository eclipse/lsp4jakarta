
package org.eclipse.lsp4jakarta.jdt.internal.cdi;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4jakarta.commons.codeaction.ICodeActionId;
import org.eclipse.lsp4jakarta.commons.codeaction.JakartaCodeActionId;
import org.eclipse.lsp4jakarta.jdt.core.java.codeaction.JavaCodeActionContext;
import org.eclipse.lsp4jakarta.jdt.core.java.codeaction.RemoveAnnotationConflictQuickFix;

import com.google.gson.JsonArray;

/**
 * Removes all scope declaration annotations with the exception of the currently
 * active one.
 */
public class ScopeDeclarationQuickFix extends RemoveAnnotationConflictQuickFix {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getParticipantId() {
		return ScopeDeclarationQuickFix.class.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ICodeActionId getCodeActionId() {
		return JakartaCodeActionId.CDIRemoveScopeDeclarationAnnotationsButOne;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<? extends CodeAction> getCodeActions(JavaCodeActionContext context, Diagnostic diagnostic,
			IProgressMonitor monitor) throws CoreException {
		List<CodeAction> codeActions = new ArrayList<>();
		ASTNode node = context.getCoveredNode();
		IBinding parentType = getBinding(node);

		if (parentType != null) {
			JsonArray diagnosticData = (JsonArray) diagnostic.getData();
			List<String> annotations = IntStream.range(0, diagnosticData.size())
					.mapToObj(idx -> diagnosticData.get(idx).getAsString()).collect(Collectors.toList());

			annotations.remove(Constants.PRODUCES);
			for (String annotation : annotations) {
				List<String> resultingAnnotations = new ArrayList<>(annotations);
				resultingAnnotations.remove(annotation);

				createCodeAction(diagnostic, context, parentType, codeActions,
						resultingAnnotations.toArray(new String[] {}));
			}
		}

		return codeActions;
	}
}
