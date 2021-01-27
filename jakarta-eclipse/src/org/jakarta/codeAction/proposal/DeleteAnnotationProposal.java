package org.jakarta.codeAction.proposal;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;

/**
 * 
 * Code action proposal for deleting an existing annotation. 
 *
 */

public class DeleteAnnotationProposal extends ChangeCorrectionProposal {
	 private final CompilationUnit fInvocationNode;
	 private final IBinding fBinding;

	 private final String[] annotations;
	 public DeleteAnnotationProposal(String name, String kind, ICompilationUnit cu, ASTRewrite rewrite, int relevance) {
		 super(name, kind, cu, rewrite, relevance);
		 // TODO Auto-generated constructor stub
	 }
	
}
