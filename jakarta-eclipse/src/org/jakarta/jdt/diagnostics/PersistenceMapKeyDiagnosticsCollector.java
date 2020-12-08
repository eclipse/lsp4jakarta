package org.jakarta.jdt.diagnostics;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Range;
import org.jakarta.jdt.JDTUtils;
import org.jakarta.jdt.PersistenceConstants;
import org.jakarta.lsp4e.Activator;

import java.util.ArrayList;
import java.util.List;

public class PersistenceMapKeyDiagnosticsCollector implements DiagnosticsCollector {
	
	public PersistenceMapKeyDiagnosticsCollector() {
		
	}
	
	private Diagnostic createDiagnostic(IJavaElement el, ICompilationUnit unit, String msg, String code) {
		try {
			ISourceRange nameRange = JDTUtils.getNameRange(el);
			Range range = JDTUtils.toRange(unit, nameRange.getOffset(), nameRange.getLength());
			Diagnostic diagnostic = new Diagnostic(range, msg);
			diagnostic.setCode(code);
			return diagnostic;
		} catch(JavaModelException e) {
			Activator.logException("Cannot calculate diagnostics", e);
		}
		return null;
	}

	@Override
	public void completeDiagnostic(Diagnostic diagnostic) {
		diagnostic.setSource(PersistenceConstants.DIAGNOSTIC_SOURCE);
		diagnostic.setSeverity(PersistenceConstants.SEVERITY);
	}

	
	@Override
	public void collectDiagnostics(ICompilationUnit unit, List<Diagnostic> diagnostics) {
		// TODO Auto-generated method stub
		if(unit != null) {
			IType[] alltypes;
			IAnnotation[] allAnnotations;
			
			try {
				alltypes = unit.getAllTypes();
				for (IType type : alltypes) {
					allAnnotations = type.getAnnotations();
					
					ISourceRange nameRange = JDTUtils.getNameRange(type);
					Range range = JDTUtils.toRange(unit, nameRange.getOffset(), nameRange.getLength());
					

					/* ========= MapKey and MapKeyColumn Annotations Checks ========= */
					for (IMethod method: type.getMethods()) {
						IAnnotation MapKeyAnnotation = null;
						IAnnotation MapKeyClassAnnotation = null;
						for (IAnnotation annotation: method.getAnnotations()) {
							if (annotation.getElementName().equals(PersistenceConstants.MAPKEY)) MapKeyAnnotation =  annotation;
							if (annotation.getElementName().equals(PersistenceConstants.MAPKEYCLASS)) MapKeyClassAnnotation = annotation;
						}
						if (MapKeyAnnotation != null && MapKeyClassAnnotation != null) {
							// A single field cannot have the same
							diagnostics.add(createDiagnostic(method, unit, "@MapKeyClass and @MapKey annotations cannot be used on the same field or property", PersistenceConstants.DIAGNOSTIC_CODE_INVALID_ANNOTATION));
						}
					}
					
					// Go through each field to ensure they do not have both MapKey and MapKeyColumn Annotations
					for (IField field: type.getFields()) {
						IAnnotation MapKeyAnnotation = null;
						IAnnotation MapKeyClassAnnotation = null;
						for (IAnnotation annotation: field.getAnnotations()) {
							if (annotation.getElementName().equals(PersistenceConstants.MAPKEY)) MapKeyAnnotation =  annotation;
							if (annotation.getElementName().equals(PersistenceConstants.MAPKEYCLASS)) MapKeyClassAnnotation = annotation;
						}
						if (MapKeyAnnotation != null && MapKeyClassAnnotation != null) {
							// A single field cannot have the same
							diagnostics.add(createDiagnostic(field, unit, "@MapKeyClass and @MapKey annotations cannot be used on the same field or property", PersistenceConstants.DIAGNOSTIC_CODE_INVALID_ANNOTATION));
						}
						
					}
					
					/* ======== MapKeyJoinColumn Diagnostic Checks ========= */
					for (IMethod method: type.getMethods()) {
						List<IAnnotation> mapKeyJoinCols = new ArrayList<IAnnotation>();
						for (IAnnotation annotation: method.getAnnotations()) {
							if (annotation.getElementName().equals(PersistenceConstants.MAPKEYJOINCOLUMN)) {
								mapKeyJoinCols.add(annotation);
							}
						}
						if (mapKeyJoinCols.size() <= 1) continue;
						
						// If we have multiple MapKeyJoinColumn annotations on a single method we must ensure each has a name and referencedColumnName
						mapKeyJoinCols.forEach((annotation) -> {
							boolean isNameSpecified = false;
							boolean isReferencedColumnNameSpecified = false;
							try {
								IMemberValuePair[] memberValues =  annotation.getMemberValuePairs();
								for (IMemberValuePair mv: memberValues) {
									if (mv.getMemberName().equals(PersistenceConstants.NAME)) {
										isNameSpecified = true;
										continue;
									}
									
									if (mv.getMemberName().equals(PersistenceConstants.REFERENCEDCOLUMNNAME)) {
										isReferencedColumnNameSpecified = true;
										continue;
									}
								}
								if (!isNameSpecified || !isReferencedColumnNameSpecified) {
									diagnostics.add(createDiagnostic(method, unit, "A field with multiple @MapKeyJoinColumn annotations must specify both the name and referencedColumnName attributes in the corresponding @MapKeyJoinColumn annotations.", PersistenceConstants.DIAGNOSTIC_CODE_MISSING_ATTRIBUTES));
								}
							} catch (JavaModelException e) {
								Activator.logException("Error while retrieving member values of @MapKeyJoinColumn Annotation", e);
							}
						});
						
					}
					
					for (IField field: type.getFields()) {
						List<IAnnotation> mapKeyJoinCols = new ArrayList<IAnnotation>();
						for (IAnnotation annotation: field.getAnnotations()) {
							if (annotation.getElementName().equals(PersistenceConstants.MAPKEYJOINCOLUMN)) {
								mapKeyJoinCols.add(annotation);
							}
						}
						if (mapKeyJoinCols.size() <= 1) continue;
						
						mapKeyJoinCols.forEach((annotation) -> {
							boolean isNameSpecified = false;
							boolean isReferencedColumnNameSpecified = false;
							try {
								IMemberValuePair[] memberValues =  annotation.getMemberValuePairs();
								for (IMemberValuePair mv: memberValues) {
									if (mv.getMemberName().equals(PersistenceConstants.NAME)) {
										isNameSpecified = true;
										continue;
									}
									
									if (mv.getMemberName().equals(PersistenceConstants.REFERENCEDCOLUMNNAME)) {
										isReferencedColumnNameSpecified = true;
										continue;
									}
								}
								if (!isNameSpecified || !isReferencedColumnNameSpecified) {
									diagnostics.add(createDiagnostic(field, unit, "A field with multiple @MapKeyJoinColumn annotations must specify both the name and referencedColumnName attributes in the corresponding @MapKeyJoinColumn annotations.", PersistenceConstants.DIAGNOSTIC_CODE_MISSING_ATTRIBUTES));
								}
							} catch (JavaModelException e) {
								Activator.logException("Error while retrieving member values of @MapKeyJoinColumn Annotation", e);
							}
						});
						
					}
					
					

				}
			} catch(JavaModelException e) {
				Activator.logException("Cannot calculate diagnostics", e);
			}
					
		}
		
	}
}
