package org.jakarta.jdt.persistence;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;
import org.jakarta.codeAction.IJavaCodeActionParticipant;
import org.jakarta.codeAction.JavaCodeActionContext;
import org.jakarta.codeAction.proposal.ChangeCorrectionProposal;
import org.jakarta.codeAction.proposal.ModifyAnnotationProposal;
import org.jakarta.jdt.servlet.InsertAnnotationMissingQuickFix;

/**
 * If more than one @MapKeyJoinColumn annotation is applied to a field 
 * or property, both the name and the referencedColumnName elements must
 * be specified in each such @MapKeyJoinColumn annotation.
 */

/**
 * QuickFix for fixing {@link PersistenceConstants#DIAGNOSTIC_CODE_MISSING_ATTRIBUTES} error
 * by providing several code actions to add the missing elements to the existing annotations:
 * 
 * {@link PersistenceConstants#DIAGNOSTIC_CODE_MISSING_ATTRIBUTES}
 * <ul>
 * <li> Add the `name` attribute to the `@MapKeyJoinColumn` annotation
 * <li> Add the `referencedColumnName` attribute to the `@MapKeyJoinColumn` annotation
 * </ul>
 *
 * @author Leslie Dawson (lamminade)
 *
 */
public class PersistenceAnnotationQuickFix extends InsertAnnotationMissingQuickFix {

    public PersistenceAnnotationQuickFix() {
        super("jakarta.persistence.MapKeyJoinColumn");
    }

    @Override
    protected void insertAnnotations(Diagnostic diagnostic, JavaCodeActionContext context, IBinding parentType,
            List<CodeAction> codeActions) throws CoreException {
        String[] annotations = getAnnotations();
        for (String annotation : annotations) {
            insertAndReplaceAnnotation(diagnostic, context, parentType, codeActions, annotation);
        }
    }

    private static void insertAndReplaceAnnotation(Diagnostic diagnostic, JavaCodeActionContext context,
            IBinding parentType, List<CodeAction> codeActions, String annotation) throws CoreException {

        // Insert the annotation and the proper import by using JDT Core Manipulation API

        // insert correct missing attributes
        ArrayList<String> attributes = new ArrayList<>();
        if (diagnostic.getCode().getLeft().equals(PersistenceConstants.DIAGNOSTIC_CODE_MISSING_NAME) 
                || diagnostic.getCode().getLeft().equals(PersistenceConstants.DIAGNOSTIC_CODE_MISSING_ATTRIBUTES)) {
            attributes.add("name"); 
        }
        if (diagnostic.getCode().getLeft().equals(PersistenceConstants.DIAGNOSTIC_CODE_MISSING_MAPKEYJOINCOLUMN) 
                || diagnostic.getCode().getLeft().equals(PersistenceConstants.DIAGNOSTIC_CODE_MISSING_ATTRIBUTES)) {
            attributes.add("referencedColumnName");
        }

        // for each attribute create a proposal
        for (String attribute : attributes) {
            ArrayList<String> attributesToAdd = new ArrayList<>();
            attributesToAdd.add(attribute);
            String name = getLabel(annotation, attribute, "Add");
            ChangeCorrectionProposal proposal = new ModifyAnnotationProposal(name, context.getCompilationUnit(),
                    context.getASTRoot(), parentType, 0, annotation, attributesToAdd);

            // Convert the proposal to LSP4J CodeAction
            CodeAction codeAction = context.convertToCodeAction(proposal, diagnostic);
            codeAction.setTitle(name);
            if (codeAction != null) {
                codeActions.add(codeAction);
            }
        }
    }

    private static String getLabel(String annotation, String attribute, String labelType) {
        StringBuilder name = new StringBuilder("Add the `" + attribute + "` attribute to ");
        if (labelType.equals("Remove")) {
            name = new StringBuilder("Remove the `" + attribute + "` attribute from ");
        }
        String annotationName = annotation.substring(annotation.lastIndexOf('.') + 1, annotation.length());
        name.append("@");
        name.append(annotationName);
        return name.toString();
    }
}