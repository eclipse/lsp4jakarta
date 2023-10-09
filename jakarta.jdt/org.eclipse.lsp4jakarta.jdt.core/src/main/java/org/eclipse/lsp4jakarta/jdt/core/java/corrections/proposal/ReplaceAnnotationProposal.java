/*******************************************************************************
 * Copyright (c) 2000, 2016 IBM Corporation and others.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
* which is available at https://www.apache.org/licenses/LICENSE-2.0.
*
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 *
 * Copied from /org.eclipse.jdt.ui/src/org/eclipse/jdt/internal/ui/text/correction/proposals/NewAnnotationMemberProposal.java
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.lsp4jakarta.jdt.core.java.corrections.proposal;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite.ImportRewriteContext;
import org.eclipse.jdt.internal.core.manipulation.dom.ASTResolving;
import org.eclipse.jdt.internal.corext.codemanipulation.ContextSensitiveImportRewriteContext;

/**
 * Similar functionality as NewAnnotationProposal. The main difference is that
 * first removes specified annotations before adding a new annotation.
 *
 * Note: This class only accepts one new annotation to add.
 *
 * @author Kathryn Kodama
 */
public class ReplaceAnnotationProposal extends InsertAnnotationProposal {

    private final String[] removeAnnotations;

    public ReplaceAnnotationProposal(String label, ICompilationUnit targetCU, CompilationUnit invocationNode,
                                     IBinding binding, int relevance, String annotation, String... removeAnnotations) {
        super(label, targetCU, invocationNode, binding, relevance, annotation);
        this.removeAnnotations = removeAnnotations;
    }

    @Override
    protected ASTRewrite getRewrite() throws CoreException {
        CompilationUnit fInvocationNode = getInvocationNode();
        IBinding fBinding = getBinding();
        String[] annotations = getAnnotations();

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

            // remove annotations in the removeAnnotations list
            @SuppressWarnings("unchecked")
            List<? extends ASTNode> children = (List<? extends ASTNode>) declNode.getStructuralProperty(TypeDeclaration.MODIFIERS2_PROPERTY);
            for (ASTNode child : children) {
                if (child instanceof Annotation) {
                    Annotation annotation = (Annotation) child;
                    IAnnotationBinding annotationBinding = annotation.resolveAnnotationBinding();
                    boolean containsAnnotation = Arrays.stream(removeAnnotations).anyMatch(annotationBinding.getName()::contains);
                    if (containsAnnotation) {
                        rewrite.remove(child, null);
                    }
                }
            }
            for (String annotation : annotations) {
                Annotation marker = ast.newMarkerAnnotation();
                marker.setTypeName(ast.newName(imports.addImport(annotation, importRewriteContext))); // $NON-NLS-1$
                rewrite.getListRewrite(declNode,
                                       isField ? FieldDeclaration.MODIFIERS2_PROPERTY : TypeDeclaration.MODIFIERS2_PROPERTY).insertFirst(marker, null);
            }

            return rewrite;
        }
        return null;
    }

}