package org.eclipse.lsp4jakarta.jdt.core.websockets;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
import org.eclipse.lsp4jakarta.jdt.core.DiagnosticsCollector;
import org.eclipse.lsp4jakarta.jdt.core.JakartaCorePlugin;



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

				invalidParamsCheck(type, "OnOpen");
			}
		} catch (JavaModelException e) {
			JakartaCorePlugin.logException("Cannot calculate diagnostics", e);
		}
	}

	private void invalidParamsCheck(IType type, String function) throws JavaModelException {
		// check methods annotations, then check their params, check that their type is any of the valid options, if it's a string, check that it has the @Params annotation
		// If so, set up the severity to Error, and add a code?

		// if annotation
		IMethod[] allMethods = null;
		try {
			allMethods = type.getMethods();
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (IMethod method : allMethods) {
			IAnnotation[] allAnnotations = null;
			try {
				allAnnotations = method.getAnnotations();
			} catch (JavaModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			for (IAnnotation annotation : allAnnotations) {
				if (annotation.getElementName().equals(function)) {
					// check params
					ILocalVariable[] allParams = null;
					String[] paramsNames = method.getParameterNames();
					try {
						allParams = method.getParameters();
						
					} catch (JavaModelException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					for (String par : paramsNames) {
						ITypeParameter typePar = method.getTypeParameter(par);
						String test1 = typePar.getDeclaringMember().getDeclaringType().getFullyQualifiedName();
						String name = typePar.getElementName();
						System.out.println("");
					}

					// TODO check if allParams is null
					for (ILocalVariable param : allParams) {
						String signature = param.getTypeSignature();
						String test = param.getClass().getName();
						String elementType = Signature.getElementType(signature);
						String simpleName = Signature.getSignatureSimpleName(signature);
						try {
							IAnnotation[] annotations = param.getAnnotations();
						} catch (JavaModelException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
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
		
		IAnnotation[] allAnnotations = type.getAnnotations();
		// TODO change this one -> need to create a class for all the valid annotations
		List<String> scopes = Arrays.asList(type.getAnnotations()).stream().map(res -> res.getElementName()).filter(ann -> WebSocketConstants.WS_ANNOTATION_CLASS.contains(ann)).collect(Collectors.toList());

		boolean useAnnotation = scopes.size() > 0;
		boolean useSuperclass = false;
		
		String superclass = type.getSuperclassName();
		
		if (superclass == null) {
			useSuperclass = false;
		} else {
			useSuperclass = type.getSuperclassName().equals(WebSocketConstants.ENDPOINT_SUPERCLASS);
		}
		
		if (useAnnotation || useSuperclass) {
			return true;
		}
		// check if type.getAnnotations() is equal to the valid annotations
		// or 
		// type.getSuperclassName().getElementName() == "Endpoint"
		return false;
	}
}