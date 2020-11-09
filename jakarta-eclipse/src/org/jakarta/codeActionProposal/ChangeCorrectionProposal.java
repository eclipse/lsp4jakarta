/*******************************************************************************
 * Copyright (c) 2000, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copied from /org.eclipse.jdt.ui/src/org/eclipse/jdt/ui/text/java/correction/ChangeCorrectionProposal.java
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.jakarta.codeActionProposal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite;
import org.eclipse.jdt.core.manipulation.CUCorrectionProposalCore;
import org.eclipse.jdt.core.manipulation.ChangeCorrectionProposalCore;
import org.eclipse.jdt.core.manipulation.CodeStyleConfiguration;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.IUndoManager;
import org.eclipse.ltk.core.refactoring.RefactoringCore;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.text.edits.TextEdit;
import org.jakarta.lsp4e.Activator;

/**
 * Adapted from
 * /org.eclipse.jdt.ui/src/org/eclipse/jdt/ui/text/java/correction/ChangeCorrectionProposal.java
 * and
 * https://github.com/eclipse/lsp4mp/blob/55ac697e9382db279480d992a993f17bc7a92c60/microprofile.jdt/org.eclipse.lsp4mp.jdt.core/src/main/java/org/eclipse/lsp4mp/jdt/core/java/corrections/proposal/ChangeCorrectionProposal.java
 */
public class ChangeCorrectionProposal extends ChangeCorrectionProposalCore {

	// LSP: Code Action Kind
	private String fKind;
	private ASTRewrite fRewrite;
	private ImportRewrite fImportRewrite;
	private CUCorrectionProposalCore fProposalCore;

	/**
	 * Constructs a change correction proposal.
	 *
	 * @param name   the name that is displayed in the proposal selection dialog
	 * @param change the change that is executed when the proposal is applied or
	 *               <code>null</code> if the change will be created by implementors
	 *               of {@link #createChange()}
	 */
	public ChangeCorrectionProposal(String name, String kind, ICompilationUnit cu, ASTRewrite rewrite, int relevance) {
		super(name, null, 0);
		fKind = kind;
		if (cu == null) {
			throw new IllegalArgumentException("Compilation unit must not be null"); //$NON-NLS-1$
		}

		fRewrite = rewrite;
		fProposalCore = new CUCorrectionProposalCore(name, cu, null, relevance);
	}

	/**
	 * Performs the change associated with this proposal.
	 * <p>
	 * Subclasses may extend, but must call the super implementation.
	 *
	 * @throws CoreException when the invocation of the change failed
	 */
	@Override
	protected void performChange() throws CoreException {
		Change change = null;
		try {
			change = getChange();
			if (change != null) {

				change.initializeValidationData(new NullProgressMonitor());
				RefactoringStatus valid = change.isValid(new NullProgressMonitor());
				if (valid.hasFatalError()) {
					IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, IStatus.ERROR,
							valid.getMessageMatchingSeverity(RefactoringStatus.FATAL), null);
					throw new CoreException(status);
				} else {
					IUndoManager manager = RefactoringCore.getUndoManager();
					Change undoChange;
					boolean successful = false;
					try {
						manager.aboutToPerformChange(change);
						undoChange = change.perform(new NullProgressMonitor());
						successful = true;
					} finally {
						manager.changePerformed(change, successful);
					}
					if (undoChange != null) {
						undoChange.initializeValidationData(new NullProgressMonitor());
						manager.addUndo(getName(), undoChange);
					}
				}
			}
		} finally {

			if (change != null) {
				change.dispose();
			}
		}
	}

	@Override
	public Object getAdditionalProposalInfo(IProgressMonitor monitor) {
		return fProposalCore.getAdditionalProposalInfo(monitor);
	}

	@Override
	public void apply() throws CoreException {
		performChange();
	}

	@Override
	protected final Change createChange() throws CoreException {
		return createTextChange(); // make sure that only text changes are allowed here
	}

	/**
	 * Returns the kind of the proposal.
	 *
	 * @return the kind of the proposal
	 */
	public String getKind() {
		return fKind;
	}

	/**
	 * @param codeActionKind the Code Action Kind to set
	 */
	public void setKind(String codeActionKind) {
		this.fKind = codeActionKind;
	}

	/**
	 * The compilation unit on which the change works.
	 *
	 * @return the compilation unit on which the change works
	 */
	public final ICompilationUnit getCompilationUnit() {
		return fProposalCore.getCompilationUnit();
	}
	
	/**
	 * Returns the text change that is invoked when the change is applied.
	 *
	 * @return the text change that is invoked when the change is applied
	 * @throws CoreException if accessing the change failed
	 */
	public final TextChange getTextChange() throws CoreException {
		return (TextChange) getChange();
	}

	/**
	 * Creates the text change for this proposal. This method is only called once
	 * and only when no text change has been passed in
	 * {@link #CUCorrectionProposal(String, ICompilationUnit, TextChange, int)}.
	 *
	 * @return the created text change
	 * @throws CoreException if the creation of the text change failed
	 */
	protected TextChange createTextChange() throws CoreException {
		TextChange change = fProposalCore.getNewChange();
		// initialize text change
		IDocument document = change.getCurrentDocument(new NullProgressMonitor());
		addEdits(document, change.getEdit());
		return change;
	}

	/**
	 * Creates a preview of the content of the compilation unit after applying the
	 * change.
	 *
	 * @return the preview of the changed compilation unit
	 * @throws CoreException if the creation of the change failed
	 *
	 * @noreference This method is not intended to be referenced by clients.
	 */
	public String getPreviewContent() throws CoreException {
		return getTextChange().getPreviewContent(new NullProgressMonitor());
	}

	/**
	 * Returns the import rewrite used for this compilation unit.
	 *
	 * @return the import rewrite or <code>null</code> if no import rewrite has been
	 *         set
	 * @nooverride This method is not intended to be re-implemented or extended by
	 *             clients.
	 */
	public ImportRewrite getImportRewrite() {
		return fImportRewrite;
	}

	/**
	 * Sets the import rewrite used for this compilation unit.
	 *
	 * @param rewrite the import rewrite
	 * @nooverride This method is not intended to be re-implemented or extended by
	 *             clients.
	 */
	public void setImportRewrite(ImportRewrite rewrite) {
		fImportRewrite = rewrite;
	}

	/**
	 * Creates and sets the import rewrite used for this compilation unit.
	 *
	 * @param astRoot the AST for the current CU
	 * @return the created import rewrite
	 * @nooverride This method is not intended to be re-implemented or extended by
	 *             clients.
	 */
	public ImportRewrite createImportRewrite(CompilationUnit astRoot) {
		fImportRewrite = CodeStyleConfiguration.createImportRewrite(astRoot, true);
		return fImportRewrite;
	}

	protected void addEdits(IDocument document, TextEdit editRoot) throws CoreException {
		ASTRewrite rewrite = getRewrite();
		if (rewrite != null) {
			try {
				TextEdit edit = rewrite.rewriteAST();
				editRoot.addChild(edit);
			} catch (IllegalArgumentException e) {
				Activator.log(new Status(IStatus.ERROR,
	                    Activator.getDefault().getBundle().getSymbolicName(),"Invalid AST Rewriter", e));
			}
		}
		if (fImportRewrite != null) {
			editRoot.addChild(fImportRewrite.rewriteImports(new NullProgressMonitor()));
		}
	}

	/**
	 * Returns the rewrite that has been passed in the constructor. Implementors can
	 * override this method to create the rewrite lazily. This method will only be
	 * called once.
	 *
	 * @return the rewrite to be used
	 * @throws CoreException when the rewrite could not be created
	 */
	protected ASTRewrite getRewrite() throws CoreException {
		if (fRewrite == null) {
			Activator.log(new Status(IStatus.ERROR,
                    Activator.getDefault().getBundle().getSymbolicName(),"Invalid AST Rewrite"));
		}
		return fRewrite;
	}
}