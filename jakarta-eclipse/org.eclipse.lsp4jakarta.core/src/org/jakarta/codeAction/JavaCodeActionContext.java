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

package org.jakarta.codeAction;

import java.util.Arrays;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.WorkspaceEdit;

import org.jakarta.jdt.JDTUtils;
import org.jakarta.codeAction.proposal.ChangeCorrectionProposal;
import org.jakarta.jdt.ChangeUtil;

import io.microshed.jakartals.commons.JakartaJavaCodeActionParams;

/**
 * Java codeAction context for a given compilation unit. Reused from
 * https://github.com/eclipse/lsp4mp/blob/b88710cc54170844717f655b9bff8bb4c4649a8d/microprofile.jdt/org.eclipse.lsp4mp.jdt.core/src/main/java/org/eclipse/lsp4mp/jdt/core/java/codeaction/JavaCodeActionContext.java
 * 
 * @author credit to Angelo ZERR
 *
 */
public class JavaCodeActionContext extends AbstractJavaContext implements IInvocationContext {

    private final int selectionOffset;
    private final int selectionLength;

    private final JakartaJavaCodeActionParams params;
    private NodeFinder fNodeFinder;

    public JavaCodeActionContext(ITypeRoot typeRoot, int selectionOffset, int selectionLength, JDTUtils utils,
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

    public CodeAction convertToCodeAction(ChangeCorrectionProposal proposal, Diagnostic... diagnostics)
            throws CoreException {
        String name = proposal.getName();
        WorkspaceEdit edit = ChangeUtil.convertToWorkspaceEdit(proposal.getChange(), getUri(), getUtils(),
                params.isResourceOperationSupported());
        if (!ChangeUtil.hasChanges(edit)) {
            return null;
        }
        CodeAction codeAction = new CodeAction();
        codeAction.setTitle(name);
        codeAction.setKind(proposal.getKind());
        codeAction.setEdit(edit);
        codeAction.setDiagnostics(Arrays.asList(diagnostics));
        return codeAction;
    }

}
