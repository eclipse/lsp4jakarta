package org.jakarta.jdt.cdi;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.internal.corext.dom.Bindings;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;
import org.jakarta.codeAction.JavaCodeActionContext;
import org.jakarta.jdt.servlet.InsertAnnotationMissingQuickFix;

public class ManagedBeanConstructorQuickFix extends InsertAnnotationMissingQuickFix  {
	public ManagedBeanConstructorQuickFix() {
        super("jakarta.inject.Inject");
    }
}
