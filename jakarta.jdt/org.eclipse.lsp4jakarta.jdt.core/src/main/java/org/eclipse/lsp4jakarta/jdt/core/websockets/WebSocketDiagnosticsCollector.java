package org.eclipse.lsp4jakarta.jdt.core.websockets;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Set;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4jakarta.jdt.core.JDTUtils;
import org.eclipse.lsp4jakarta.jdt.core.DiagnosticsCollector;
import org.eclipse.lsp4jakarta.jdt.core.JakartaCorePlugin;
import org.eclipse.jdt.core.ISourceRange;

import org.eclipse.lsp4jakarta.jdt.coreUtils.Utils;



public class  WebSocketDiagnosticsCollector implements DiagnosticsCollector {
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

		try {
			alltypes = unit.getAllTypes();
			for (IType type : alltypes) {
				if (!isWebSocketClass(type)) {
					continue;
				}

				invalidParamsCheck(type, WebSocketConstants.ON_OPEN, WebSocketConstants.ON_OPEN_SET_PARAM_TYPES, unit, diagnostics);
			}
		} catch (JavaModelException e) {
			JakartaCorePlugin.logException("Cannot calculate diagnostics", e);
		}
	}

	private void invalidParamsCheck(IType type, String function, Set<String> validParamTypes, ICompilationUnit unit, List<Diagnostic> diagnostics) throws JavaModelException {
		// check methods annotations, then check their params, check that their type is any of the valid options, if it's a string, check that it has the @Params annotation
		// If so, set up the severity to Error, and add a code?

		IMethod[] allMethods = type.getMethods();

		for (IMethod method : allMethods) {
			IAnnotation[] allAnnotations = method.getAnnotations();

			for (IAnnotation annotation : allAnnotations) {
				if (annotation.getElementName().equals(function)) {
					// check params
					ILocalVariable[] allParams = method.getParameters();

					// TODO check if allParams is null
					for (ILocalVariable param : allParams) {
						String signature = param.getTypeSignature();
						String paramType = Signature.getSignatureSimpleName(signature);
						
						if (validParamTypes.contains(paramType)) {
							if (paramType.equals(WebSocketConstants.STRING)) {
								List<String> paramAnnotation = Utils.getScopeAnnotation(param, WebSocketConstants.PATH_PARAM_ANNOTATION);
								
								if (paramAnnotation.size() == 0) {
									// throw error
									// TODO create diagnostics
									 ISourceRange paramNameRange = JDTUtils.getNameRange(param);
									 Range paramRange = JDTUtils.toRange(
                                             unit,
                                             paramNameRange.getOffset(),
                                             paramNameRange.getLength());
		                             Diagnostic diagnostic = new Diagnostic(
		                            		 paramRange,
		                                     "It is missing @PathParams");
		                             diagnostics.add(diagnostic);
								}
							}
						}
						
					}
				}
			}
		}
	}
	
	/* Check if the type is a websocket class */
	private boolean isWebSocketClass(IType type) throws JavaModelException {
		if (!type.isClass()) {
			return false;
		}
		
		/* Check that class follows /* https://jakarta.ee/specifications/websocket/2.0/websocket-spec-2.0.html#applications */
		List<String> scopes = Utils.getScopeAnnotations(type, WebSocketConstants.WS_ANNOTATION_CLASS);

		boolean useAnnotation = scopes.size() > 0;
//		boolean useSuperclass = false;
//		
//		String superclass = type.getSuperclassName();
//		
//		if (superclass == null) {
//			useSuperclass = false;
//		} else {
//			useSuperclass = superclass.equals(WebSocketConstants.ENDPOINT_SUPERCLASS);
//		}
		
		if (useAnnotation) {
			return true;
		}
		return false;
	}
}