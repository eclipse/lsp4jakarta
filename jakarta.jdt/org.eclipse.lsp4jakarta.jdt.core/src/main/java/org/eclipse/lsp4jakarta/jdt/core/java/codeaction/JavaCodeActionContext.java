/*******************************************************************************
* Copyright (c) 2020 Red Hat Inc. and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
* which is available at https://www.apache.org/licenses/LICENSE-2.0.
*
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
*
* Contributors:
*     Red Hat Inc. - initial API and implementation
*******************************************************************************/
package org.eclipse.lsp4jakarta.jdt.core.java.codeaction;

import java.util.Arrays;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.WorkspaceEdit;
import org.eclipse.lsp4jakarta.commons.JakartaJavaCodeActionParams;
import org.eclipse.lsp4jakarta.jdt.core.java.AbtractJavaContext;
import org.eclipse.lsp4jakarta.jdt.core.java.corrections.proposal.ChangeCorrectionProposal;
import org.eclipse.lsp4jakarta.jdt.core.utils.IJDTUtils;
import org.eclipse.lsp4jakarta.jdt.internal.core.java.ChangeUtil;

/**
 * Java codeAction context for a given compilation unit.
 *
 * @author Angelo ZERR
 *
 */
public class JavaCodeActionContext extends AbtractJavaContext implements IInvocationContext {

    private final int selectionOffset;
    private final int selectionLength;

    private final JakartaJavaCodeActionParams params;
    private NodeFinder fNodeFinder;

    public JavaCodeActionContext(ITypeRoot typeRoot, int selectionOffset, int selectionLength, IJDTUtils utils,
                                 JakartaJavaCodeActionParams params) {
        super(params.getUri(), typeRoot, utils);
        this.selectionOffset = selectionOffset;
        this.selectionLength = selectionLength;
        this.params = params;
    }

    public JakartaJavaCodeActionParams getParams() {
        return params;
    }

    @Override
    public ICompilationUnit getCompilationUnit() {
        return (ICompilationUnit) getTypeRoot();
    }

    /**
     * Returns the length.
     *
     * @return int
     */
    @Override
    public int getSelectionLength() {
        return selectionLength;
    }

    /**
     * Returns the offset.
     *
     * @return int
     */
    @Override
    public int getSelectionOffset() {
        return selectionOffset;
    }

    @Override
    public ASTNode getCoveringNode() {
        if (fNodeFinder == null) {
            fNodeFinder = new NodeFinder(getASTRoot(), selectionOffset, selectionLength);
        }
        return fNodeFinder.getCoveringNode();
    }

    @Override
    public ASTNode getCoveredNode() {
        if (fNodeFinder == null) {
            fNodeFinder = new NodeFinder(getASTRoot(), selectionOffset, selectionLength);
        }
        return fNodeFinder.getCoveredNode();
    }

    public CodeAction convertToCodeAction(ChangeCorrectionProposal proposal, Diagnostic... diagnostics) throws CoreException {
        String name = proposal.getName();
        WorkspaceEdit edit = ChangeUtil.convertToWorkspaceEdit(proposal.getChange(), getUri(), getUtils(),
                                                               params.isResourceOperationSupported());
        if (!ChangeUtil.hasChanges(edit)) {
            return null;
        }
        ExtendedCodeAction codeAction = new ExtendedCodeAction(name);
        codeAction.setRelevance(proposal.getRelevance());
        codeAction.setKind(proposal.getKind());
        codeAction.setEdit(edit);
        codeAction.setDiagnostics(Arrays.asList(diagnostics));
        return codeAction;
    }

    public WorkspaceEdit convertToWorkspaceEdit(ChangeCorrectionProposal proposal) throws CoreException {
        WorkspaceEdit edit = ChangeUtil.convertToWorkspaceEdit(proposal.getChange(), getUri(), getUtils(),
                                                               params.isResourceOperationSupported());
        if (!ChangeUtil.hasChanges(edit)) {
            return null;
        }
        return edit;
    }

}
