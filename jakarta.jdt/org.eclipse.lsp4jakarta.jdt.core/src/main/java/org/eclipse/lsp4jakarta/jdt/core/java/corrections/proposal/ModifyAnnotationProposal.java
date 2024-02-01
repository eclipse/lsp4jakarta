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
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.ChildListPropertyDescriptor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeLiteral;
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
        boolean isSingleMemberAnnotation = declNode instanceof SingleMemberAnnotation;

        if (isField) {
            declNode = declNode.getParent();
        }

        // if the Annotation is declared on a class field
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
                    NormalAnnotation marker = null;
                    marker = processNormalAnnotation(ast, imports, importRewriteContext, annotations, (NormalAnnotation) a);

                    // add new annotation proposal to the rewrite text edit
                    rewrite.getListRewrite(declNode,
                                           isField ? FieldDeclaration.MODIFIERS2_PROPERTY : TypeDeclaration.MODIFIERS2_PROPERTY).insertFirst(marker, null);
                }
            }

            return rewrite;
        } else if (declNode instanceof TypeDeclaration || isField || isSingleVarDecl) {
            // Annotation in question is set on a class declaration or is a method parameter declaration
            AST ast = declNode.getAST();
            ASTRewrite rewrite = ASTRewrite.create(ast);

            ImportRewriteContext importRewriteContext = new ContextSensitiveImportRewriteContext(declNode, imports);
            List<Annotation> existingAnnotations = new ArrayList<Annotation>();
            ChildListPropertyDescriptor property = isSingleVarDecl ? SingleVariableDeclaration.MODIFIERS2_PROPERTY : TypeDeclaration.MODIFIERS2_PROPERTY;
            List<? extends ASTNode> children = (List<? extends ASTNode>) declNode.getStructuralProperty(property);

            boolean isCompositeAnnotation = false;

            // find and save existing annotation, then remove it from ast
            // this will cause the entire annotation to be deleted from the file
            for (ASTNode child : children) {
                // for all existing annotations (that are the annotation we want)
                if (child instanceof Annotation) {
                    Annotation annotation = (Annotation) child;
                    boolean containsAnnotation = Arrays.stream(annotationShortNames).anyMatch(annotation.getTypeName().toString()::contains);
                    if (containsAnnotation) {
                        // save the existing annotation for processing but remove it from the file
                        existingAnnotations.add(annotation);
                        rewrite.remove(child, null);
                    }
                }
            }

            // add a new annotation with fields from existing annotation
            for (String annotation : annotations) {
                Annotation newAnnotationToWrite = null;
                if (!existingAnnotations.isEmpty()) {
                    for (Annotation a : existingAnnotations) {
                        if (a instanceof SingleMemberAnnotation) {
                            // this type of annotation contains a single valuie which may be a list of other annotations,
                            // some of which need to be fixed
                            newAnnotationToWrite = processSingleMemberAnnotation(ast, imports, importRewriteContext, annotations, (SingleMemberAnnotation) a);
                        } else if (a instanceof NormalAnnotation) {
                            // this type of annotation is a base annotation containing a name value pair that needs to be fixed
                            newAnnotationToWrite = processNormalAnnotation(ast, imports, importRewriteContext, annotations, (NormalAnnotation) a);

                        }
                    }
                } else {
                    // if if this annotation proposal is to add an annotation where one did not exist prior
                    newAnnotationToWrite = createNewAnnotation(ast, imports, importRewriteContext, annotation);
                }

                ChildListPropertyDescriptor newRewrite;
                if (isSingleVarDecl) {
                    newRewrite = SingleVariableDeclaration.MODIFIERS2_PROPERTY;
                } else if (isField) {
                    newRewrite = FieldDeclaration.MODIFIERS2_PROPERTY;
                } else {
                    newRewrite = TypeDeclaration.MODIFIERS2_PROPERTY;
                }

                // add new annotation proposal to the rewrite text edit
                rewrite.getListRewrite(declNode, newRewrite).insertFirst(newAnnotationToWrite, null);
            }
            return rewrite;
        }
        return null;
    }

    private SingleMemberAnnotation processSingleMemberAnnotation(AST ast, ImportRewrite imports,
                                                                 ImportRewriteContext importRWCtx, String[] annotations,
                                                                 SingleMemberAnnotation annotationToProcess) {

        // A SingleMemberAnnotation is an annotation that contains within it a single
        // value (with no name associated with it)
        // That value may be a list of additional annotations, IE:
        //
        // @Resources ({ @Resource(name = "aaa"), @Resource(type = Object.class) })
        //
        // This method will process the annotations in the list, one by one, for quick
        // fix actions on any applicable sub annotations

        SingleMemberAnnotation newSingleMemberAnnotation = ast.newSingleMemberAnnotation();
        ArrayInitializer newAIInstance = (ArrayInitializer) ast.createInstance(ArrayInitializer.class);

        newSingleMemberAnnotation.setTypeName(
                                              ast.newName(imports.addImport(annotationToProcess.getTypeName().toString(), importRWCtx)));
        List<NormalAnnotation> newCompositeAnnotationContents = newAIInstance.expressions();

        // An ArrayInitializer is the object that holds the list of sub annotations within the SingleMemberAnnotation
        ArrayInitializer ai = (ArrayInitializer) ((SingleMemberAnnotation) annotationToProcess).getValue();

        // get the list of annotations to process
        List<NormalAnnotation> normalAnnotations = ai.expressions();

        // for each annotation in the list, process it for the quick fix edit
        for (NormalAnnotation na : normalAnnotations) {
            NormalAnnotation newNormalAnnotation = processNormalAnnotation(ast, imports, importRWCtx, annotations,
                                                                           na);
            newCompositeAnnotationContents.add(newNormalAnnotation);
        }

        // now add the processed annotations to the new SingleMemberAnnotation to be written into the file
        newSingleMemberAnnotation.setValue(newAIInstance);

        return newSingleMemberAnnotation;
    }

    private NormalAnnotation processNormalAnnotation(AST ast, ImportRewrite imports, ImportRewriteContext importRWCtx,
                                                     String[] annotations, NormalAnnotation annotationToProcess) {

        NormalAnnotation newNormalAnnotation = ast.newNormalAnnotation();

        // for every annotation we are fixing
        for (String annotation : annotations) {

            // create a new NormalAnnotation to be written
            newNormalAnnotation.setTypeName(ast.newName(imports.addImport(annotation, importRWCtx)));
            List<MemberValuePair> values = newNormalAnnotation.values();

            // get the existing name/value pairs
            List<MemberValuePair> existingValues = ((NormalAnnotation) annotationToProcess).values();
            for (MemberValuePair mvp : existingValues) {
                boolean containsAttributeToAdd = this.attributesToAdd.contains(mvp.getName().getFullyQualifiedName());
                boolean containsAllToAdd = this.attributesToAdd.stream().allMatch(attr -> existingValues.stream().anyMatch(v -> v.equals(attr)));
                boolean removeAttribute = this.attributesToRemove.contains(mvp.getName().getFullyQualifiedName());

                if (!containsAttributeToAdd || !containsAllToAdd) {

                    // do not add attribute to be removed
                    if (!removeAttribute) {
                        // copy the existing values into the new Annotation
                        MemberValuePair existingMemberValuePair = ast.newMemberValuePair();
                        existingMemberValuePair.setName(ast.newSimpleName(mvp.getName().getFullyQualifiedName()));

                        if (mvp.getValue() instanceof StringLiteral) {
                            existingMemberValuePair.setValue((StringLiteral) mvp.getValue().copySubtree(ast, mvp.getValue()));
                        } else if (mvp.getValue() instanceof TypeLiteral) {
                            existingMemberValuePair.setValue((TypeLiteral) mvp.getValue().copySubtree(ast, mvp.getValue()));

                        }

                        values.add(existingMemberValuePair);
                    }

                    // add new String attributes
                    //values = addNewAttributes(ast, values);
                } else {
                    // the current member annotation already contains the attribute and so is not
                    // the one to have the quickfix applied to it
                    // return an as-is copy of the exsiting (and removed) annotation.
                    newNormalAnnotation = (NormalAnnotation) annotationToProcess.copySubtree(ast, annotationToProcess);
                    return newNormalAnnotation;
                }

            }

            // add new String attributes
            values = addNewAttributes(ast, values);
        }
        return newNormalAnnotation;
    }

    private NormalAnnotation createNewAnnotation(AST ast, ImportRewrite imports, ImportRewriteContext importRWCtx,
                                                 String annotation) {

        NormalAnnotation marker = ast.newNormalAnnotation();

        marker.setTypeName(ast.newName(imports.addImport(annotation, importRWCtx)));
        List<MemberValuePair> values = marker.values();

        values = addNewAttributes(ast, values);

        return marker;

    }

    private List<MemberValuePair> addNewAttributes(AST ast, List<MemberValuePair> values) {
        // add new String attributes
        // we are adding empty strings for values because we cannot know what the user
        // wishes to have - they will have to add that themselves
        // ie: name="" or type=""
        for (String newAttr : this.attributesToAdd) {
            if (values.stream().noneMatch(v -> v.getName().toString().equals(newAttr))) {
                MemberValuePair newMemberValuePair = ast.newMemberValuePair();
                newMemberValuePair.setName(ast.newSimpleName(newAttr));
                StringLiteral stringValue = ast.newStringLiteral();
                stringValue.setLiteralValue("");
                newMemberValuePair.setValue(stringValue);
                values.add(newMemberValuePair);
            }
        }
        return values;
    }
}
