/******************************************************************************* 
 * Copyright (c) 2022 IBM Corporation and others. 
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v. 2.0 which is available at 
 * http://www.eclipse.org/legal/epl-2.0. 
 * 
 * SPDX-License-Identifier: EPL-2.0 
 * 
 * Contributors: 
 *     Giancarlo Pernudi Segura - initial API and implementation 
 *******************************************************************************/

package org.eclipse.lsp4jakarta.jdt.core.websocket;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4jakarta.jdt.core.JDTUtils;
import org.eclipse.lsp4jakarta.jdt.core.DiagnosticsCollector;
import org.eclipse.lsp4jakarta.jdt.core.JakartaCorePlugin;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.lsp4jakarta.jdt.core.AnnotationUtil;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;

import static org.eclipse.lsp4jakarta.jdt.core.TypeHierarchyUtils.doesITypeHaveSuperType;


public class WebSocketDiagnosticsCollector implements DiagnosticsCollector {
	public WebSocketDiagnosticsCollector() {
	}

	private Diagnostic createDiagnostic(IJavaElement el, ICompilationUnit unit, String msg, String code) {
		try {
			ISourceRange nameRange = JDTUtils.getNameRange(el);
			Range range = JDTUtils.toRange(unit, nameRange.getOffset(), nameRange.getLength());
			Diagnostic diagnostic = new Diagnostic(range, msg);
			diagnostic.setCode(code);
			completeDiagnostic(diagnostic);
			return diagnostic;
		} catch (JavaModelException e) {
			JakartaCorePlugin.logException(WebSocketConstants.DIAGNOSTIC_ERR_MSG, e);
		}
		return null;
	}

	@Override
	public void completeDiagnostic(Diagnostic diagnostic) {
		diagnostic.setSource(WebSocketConstants.DIAGNOSTIC_SOURCE);
		diagnostic.setSeverity(WebSocketConstants.ERROR);
	}

	@Override
	public void collectDiagnostics(ICompilationUnit unit, List<Diagnostic> diagnostics) {
		if (unit == null) {
			return;
		}

		IType[] alltypes;
		IField[] allFields;
		IAnnotation[] allFieldAnnotations;
		IMethod[] allMethods;
		IAnnotation[] allMethodAnnotations;

		HashMap<String, Boolean> checkWSEnd = null;

		try {
			alltypes = unit.getAllTypes();
			for (IType type : alltypes) {
				checkWSEnd = isWSEndpoint(type);

				// checks if the class uses annotation to create a ws endpoint
				if (checkWSEnd.get("isAnnotation")) {
					invalidParamsCheck(type, WebSocketConstants.ON_OPEN, WebSocketConstants.ON_OPEN_PARAM_OPT_TYPES,
							unit, diagnostics);
				}

			}
		} catch (JavaModelException e) {
			JakartaCorePlugin.logException(WebSocketConstants.DIAGNOSTIC_ERR_MSG, e);
		}
	}

	private void invalidParamsCheck(IType type, String function, Set<String> specialParamTypes, ICompilationUnit unit,
			List<Diagnostic> diagnostics) throws JavaModelException {

		IMethod[] allMethods = type.getMethods();

		for (IMethod method : allMethods) {
			IAnnotation[] allAnnotations = method.getAnnotations();

			for (IAnnotation annotation : allAnnotations) {
				if (annotation.getElementName().equals(function)) {
					// check params
					ILocalVariable[] allParams = method.getParameters();

					for (ILocalVariable param : allParams) {
					    
					    String signature = param.getTypeSignature();
					    String formatSignature = signature.replace("/", ".");
					    IType primaryType = param.getTypeRoot().findPrimaryType();
					    String resolvedTypeName = JavaModelUtil.getResolvedTypeName(formatSignature, primaryType);
					    					    
					    boolean isPrimitive = JavaModelUtil.isPrimitive(formatSignature);
					    boolean isSpecialType = specialParamTypes.contains(resolvedTypeName);
//					    if (resolvedTypeName != null) {
//					        isSpecialType = specialParamTypes.contains(resolvedTypeName);
//					    } else {
//					        isSpecialType = specialParamTypes.contains(Signature.getSignatureSimpleName(signature));
//					    }
					    
					    // check type of parameters
					    if (!(isSpecialType || isWrapper(resolvedTypeName) || isPrimitive)) {
					        Diagnostic diagnostic = createDiagnostic(param, unit,
                                    WebSocketConstants.DIAGNOSTIC_ON_OPEN_INVALID_PARAMS,
                                    WebSocketConstants.DIAGNOSTIC_CODE_ON_OPEN_INVALID_PARAMS);
                            diagnostics.add(diagnostic);
                            return;
					    }
					    
					    if (!isSpecialType) {
					        // check if it has @PathParam
					        IAnnotation[] param_annotations = param.getAnnotations();
	                        boolean hasPathParamAnnot = Arrays.asList(param_annotations).stream().anyMatch(
	                                annot -> annot.getElementName().equals(WebSocketConstants.PATH_PARAM_ANNOTATION));
	                        
	                        if (!hasPathParamAnnot) {
	                            Diagnostic diagnostic = createDiagnostic(param, unit,
                                        WebSocketConstants.DIAGNOSTIC_PATH_PARAMS_ANNOT_MISSING,
                                        WebSocketConstants.DIAGNOSTIC_CODE_PATH_PARMS_ANNOT);
                                diagnostics.add(diagnostic);
	                        }
					    }
					}
				}
			}
		}
	}

	/* Based on https://github.com/eclipse/lsp4mp/blob/9789a1a996811fade43029605c014c7825e8f1da/microprofile.jdt/org.eclipse.lsp4mp.jdt.core/src/main/java/org/eclipse/lsp4mp/jdt/core/utils/JDTTypeUtils.java#L294-L298 */
	private boolean isWrapper(String valueClass) {
	    return WebSocketConstants.WRAPPER_OBJS
	            .stream()
	            .anyMatch(wrapper -> wrapper.equals(valueClass));
	}
	
	/* Check if the type is a websocket endpoint */
	private HashMap<String, Boolean> isWSEndpoint(IType type) throws JavaModelException {
		HashMap<String, Boolean> wsEndpoint = new HashMap<>();

		// check trivial case
		if (!type.isClass()) {
			wsEndpoint.put("isAnnotation", false);
			wsEndpoint.put("isSuperClass", false);
			return wsEndpoint;
		}

		// Check that class follows https://jakarta.ee/specifications/websocket/2.0/websocket-spec-2.0.html#applications
		
		List<String> scopes = AnnotationUtil.getScopeAnnotations(type, WebSocketConstants.WS_ANNOTATION_CLASS);
		boolean useAnnotation = scopes.size() > 0;

		boolean useSuperclass = false;

		String superclass = type.getSuperclassName();
		try {
			int r = doesITypeHaveSuperType(type, WebSocketConstants.ENDPOINT_SUPERCLASS);
			useSuperclass = (r >= 0);
		} catch (CoreException e) {
			JakartaCorePlugin.logException(WebSocketConstants.DIAGNOSTIC_ERR_MSG, e);
		}

		wsEndpoint.put("isAnnotation", useAnnotation);
		wsEndpoint.put("isSuperClass", useSuperclass);

		return wsEndpoint;
	}
}
