/*******************************************************************************
 * Copyright (c) 2023 IBM Corporation and others.
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
package org.eclipse.lsp4jakarta.jdt.internal.servlet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.internal.corext.dom.Bindings;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionKind;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4jakarta.commons.codeaction.CodeActionResolveData;
import org.eclipse.lsp4jakarta.commons.codeaction.JakartaCodeActionId;
import org.eclipse.lsp4jakarta.jdt.core.java.codeaction.ExtendedCodeAction;
import org.eclipse.lsp4jakarta.jdt.core.java.codeaction.IJavaCodeActionParticipant;
import org.eclipse.lsp4jakarta.jdt.core.java.codeaction.JavaCodeActionContext;
import org.eclipse.lsp4jakarta.jdt.core.java.codeaction.JavaCodeActionResolveContext;
import org.eclipse.lsp4jakarta.jdt.core.java.corrections.proposal.ChangeCorrectionProposal;
import org.eclipse.lsp4jakarta.jdt.core.java.corrections.proposal.ImplementInterfaceProposal;
import org.eclipse.lsp4jakarta.jdt.internal.Messages;

/**
 * Inserts the implements clause to the active class to implement the HTTP
 * session and servlet listener interfaces.
 */
public class InsertImplementsClauseToImplListenerQuickFix implements IJavaCodeActionParticipant {

    /** Logger object to record events for this class. */
    private static final Logger LOGGER = Logger.getLogger(InsertImplementsClauseToImplListenerQuickFix.class.getName());

    /** Map key to retrieve an interface type */
    private static final String INTERFACE_TYPE_KEY = "interface.type";

    /** Map key to retrieve an interface name */
    private static final String INTERFACE_NAME_KEY = "interface.name";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getParticipantId() {
        return InsertImplementsClauseToImplListenerQuickFix.class.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<? extends CodeAction> getCodeActions(JavaCodeActionContext context, Diagnostic diagnostic,
                                                     IProgressMonitor monitor) throws CoreException {
        List<CodeAction> codeActions = new ArrayList<>();
        ASTNode node = context.getCoveredNode();
        ITypeBinding parentType = Bindings.getBindingOfParentType(node);

        if (parentType != null) {
            createCodeAction(context, diagnostic, parentType, Constants.SERVLET_CONTEXT_LISTENER,
                             "jakarta.servlet.ServletContextListener", codeActions);
            createCodeAction(context, diagnostic, parentType, Constants.SERVLET_CONTEXT_ATTRIBUTE_LISTENER,
                             "jakarta.servlet.ServletContextAttributeListener", codeActions);
            createCodeAction(context, diagnostic, parentType, Constants.SERVLET_REQUEST_LISTENER,
                             "jakarta.servlet.ServletRequestListener", codeActions);
            createCodeAction(context, diagnostic, parentType, Constants.SERVLET_REQUEST_ATTRIBUTE_LISTENER,
                             "jakarta.servlet.ServletRequestAttributeListener", codeActions);
            createCodeAction(context, diagnostic, parentType, Constants.HTTP_SESSION_LISTENER,
                             "jakarta.servlet.http.HttpSessionListener", codeActions);
            createCodeAction(context, diagnostic, parentType, Constants.HTTP_SESSION_ATTRIBUTE_LISTENER,
                             "jakarta.servlet.http.HttpSessionAttributeListener", codeActions);
            createCodeAction(context, diagnostic, parentType, Constants.HTTP_SESSION_ID_LISTENER,
                             "jakarta.servlet.http.HttpSessionIdListener", codeActions);
        }

        return codeActions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CodeAction resolveCodeAction(JavaCodeActionResolveContext context) {
        CodeAction toResolve = context.getUnresolved();
        ASTNode node = context.getCoveredNode();
        ITypeBinding parentType = Bindings.getBindingOfParentType(node);

        CodeActionResolveData data = (CodeActionResolveData) toResolve.getData();
        String interfaceName = (String) data.getExtendedDataEntry(INTERFACE_NAME_KEY);
        String interfaceType = (String) data.getExtendedDataEntry(INTERFACE_TYPE_KEY);

        String label = getLabel(parentType.getName(), interfaceName);

        ChangeCorrectionProposal proposal = new ImplementInterfaceProposal(label, context.getCompilationUnit(), parentType, context.getASTRoot(), interfaceType, 0);

        try {
            toResolve.setEdit(context.convertToWorkspaceEdit(proposal));
        } catch (CoreException e) {
            LOGGER.log(Level.SEVERE, "Unable to resolve code action edit to add the implements clause to a class.",
                       e);
        }

        return toResolve;
    }

    /**
     * Creates a code action.
     *
     * @param context The code action context.
     * @param diagnostic The diagnostic associated with this code action.
     * @param parentType The parent binding associated with the current node.
     * @param interfaceName The interface name.
     * @param interfaceType The interface type.
     * @param codeActions The list of code actions to populate.
     */
    protected void createCodeAction(JavaCodeActionContext context, Diagnostic diagnostic, ITypeBinding parentType,
                                    String interfaceName, String interfaceType, List<CodeAction> codeActions) {

        ExtendedCodeAction codeAction = new ExtendedCodeAction(getLabel(parentType.getName(), interfaceName));
        codeAction.setRelevance(0);
        codeAction.setKind(CodeActionKind.QuickFix);
        codeAction.setDiagnostics(Arrays.asList(diagnostic));
        Map<String, Object> extendedData = new HashMap<String, Object>();
        extendedData.put(INTERFACE_NAME_KEY, interfaceName);
        extendedData.put(INTERFACE_TYPE_KEY, interfaceType);
        codeAction.setData(new CodeActionResolveData(context.getUri(), getParticipantId(), context.getParams().getRange(), extendedData, context.getParams().isResourceOperationSupported(), context.getParams().isCommandConfigurationUpdateSupported(), JakartaCodeActionId.ServletListenerImplementation));
        codeActions.add(codeAction);
    }

    /**
     * Returns the code action label.
     *
     * @param classTypeName The class type element name.
     * @param interfaceName The interface name.
     *
     * @return The code action label.
     */
    @SuppressWarnings("restriction")
    private String getLabel(String classTypeName, String interfaceName) {
        return Messages.getMessage("LetClassImplement",
                                   org.eclipse.jdt.internal.core.manipulation.util.BasicElementLabels.getJavaElementName(classTypeName),
                                   org.eclipse.jdt.internal.core.manipulation.util.BasicElementLabels.getJavaElementName(interfaceName));
    }
}