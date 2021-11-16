package org.eclipse.lsp4jakarta.jdt.core.annotations;

import org.eclipse.lsp4jakarta.jdt.codeAction.proposal.quickfix.AddAnnotationMissingQuickFix;

public class AddResourceMissingTypeQuickFix extends AddAnnotationMissingQuickFix{
	/** This is still a simple version of doing adding missing attributes, not perfect, one has
	 * to specify the name of annotation
	 * 
	 */
	public AddResourceMissingTypeQuickFix() {
		super("jakarta.annotation.Resource",false,"type");
	}

}
