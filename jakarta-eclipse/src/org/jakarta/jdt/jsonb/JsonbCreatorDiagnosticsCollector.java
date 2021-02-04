package org.jakarta.jdt.jsonb;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Range;
import org.jakarta.jdt.DiagnosticsCollector;
import org.jakarta.jdt.JDTUtils;
import org.jakarta.lsp4e.Activator;

public class JsonbCreatorDiagnosticsCollector implements DiagnosticsCollector {

	@Override
	public void completeDiagnostic(Diagnostic diagnostic) {

		diagnostic.setSource(JsonbConstants.DIAGNOSTIC_SOURCE);
		diagnostic.setSeverity(DiagnosticSeverity.Error);
	}

	@Override
	public void collectDiagnostics(ICompilationUnit unit, List<Diagnostic> diagnostics) {

		if (Objects.isNull(unit)) {
			return;
		}

		try {

			IType[] types = unit.getAllTypes();

			boolean annotationOnConstructor = false;
			boolean anyStaticMethodAnnotatedWithJsonbCreator = false;

			for (IType type : types) {

				IMethod[] methods = type.getMethods();

				for (IMethod method : methods) {

					if (method.isConstructor()) {
						
						IAnnotation jsonbCreatorAnnotation = method.getAnnotation(JsonbConstants.JSONB_CREATOR);
						
						if (!Objects.isNull(jsonbCreatorAnnotation)) {
							
							if (annotationOnConstructor) {
								
								Diagnostic diagnostic = createDiagnosticBy(unit, jsonbCreatorAnnotation);
								
								diagnostics.add(diagnostic);
							}
							
							annotationOnConstructor = true;
						}
					}

					method.getReturnType();
					
					if (Flags.isStatic(method.getFlags())
							&& !Objects.isNull(method.getAnnotation(JsonbConstants.JSONB_CREATOR))) {
						
						if (anyStaticMethodAnnotatedWithJsonbCreator || annotationOnConstructor) {
							
						}
						
						anyStaticMethodAnnotatedWithJsonbCreator = true;
					}
				}
			}

		} catch (JavaModelException e) {
			Activator.logException("Cannot calculate jakarta-jsonb diagnostics", e);
		}
	}
	

	private Diagnostic createDiagnosticBy(ICompilationUnit unit, IAnnotation jsonbCreatorAnnotation) throws JavaModelException {
		
		ISourceRange sourceRange = JDTUtils.getNameRange(jsonbCreatorAnnotation);
		
		Range range = JDTUtils.toRange(unit, sourceRange.getOffset(), sourceRange.getLength());
		
		return new Diagnostic(range, JsonbConstants.ERROR_MESSAGE);
	}

}
