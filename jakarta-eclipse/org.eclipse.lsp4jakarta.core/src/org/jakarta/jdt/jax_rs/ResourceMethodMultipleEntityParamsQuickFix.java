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

package org.jakarta.jdt.jax_rs;

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
import org.jakarta.codeAction.IJavaCodeActionParticipant;
import org.jakarta.codeAction.JavaCodeActionContext;
import org.jakarta.codeAction.proposal.ChangeCorrectionProposal;
import org.jakarta.codeAction.proposal.RemoveEntityParamsProposal;

/**
 * Quick fix for the ResourceMethodMultipleEntityParams diagnostic in
 * ResourceMethodDiagnosticsCollector.
 *
 * @author Bera Sogut
 *
 */
public class ResourceMethodMultipleEntityParamsQuickFix implements IJavaCodeActionParticipant {

    @Override
    public List<? extends CodeAction> getCodeActions(JavaCodeActionContext context, Diagnostic diagnostic,
            IProgressMonitor monitor) throws CoreException {
        ASTNode node = context.getCoveredNode();
        MethodDeclaration parentNode = (MethodDeclaration) node.getParent();
        IMethodBinding parentMethod = parentNode.resolveBinding();

        if (parentMethod != null) {
            List<CodeAction> codeActions = new ArrayList<>();

            List<SingleVariableDeclaration> params = (List<SingleVariableDeclaration>) parentNode.parameters();

            List<SingleVariableDeclaration> entityParams = new ArrayList<>();

            for (SingleVariableDeclaration param : params) {
                if (isEntityParam(param)) {
                    entityParams.add(param);
                }
            }

            for (SingleVariableDeclaration entityParam : entityParams) {
                final String TITLE_MESSAGE = "Remove all entity parameters except "
                        + entityParam.getName().getIdentifier();

                ChangeCorrectionProposal proposal = new RemoveEntityParamsProposal(TITLE_MESSAGE,
                        context.getCompilationUnit(), context.getASTRoot(), parentMethod, 0, entityParams, entityParam);

                // Convert the proposal to LSP4J CodeAction
                CodeAction codeAction = context.convertToCodeAction(proposal, diagnostic);
                codeAction.setTitle(TITLE_MESSAGE);
                codeActions.add(codeAction);
            }

            return codeActions;
        }
        return null;
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
