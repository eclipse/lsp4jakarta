package org.jakarta.jdt;

import java.util.List;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Range;
import org.jakarta.lsp4e.Activator;

public class BeanValidationDiagnosticsCollector  implements DiagnosticsCollector {

	public void collectDiagnostics(ICompilationUnit unit, List<Diagnostic> diagnostics) {

		if (unit != null) {
			IType[] alltypes;
			IField[] allFields;
			IAnnotation[] allFieldAnnotations;

			try {
				alltypes = unit.getAllTypes();
				for (IType type : alltypes) {
					allFields = type.getFields();
					for(IField field: allFields) {


						allFieldAnnotations = field.getAnnotations();
						String fieldType = field.getTypeSignature();

						System.out.println("--Field name: " + field.getElementName());
						System.out.println("--Field type: " + field.getTypeSignature());

						for (IAnnotation annotation : allFieldAnnotations) {
							if (annotation.getElementName().equals("AssertFalse")
									|| annotation.getElementName().equals("AssertTrue")) {

								if (!fieldType.equals("Z") || !fieldType.equals("QBoolean")) {
									ISourceRange fieldAnnotationNameRange = JDTUtils.getNameRange(annotation);
									Range fieldAnnotationrange = JDTUtils.toRange(unit, fieldAnnotationNameRange.getOffset(),
											fieldAnnotationNameRange.getLength());
									diagnostics.add(new Diagnostic(fieldAnnotationrange, "This annotation can only be used on boolean and Boolean type fields."));	

								}

							}
						}




					}
				}

			} catch (JavaModelException e) {
				Activator.logException("Cannot calculate diagnostics", e);
			}
		}

	}

}