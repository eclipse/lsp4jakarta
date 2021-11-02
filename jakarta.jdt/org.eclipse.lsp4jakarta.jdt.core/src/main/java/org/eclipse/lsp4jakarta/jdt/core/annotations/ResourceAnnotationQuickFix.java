/*******************************************************************************
* Copyright (c) 2021 IBM Corporation and others.
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




package org.eclipse.lsp4jakarta.jdt.core.annotations;



import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4jakarta.jdt.codeAction.IJavaCodeActionParticipant;
import org.eclipse.lsp4jakarta.jdt.codeAction.JavaCodeActionContext;
import org.eclipse.lsp4jakarta.jdt.codeAction.proposal.ChangeCorrectionProposal;
import org.eclipse.lsp4jakarta.jdt.codeAction.proposal.ModifyAnnotationProposal;
import org.eclipse.lsp4jakarta.jdt.codeAction.proposal.quickfix.InsertAnnotationMissingQuickFix;
import org.eclipse.lsp4jakarta.jdt.core.annotations.AnnotationConstants;



/**
 * Quickfix for annotation Resource
 * 1. Add missing name
 * 2. Add missing type
 * 
 * @author Zijian Pei
 *
 */
public class ResourceAnnotationQuickFix extends InsertAnnotationMissingQuickFix{
	public ResourceAnnotationQuickFix() {
        super("jakarta.annotation.Resource");
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

	        // Insert the annotation and the proper import by using JDT Core Manipulation
	        // API

		  
		    ArrayList<String> attributes = new ArrayList<>();
	        // if missing an attribute, do value insertion
	        if (diagnostic.getCode().getLeft().equals(AnnotationConstants.DIAGNOSTIC_CODE_MISSING_RESOURCE_NAME_ATTRIBUTE)) {
	            attributes.add("name");
	        }
	        if (diagnostic.getCode().getLeft().equals(AnnotationConstants.DIAGNOSTIC_CODE_MISSING_RESOURCE_TYPE_ATTRIBUTE)) {
	        	attributes.add("type");
	        }
	            
	            
	            for (int i = 0; i < attributes.size(); i++) {
	                String attribute = attributes.get(i);

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
	        String annotationName = annotation.substring(annotation.lastIndexOf('.') + 1, annotation.length());
	        name.append("@");
	        name.append(annotationName);
	        return name.toString();
	    }
}