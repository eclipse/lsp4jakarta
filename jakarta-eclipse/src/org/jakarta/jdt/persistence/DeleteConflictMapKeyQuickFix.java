package org.jakarta.jdt.persistence;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;
import org.jakarta.codeAction.JavaCodeActionContext;
import org.jakarta.codeAction.proposal.ChangeCorrectionProposal;
import org.jakarta.codeAction.proposal.DeleteAnnotationProposal;

public class DeleteConflictMapKeyQuickFix extends RemoveAnnotationConflictQuickFix {
	
	public DeleteConflictMapKeyQuickFix() {
		super("jakarta.persistence.annotation.MapKey");
	}
	
	@Override
	protected void removeAnnotations(Diagnostic diagnostic, JavaCodeActionContext context, IBinding parentType,
            List<CodeAction> codeActions) throws CoreException {
		String[] annotations = getAnnotations();
		if(diagnostic.getCode().getLeft().equals(PersistenceConstants.DIAGNOSTIC_CODE_INVALID_ANNOTATION)) {
			String name = getLabel(annotations);
	        ChangeCorrectionProposal proposal = new DeleteAnnotationProposal(name, context.getCompilationUnit(),
	                context.getASTRoot(), parentType, 0, annotations);
	        // Convert the proposal to LSP4J CodeAction
	        CodeAction codeAction = context.convertToCodeAction(proposal, diagnostic);
	        codeAction.setTitle(name);
	        if (codeAction != null) {
	            codeActions.add(codeAction);
	        }
		}
	}
	
	private static String getLabel(String[] annotations) {
        StringBuilder name = new StringBuilder("Remove ");
        for (int i = 0; i < annotations.length; i++) {
            String annotation = annotations[i];
            String annotationName = annotation.substring(annotation.lastIndexOf('.') + 1, annotation.length());
            if (i > 0) {
                name.append(", ");
            }
            name.append("@");
            name.append(annotationName);
        }
        return name.toString();
    }
}
