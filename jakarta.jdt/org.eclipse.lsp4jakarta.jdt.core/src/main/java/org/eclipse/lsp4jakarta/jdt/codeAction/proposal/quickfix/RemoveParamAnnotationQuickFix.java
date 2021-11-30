/*******************************************************************************
* Copyright (c) 2020 Red Hat Inc. and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
* which is available at https://www.apache.org/licenses/LICENSE-2.0.
*
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
*
* Contributors:
*     Red Hat Inc. - initial API and implementation
*******************************************************************************/
package org.eclipse.lsp4jakarta.jdt.codeAction.proposal.quickfix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4jakarta.jdt.codeAction.JavaCodeActionContext;

/**
 * QuickFix for removing parameter annotations
 */
public class RemoveParamAnnotationQuickFix extends RemoveModifierConflictQuickFix {

	String[] annotations;
	
    public RemoveParamAnnotationQuickFix(String ...annotations) {
        super(annotations);
        this.annotations = annotations;
    }

    @Override
    public List<? extends CodeAction> getCodeActions(JavaCodeActionContext context, Diagnostic diagnostic,
            IProgressMonitor monitor) throws CoreException {
        
    	ASTNode node = context.getCoveredNode();
    	MethodDeclaration parentNode = (MethodDeclaration) node.getParent();
    	IMethodBinding parentMethod = parentNode.resolveBinding();
        
        List<CodeAction> codeActions = new ArrayList<>();

        List<SingleVariableDeclaration> parameters = (List<SingleVariableDeclaration>) parentNode.parameters();

        for (SingleVariableDeclaration parameter : parameters) {
        	            
            List<ASTNode> modifiers = (List<ASTNode>) parameter.getStructuralProperty(SingleVariableDeclaration.MODIFIERS2_PROPERTY);
            ArrayList<String> annotationsToRemove = new ArrayList<>();
            
            for(ASTNode modifier : modifiers) {
            	Name markAnnotationTypeName = ((MarkerAnnotation) modifier).getTypeName();
                if (Arrays.asList(this.annotations).stream().anyMatch(m -> m.equals(markAnnotationTypeName.toString()))) {
                	annotationsToRemove.add(markAnnotationTypeName.toString());
                }
            }
            
            StringBuilder sb = new StringBuilder("Remove the ");
            sb.append("'@").append(annotationsToRemove.get(0)).append("'");
            for (int i = 1; i < annotationsToRemove.size();i++) {
            	sb.append(", '@").append(annotationsToRemove.get(i)).append("'");
            }
            sb.append(" modifier from parameter '").append(parameter.getName().toString()).append("'");

            removeModifier(diagnostic, context, parentMethod, codeActions, parameter, sb.toString(), 
            		(String []) annotationsToRemove.toArray(new String[annotationsToRemove.size()]));
        }

        return codeActions;
    }
    
}
