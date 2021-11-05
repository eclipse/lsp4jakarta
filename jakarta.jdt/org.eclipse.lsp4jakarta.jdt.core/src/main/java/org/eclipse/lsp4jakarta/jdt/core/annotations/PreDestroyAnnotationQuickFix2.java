package org.eclipse.lsp4jakarta.jdt.core.annotations;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.internal.corext.dom.Bindings;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4jakarta.jdt.codeAction.IJavaCodeActionParticipant;
import org.eclipse.lsp4jakarta.jdt.codeAction.JavaCodeActionContext;
import org.eclipse.lsp4jakarta.jdt.codeAction.proposal.ChangeCorrectionProposal;
import org.eclipse.lsp4jakarta.jdt.codeAction.proposal.ModifyModifiersProposal;
import org.eclipse.lsp4jakarta.jdt.codeAction.proposal.RemoveParamsProposal;
import org.eclipse.lsp4jakarta.jdt.core.annotations.AnnotationConstants;
import org.eclipse.lsp4jakarta.jdt.core.beanvalidation.BeanValidationConstants;
import org.eclipse.lsp4jakarta.jdt.codeAction.proposal.quickfix.RemoveModifierConflictQuickFix;

public class PreDestroyAnnotationQuickFix2 extends RemoveModifierConflictQuickFix {
	public PreDestroyAnnotationQuickFix2() {
        super(false, "static");
    }

}
