package org.eclipse.lsp4jakarta.jdt.core.annotations;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.internal.corext.dom.Bindings;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4jakarta.jdt.codeAction.IJavaCodeActionParticipant;
import org.eclipse.lsp4jakarta.jdt.codeAction.JavaCodeActionContext;
import org.eclipse.lsp4jakarta.jdt.codeAction.proposal.ModifyModifiersProposal;
import org.eclipse.lsp4jakarta.jdt.codeAction.proposal.quickfix.RemoveAnnotationConflictQuickFix;


public class RemovePreDestroyAnnotationQuickFix extends RemoveAnnotationConflictQuickFix{
	public RemovePreDestroyAnnotationQuickFix() {
        super(false, "jakarta.annotation.PreDestroy");
    }

}
