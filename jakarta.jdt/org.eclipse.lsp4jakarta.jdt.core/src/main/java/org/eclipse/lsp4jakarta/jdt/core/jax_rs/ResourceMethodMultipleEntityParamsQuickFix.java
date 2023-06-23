/*******************************************************************************
 * Copyright (c) 2021 IBM Corporation, Bera Sogut and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation, Bera Sogut - initial API and implementation
 *******************************************************************************/

package org.eclipse.lsp4jakarta.jdt.core.jax_rs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4jakarta.jdt.codeAction.IJavaCodeActionParticipant;
import org.eclipse.lsp4jakarta.jdt.codeAction.JavaCodeActionContext;
import org.eclipse.lsp4jakarta.jdt.codeAction.proposal.ChangeCorrectionProposal;
import org.eclipse.lsp4jakarta.jdt.codeAction.proposal.RemoveParamsProposal;
import org.eclipse.lsp4jakarta.jdt.core.Messages;

/**
 * Quick fix for the ResourceMethodMultipleEntityParams diagnostic in
 * ResourceMethodDiagnosticsCollector. This class adds a quick fix for each
 * entity parameter which removes all entity parameters except the chosen one.
 *
 * @author Bera Sogut
 *
 */
public class ResourceMethodMultipleEntityParamsQuickFix implements IJavaCodeActionParticipant {

    @Override
    public List<? extends CodeAction> getCodeActions(JavaCodeActionContext context, Diagnostic diagnostic,
            IProgressMonitor monitor) throws CoreException {
        List<CodeAction> codeActions = new ArrayList<>();
        ASTNode node = context.getCoveredNode();
        MethodDeclaration parentNode = (MethodDeclaration) node.getParent();
        IMethodBinding parentMethod = parentNode.resolveBinding();

        if (parentMethod != null) {

            List<SingleVariableDeclaration> params = (List<SingleVariableDeclaration>) parentNode.parameters();

            List<SingleVariableDeclaration> entityParams = new ArrayList<>();

            for (SingleVariableDeclaration param : params) {
                if (isEntityParam(param)) {
                    entityParams.add(param);
                }
            }

            for (SingleVariableDeclaration entityParam : entityParams) {
                final String TITLE_MESSAGE = Messages.getMessage("RemoveAllEntityParametersExcept",
                        entityParam.getName().getIdentifier());

                // Remove all entity parameters except the current chosen one
                ChangeCorrectionProposal proposal = new RemoveParamsProposal(TITLE_MESSAGE,
                        context.getCompilationUnit(), context.getASTRoot(), parentMethod, 0, entityParams, entityParam);

                // Convert the proposal to LSP4J CodeAction
                CodeAction codeAction = context.convertToCodeAction(proposal, diagnostic);
                codeAction.setTitle(TITLE_MESSAGE);
                codeActions.add(codeAction);
            }

        }
        return codeActions;
    }

    /**
     * Returns a boolean variable that indicates whether the given parameter is an
     * entity parameter or not.
     *
     * @param param the parameter to check whether it is an entity parameter or not
     * @return true if the given parameter is an entity parameter, false otherwise
     */
    private boolean isEntityParam(SingleVariableDeclaration param) {
        ArrayList<String> nonEntityParamAnnotations = Jax_RSConstants.NON_ENTITY_PARAM_ANNOTATIONS;

        boolean isEntityParam = true;
        for (IExtendedModifier modifier : (List<IExtendedModifier>) param.modifiers()) {
            if (modifier.isAnnotation()) {
                Name typeName = ((Annotation) modifier).getTypeName();
                if (nonEntityParamAnnotations.contains(typeName.toString())) {
                    isEntityParam = false;
                    break;
                }
            }
        }

        return isEntityParam;
    }
}
