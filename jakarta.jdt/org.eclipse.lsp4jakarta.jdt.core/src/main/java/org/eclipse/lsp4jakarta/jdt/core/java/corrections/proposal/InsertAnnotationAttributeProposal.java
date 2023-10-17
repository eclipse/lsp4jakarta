/*******************************************************************************
 * Copyright (c) 2000, 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copied from /org.eclipse.jdt.ui/src/org/eclipse/jdt/internal/ui/text/correction/proposals/MissingAnnotationAttributesProposal.java
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.lsp4jakarta.jdt.core.java.corrections.proposal;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite.ImportRewriteContext;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.internal.core.manipulation.dom.ASTResolving;
import org.eclipse.jdt.internal.corext.codemanipulation.ContextSensitiveImportRewriteContext;
import org.eclipse.lsp4j.CodeActionKind;

public class InsertAnnotationAttributeProposal extends LinkedCorrectionProposal {

    private final Annotation fAnnotation;
    private final Set<String> attributes;

    public InsertAnnotationAttributeProposal(String label, ICompilationUnit targetCU, Annotation annotation,
                                             int relevance, String... attributes) {
        super(label, CodeActionKind.QuickFix, targetCU, null, relevance);
        this.fAnnotation = annotation;
        this.attributes = new HashSet<>(Arrays.asList(attributes));
    }

    @Override
    protected ASTRewrite getRewrite() throws CoreException {
        AST ast = fAnnotation.getAST();

        ASTRewrite rewrite = ASTRewrite.create(ast);
        createImportRewrite((CompilationUnit) fAnnotation.getRoot());

        ListRewrite listRewrite;
        if (fAnnotation instanceof NormalAnnotation) {
            listRewrite = rewrite.getListRewrite(fAnnotation, NormalAnnotation.VALUES_PROPERTY);
        } else {
            NormalAnnotation newAnnotation = ast.newNormalAnnotation();
            newAnnotation.setTypeName((Name) rewrite.createMoveTarget(fAnnotation.getTypeName()));
            rewrite.replace(fAnnotation, newAnnotation, null);

            listRewrite = rewrite.getListRewrite(newAnnotation, NormalAnnotation.VALUES_PROPERTY);
        }
        addDefinedAtributes(fAnnotation.resolveTypeBinding(), listRewrite);

        return rewrite;
    }

    private void addDefinedAtributes(ITypeBinding binding, ListRewrite listRewriter) {
        Set<String> implementedAttribs = new HashSet<String>();
        if (fAnnotation instanceof NormalAnnotation) {
            List<MemberValuePair> list = ((NormalAnnotation) fAnnotation).values();
            for (int i = 0; i < list.size(); i++) {
                MemberValuePair curr = list.get(i);
                implementedAttribs.add(curr.getName().getIdentifier());
            }
        } else if (fAnnotation instanceof SingleMemberAnnotation) {
            implementedAttribs.add("value"); //$NON-NLS-1$
        }
        ASTRewrite rewriter = listRewriter.getASTRewrite();
        AST ast = rewriter.getAST();
        ImportRewriteContext context = null;
        ASTNode bodyDeclaration = ASTResolving.findParentBodyDeclaration(listRewriter.getParent());
        if (bodyDeclaration != null) {
            context = new ContextSensitiveImportRewriteContext(bodyDeclaration, getImportRewrite());
        }

        IMethodBinding[] declaredMethods = binding.getDeclaredMethods();
        for (int i = 0; i < declaredMethods.length; i++) {
            IMethodBinding curr = declaredMethods[i];
            if (!implementedAttribs.contains(curr.getName()) && attributes.contains(curr.getName())) {
                MemberValuePair pair = ast.newMemberValuePair();
                pair.setName(ast.newSimpleName(curr.getName()));
                pair.setValue(newDefaultExpression(ast, curr.getReturnType(), context));
                listRewriter.insertLast(pair, null);
            }
        }
    }

    private Expression newDefaultExpression(AST ast, ITypeBinding type, ImportRewriteContext context) {
        if (type.isPrimitive()) {
            String name = type.getName();
            if ("boolean".equals(name)) { //$NON-NLS-1$
                return ast.newBooleanLiteral(false);
            } else {
                return ast.newNumberLiteral("0"); //$NON-NLS-1$
            }
        }
        if (type == ast.resolveWellKnownType("java.lang.String")) { //$NON-NLS-1$
            return ast.newStringLiteral();
        }
        if (type.isArray()) {
            ArrayInitializer initializer = ast.newArrayInitializer();
            initializer.expressions().add(newDefaultExpression(ast, type.getElementType(), context));
            return initializer;
        }
        if (type.isAnnotation()) {
            MarkerAnnotation annotation = ast.newMarkerAnnotation();
            annotation.setTypeName(ast.newName(getImportRewrite().addImport(type, context)));
            return annotation;
        }
        return ast.newNullLiteral();
    }

}
