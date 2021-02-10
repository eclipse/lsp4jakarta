/*******************************************************************************
 * Copyright (c) 2000, 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copied from https://github.com/eclipse/lsp4mp/blob/6f2d700a88a3262e39cc2ba04beedb429e162246/microprofile.jdt/org.eclipse.lsp4mp.jdt.core/src/main/java/org/eclipse/lsp4mp/jdt/core/java/corrections/proposal/NewAnnotationProposal.java
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.jakarta.codeAction.proposal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite.ImportRewriteContext;
import org.eclipse.jdt.internal.core.manipulation.dom.ASTResolving;
import org.eclipse.jdt.internal.corext.codemanipulation.ContextSensitiveImportRewriteContext;
import org.eclipse.lsp4j.CodeActionKind;

/**
 * Code action proposal for annotation reused from
 * https://github.com/eclipse/lsp4mp/blob/6f2d700a88a3262e39cc2ba04beedb429e162246/microprofile.jdt/org.eclipse.lsp4mp.jdt.core/src/main/java/org/eclipse/lsp4mp/jdt/core/java/corrections/proposal/NewAnnotationProposal.java
 */
public class NewAnnotationProposal extends ChangeCorrectionProposal {

    private final CompilationUnit fInvocationNode;
    private final IBinding fBinding;

    private final String[] annotations;

    public NewAnnotationProposal(String label, ICompilationUnit targetCU, CompilationUnit invocationNode,
            IBinding binding, int relevance, String... annotations) {
        super(label, CodeActionKind.QuickFix, targetCU, null, relevance);
        fInvocationNode = invocationNode;
        fBinding = binding;
        this.annotations = annotations;
    }

    @Override
    protected ASTRewrite getRewrite() throws CoreException {
        ASTNode declNode = null;
        ASTNode boundNode = fInvocationNode.findDeclaringNode(fBinding);
        CompilationUnit newRoot = fInvocationNode;
        if (boundNode != null) {
            declNode = boundNode; // is same CU
        } else {
            newRoot = ASTResolving.createQuickFixAST(getCompilationUnit(), null);
            declNode = newRoot.findDeclaringNode(fBinding.getKey());
        }
        ImportRewrite imports = createImportRewrite(newRoot);

        boolean isField = declNode instanceof VariableDeclarationFragment;
        if (isField) {
            declNode = declNode.getParent();
        }
        if (declNode instanceof TypeDeclaration || isField) {
            AST ast = declNode.getAST();
            ASTRewrite rewrite = ASTRewrite.create(ast);

            ImportRewriteContext importRewriteContext = new ContextSensitiveImportRewriteContext(declNode, imports);

            for (String annotation : annotations) {
                Annotation marker = ast.newMarkerAnnotation();
                marker.setTypeName(ast.newName(imports.addImport(annotation, importRewriteContext))); // $NON-NLS-1$
                rewrite.getListRewrite(declNode,
                        isField ? FieldDeclaration.MODIFIERS2_PROPERTY : TypeDeclaration.MODIFIERS2_PROPERTY)
                        .insertFirst(marker, null);
            }

            return rewrite;
        }
        return null;
    }

    /**
     * Returns the Compilation Unit node
     * 
     * @return the invocation node for the Compilation Unit
     */
    protected CompilationUnit getInvocationNode() {
        return this.fInvocationNode;
    }

    /**
     * Returns the Binding object associated with the new annotation change
     * 
     * @return the binding object
     */
    protected IBinding getBinding() {
        return this.fBinding;
    }

    /**
     * Returns the annotations list
     * 
     * @return the list of new annotations to add
     */
    protected String[] getAnnotations() {
        return this.annotations;
    }
}