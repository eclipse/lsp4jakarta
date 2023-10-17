/*******************************************************************************
 * Copyright (c) 2021, 2022 IBM Corporation and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.lsp4jakarta.jdt.core.java.corrections.proposal;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.ChildListPropertyDescriptor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite.ImportRewriteContext;
import org.eclipse.jdt.internal.core.manipulation.dom.ASTResolving;
import org.eclipse.jdt.internal.corext.codemanipulation.ContextSensitiveImportRewriteContext;

/**
 *
 * Code action proposal for modifying an existing annotation. Option for adding
 * additional string attributes and option for removing string attributes from
 * specified annotation.
 *
 * @author Kathryn Kodama
 *
 */
public class ModifyAnnotationProposal extends InsertAnnotationProposal {

    // list of attributes to add to the annotations
    private final List<String> attributesToAdd;

    // list of attributes (if they exist) to remove from the annotations
    private final List<String> attributesToRemove;

    public ModifyAnnotationProposal(String label, ICompilationUnit targetCU, CompilationUnit invocationNode,
                                    IBinding binding, int relevance, String annotation, List<String> attributesToAdd,
                                    List<String> attributesToRemove) {
        super(label, targetCU, invocationNode, binding, relevance, annotation);
        this.attributesToAdd = attributesToAdd;
        this.attributesToRemove = attributesToRemove;
    }

    public ModifyAnnotationProposal(String label, ICompilationUnit targetCU, CompilationUnit invocationNode,
                                    IBinding binding, int relevance, String annotation, List<String> attributesToAdd) {
        super(label, targetCU, invocationNode, binding, relevance, annotation);
        this.attributesToAdd = attributesToAdd;
        this.attributesToRemove = new ArrayList<>();
    }

    public ModifyAnnotationProposal(String label, ICompilationUnit targetCU, CompilationUnit invocationNode,
                                    IBinding binding, int relevance, List<String> attributesToAdd, String... annotations) {
        super(label, targetCU, invocationNode, binding, relevance, annotations);
        this.attributesToAdd = attributesToAdd;
        this.attributesToRemove = new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected ASTRewrite getRewrite() throws CoreException {
        CompilationUnit fInvocationNode = getInvocationNode();
        IBinding fBinding = getBinding();
        String[] annotations = getAnnotations();

        // get short name of annotations
        String[] annotationShortNames = new String[annotations.length];
        for (int i = 0; i < annotations.length; i++) {
            String shortName = annotations[i].substring(annotations[i].lastIndexOf(".") + 1, annotations[i].length());
            annotationShortNames[i] = shortName;
        }

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
        boolean isSingleVarDecl = declNode instanceof SingleVariableDeclaration;

        if (isField) {
            declNode = declNode.getParent();
        }

        if (declNode.getNodeType() == ASTNode.FIELD_DECLARATION) {
            AST ast = declNode.getAST();
            ASTRewrite rewrite = ASTRewrite.create(ast);

            ImportRewriteContext importRewriteContext = new ContextSensitiveImportRewriteContext(declNode, imports);
            List<Annotation> existingAnnotations = new ArrayList<Annotation>();

            List<? extends ASTNode> children = (List<? extends ASTNode>) declNode.getStructuralProperty(FieldDeclaration.MODIFIERS2_PROPERTY);

            // for all existing annotations (that are the annotation we want)
            for (ASTNode child : children) {
                if (child instanceof Annotation) {
                    Annotation annotation = (Annotation) child;
                    boolean containsAnnotation = Arrays.stream(annotationShortNames).anyMatch(annotation.getTypeName().toString()::contains);

                    // check if current child annotation has all attributes to add already or any to
                    // remove
                    if (containsAnnotation && child instanceof NormalAnnotation) {
                        List<String> existingValues = (List<String>) ((NormalAnnotation) child).values().stream().map(mvp -> ((MemberValuePair) mvp).getName().toString()).collect(toList());

                        boolean containsAllToAdd = this.attributesToAdd.stream().allMatch(attr -> existingValues.stream().anyMatch(v -> v.equals(attr)));
                        boolean containsAnyToRemove = this.attributesToRemove.stream().anyMatch(attr -> existingValues.stream().anyMatch(v -> v.equals(attr)));

                        if (!containsAllToAdd || containsAnyToRemove) {
                            existingAnnotations.add(annotation);
                            rewrite.remove(child, null);
                        }
                    }
                }
            }

            // add new annotations to proposal (restoring those that were removed)
            for (Annotation a : existingAnnotations) {
                if (a instanceof NormalAnnotation) {
                    NormalAnnotation marker = ast.newNormalAnnotation();
                    marker.setTypeName(
                                       ast.newName(imports.addImport(a.getTypeName().toString(), importRewriteContext)));
                    List<MemberValuePair> values = marker.values();

                    // add existing attributes to annotation
                    List<MemberValuePair> existingValues = ((NormalAnnotation) a).values();
                    for (MemberValuePair mvp : existingValues) {
                        boolean removeAttribute = this.attributesToRemove.contains(mvp.getName().getFullyQualifiedName());

                        // do not add attributes to be removed
                        if (!removeAttribute) {
                            MemberValuePair memberValuePair = ast.newMemberValuePair();
                            memberValuePair.setName(ast.newSimpleName(mvp.getName().getFullyQualifiedName()));
                            StringLiteral stringValue = ast.newStringLiteral();

                            if (mvp.getValue() instanceof StringLiteral) {
                                StringLiteral stringLiteral = (StringLiteral) mvp.getValue();
                                stringValue.setLiteralValue(stringLiteral.getLiteralValue());
                            } else {
                                stringValue.setLiteralValue("");
                            }
                            memberValuePair.setValue(stringValue);
                            values.add(memberValuePair);
                        }
                    }

                    // add new attributes
                    for (String newAttr : this.attributesToAdd) {
                        // dont add duplicate attributes to an annotation
                        if (values.stream().noneMatch(v -> v.getName().toString().equals(newAttr))) {
                            MemberValuePair memberValuePair = ast.newMemberValuePair();
                            memberValuePair.setName(ast.newSimpleName(newAttr));
                            StringLiteral stringValue = ast.newStringLiteral();
                            stringValue.setLiteralValue("");
                            memberValuePair.setValue(stringValue);
                            values.add(memberValuePair);
                        }
                    }

                    rewrite.getListRewrite(declNode,
                                           isField ? FieldDeclaration.MODIFIERS2_PROPERTY : TypeDeclaration.MODIFIERS2_PROPERTY).insertFirst(marker, null);
                }
            }

            return rewrite;
        } else if (declNode instanceof TypeDeclaration || isField || isSingleVarDecl) {
            AST ast = declNode.getAST();
            ASTRewrite rewrite = ASTRewrite.create(ast);

            ImportRewriteContext importRewriteContext = new ContextSensitiveImportRewriteContext(declNode, imports);
            List<Annotation> existingAnnotations = new ArrayList<Annotation>();
            ChildListPropertyDescriptor property = isSingleVarDecl ? SingleVariableDeclaration.MODIFIERS2_PROPERTY : TypeDeclaration.MODIFIERS2_PROPERTY;
            List<? extends ASTNode> children = (List<? extends ASTNode>) declNode.getStructuralProperty(property);

            // find and save existing annotation, then remove it from ast
            for (ASTNode child : children) {
                if (child instanceof Annotation) {
                    Annotation annotation = (Annotation) child;
                    boolean containsAnnotation = Arrays.stream(annotationShortNames).anyMatch(annotation.getTypeName().toString()::contains);
                    if (containsAnnotation) {
                        existingAnnotations.add(annotation);
                        rewrite.remove(child, null);
                    }
                }
            }

            // add new annotation with fields from existing annotation
            for (String annotation : annotations) {
                NormalAnnotation marker = ast.newNormalAnnotation();
                marker.setTypeName(ast.newName(imports.addImport(annotation, importRewriteContext)));
                List<MemberValuePair> values = marker.values();

                if (!existingAnnotations.isEmpty()) {
                    for (Annotation a : existingAnnotations) {
                        if (a instanceof NormalAnnotation) {
                            List<MemberValuePair> existingValues = ((NormalAnnotation) a).values();
                            for (MemberValuePair mvp : existingValues) {
                                boolean removeAttribute = this.attributesToRemove.contains(mvp.getName().getFullyQualifiedName());

                                // do not add attribute to be removed
                                if (!removeAttribute) {
                                    MemberValuePair memberValuePair = ast.newMemberValuePair();
                                    memberValuePair.setName(ast.newSimpleName(mvp.getName().getFullyQualifiedName()));
                                    StringLiteral stringValue = ast.newStringLiteral();

                                    if (mvp.getValue() instanceof StringLiteral) {
                                        StringLiteral stringLiteral = (StringLiteral) mvp.getValue();
                                        stringValue.setLiteralValue(stringLiteral.getLiteralValue());
                                    } else {
                                        stringValue.setLiteralValue("");
                                    }
                                    memberValuePair.setValue(stringValue);
                                    values.add(memberValuePair);
                                }
                            }
                        }
                    }
                }

                // add new String attributes
                for (String newAttr : this.attributesToAdd) {
                    MemberValuePair memberValuePair = ast.newMemberValuePair();
                    memberValuePair.setName(ast.newSimpleName(newAttr));
                    StringLiteral stringValue = ast.newStringLiteral();
                    stringValue.setLiteralValue("");
                    memberValuePair.setValue(stringValue);
                    values.add(memberValuePair);
                }

                ChildListPropertyDescriptor newRewrite;
                if (isSingleVarDecl) {
                    newRewrite = SingleVariableDeclaration.MODIFIERS2_PROPERTY;
                } else if (isField) {
                    newRewrite = FieldDeclaration.MODIFIERS2_PROPERTY;
                } else {
                    newRewrite = TypeDeclaration.MODIFIERS2_PROPERTY;
                }

                rewrite.getListRewrite(declNode, newRewrite).insertFirst(marker, null);
            }
            return rewrite;
        }
        return null;
    }
}
