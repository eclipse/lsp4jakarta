package org.jakarta.codeAction.proposal;

import java.util.ArrayList;
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
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.NormalAnnotation;
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
public class ModifyAnnotationProposal extends NewAnnotationProposal {

    // list of attributes to add to the annotation
    private final List<String> attributesToAdd;

    // list of attributes (if they exist) to remove from the annotation
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
        if (isField) {
            declNode = declNode.getParent();
        }
        if (declNode instanceof TypeDeclaration || isField) {
            AST ast = declNode.getAST();
            ASTRewrite rewrite = ASTRewrite.create(ast);

            ImportRewriteContext importRewriteContext = new ContextSensitiveImportRewriteContext(declNode, imports);

            // remove annotations in the removeAnnotations list
            @SuppressWarnings("unchecked")
            List<? extends ASTNode> children = (List<? extends ASTNode>) declNode
                    .getStructuralProperty(TypeDeclaration.MODIFIERS2_PROPERTY);
            Annotation existingAnnotation = null;

            // find and save existing annotation, then remove it from ast
            for (ASTNode child : children) {
                if (child instanceof Annotation) {
                    Annotation annotation = (Annotation) child;
                    IAnnotationBinding annotationBinding = annotation.resolveAnnotationBinding();

                    boolean containsAnnotation = Arrays.stream(annotationShortNames)
                            .anyMatch(annotation.getTypeName().toString()::contains);
                    if (containsAnnotation) {
                        existingAnnotation = annotation;
                        rewrite.remove(child, null);
                    }
                }
            }

            // add new annotation with fields from existing annotation
            for (String annotation : annotations) {
                NormalAnnotation marker = ast.newNormalAnnotation();
                marker.setTypeName(ast.newName(imports.addImport(annotation, importRewriteContext)));
                List<MemberValuePair> values = marker.values();
                if (existingAnnotation != null && existingAnnotation instanceof NormalAnnotation) {
                    List<MemberValuePair> existingValues = ((NormalAnnotation) existingAnnotation).values();
                    for (MemberValuePair mvp : existingValues) {

                        boolean removeAttribute = this.attributesToRemove
                                .contains(mvp.getName().getFullyQualifiedName());

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

                // add new String attributes
                for (String newAttr : this.attributesToAdd) {
                    MemberValuePair memberValuePair = ast.newMemberValuePair();
                    memberValuePair.setName(ast.newSimpleName(newAttr));
                    StringLiteral stringValue = ast.newStringLiteral();
                    stringValue.setLiteralValue("");
                    memberValuePair.setValue(stringValue);
                    values.add(memberValuePair);

                }

                rewrite.getListRewrite(declNode,
                        isField ? FieldDeclaration.MODIFIERS2_PROPERTY : TypeDeclaration.MODIFIERS2_PROPERTY)
                        .insertFirst(marker, null);
            }

            return rewrite;
        }
        return null;
    }
}
