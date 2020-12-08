package org.jakarta.jdt.diagnostics;

/**
 * @author ankushsharma
 * @brief Diagnostics implementation for Jakarta Persistence 3.0
 */

// Imports
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Flags;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Range;
import org.jakarta.jdt.JDTUtils;
import org.jakarta.jdt.PersistenceConstants;
import org.jakarta.lsp4e.Activator;

import java.util.List;
import java.util.Collection;


public class PersistenceEntityDiagnosticsCollector implements DiagnosticsCollector {
	
	public PersistenceEntityDiagnosticsCollector() {
		
	}
	
	
	/**
	 * check if the modifier provided is static
	 * @param flag
	 * @return
	 * @note modifier flags are an addition of all flags combined
	 */
	private boolean isStatic(int flag) {
		// If a field is static, we do not care about it, we care about all other field
		Integer isPublicStatic = flag - Flags.AccPublic;
		Integer isPrivateStatic = flag - Flags.AccPrivate;
		Integer isFinalStatic = flag - Flags.AccFinal;
		Integer isProtectedStatic = flag - Flags.AccProtected;
		Integer isStatic = flag;
		if (isPublicStatic.equals(Flags.AccStatic) || 
				isPrivateStatic.equals(Flags.AccStatic) || 
				isStatic.equals(Flags.AccStatic) ||
				isFinalStatic.equals(Flags.AccStatic) ||
				isProtectedStatic.equals(Flags.AccStatic))
		{
			return true;
		}
		return false;
	}
	
	/**
	 * check if the modifier provided is final
	 * @param flag
	 * @return
	 * @note modifier flags are an addition of all flags combined
	 */
	private boolean isFinal(int flag) {
		Integer isPublicFinal = flag - Flags.AccPublic;
		Integer isPrivateFinal = flag - Flags.AccPrivate;
		Integer isProtectedFinal = flag - Flags.AccProtected;
		Integer isFinal = flag;
		if (isPublicFinal.equals(Flags.AccFinal) || 
				isPrivateFinal.equals(Flags.AccFinal) || 
				isProtectedFinal.equals(Flags.AccFinal) ||
				isFinal.equals(Flags.AccFinal))
		{
			return true;
		}
		return false;
	}
	
	private Diagnostic createDiagnostic(IJavaElement el, ICompilationUnit unit, String msg, String code) {
		try {
			ISourceRange nameRange = JDTUtils.getNameRange(el);
			Range range = JDTUtils.toRange(unit, nameRange.getOffset(), nameRange.getLength());
			Diagnostic diagnostic = new Diagnostic(range, msg);
			diagnostic.setCode(code);
			completeDiagnostic(diagnostic);
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
		if (unit != null) {
			IType[] alltypes;
			IAnnotation[] allAnnotations; 
			try {
				alltypes = unit.getAllTypes();
				for (IType type: alltypes) {
					String className = type.getElementName();
					
					allAnnotations = type.getAnnotations();
					
					ISourceRange nameRange = JDTUtils.getNameRange(type);
					Range range = JDTUtils.toRange(unit, nameRange.getOffset(), nameRange.getLength());
					
					/*============ Entity Annotation Diagnostics ===========*/
					IAnnotation EntityAnnotation = null;
					for (IAnnotation annotation: allAnnotations) {
						if (annotation.getElementName().equals(PersistenceConstants.ENTITY)) {
							EntityAnnotation = annotation;
						}
					}
					
					if (EntityAnnotation != null) {
						
						ISourceRange annotationNameRange = JDTUtils.getNameRange(EntityAnnotation);
						Range annotationrange = JDTUtils.toRange(unit, annotationNameRange.getOffset(), annotationNameRange.getLength());
										
						// Define boolean requirements for the diagnostics
						boolean hasPublicOrProtectedNoArgConstructor = false;
						boolean hasArgConstructor = false;
						boolean isEntityClassFinal = false;
						boolean isMethodsOrPersistentVariablesFinal = false;
						
						// Get the Methods of the annotated Class
						for (IMethod method: type.getMethods()) {
							if (method.isConstructor()) {
								// We have found a method that is a constructor
								if (method.getNumberOfParameters() > 0)  {
									hasArgConstructor = true;
									continue;
								}
								
								// Don't need to perform subtractions to check flags because eclipse notifies on illegal constructor modifiers
								if (method.getFlags() != Flags.AccPublic && method.getFlags() != Flags.AccProtected) continue;
								
								hasPublicOrProtectedNoArgConstructor = true;
							}
							
							// All Methods of this class should not be final
							if (this.isFinal(method.getFlags())) {
								diagnostics.add(createDiagnostic(method, unit, "A class using the @Entity annotation cannot contain any methods that are declared final", PersistenceConstants.DIAGNOSTIC_CODE_FINAL_METHODS));
								isMethodsOrPersistentVariablesFinal = true;
							}
						}
						
						// Go through the instance variables and make sure no instance vars are final
						for (IField field: type.getFields()) {
							
							// If a field is static, we do not care about it, we care about all other field
							if(this.isStatic(field.getFlags()))
							{
								continue;
							}
							
							// If we find a non-static variable that is final, this is a problem
							if (this.isFinal(field.getFlags())) {
								diagnostics.add(createDiagnostic(field, unit, "A class using the @Entity annotation cannot contain any persistent instance variables that are declared final", PersistenceConstants.DIAGNOSTIC_CODE_FINAL_VARIABLES));
								isMethodsOrPersistentVariablesFinal = true;
							}
							
						}
						
						// Ensure that the Entity class is not given a final modifier
						if (this.isFinal(type.getFlags())) isEntityClassFinal = true;
						
						
						// Create Diagnostics if needed
						if (!hasPublicOrProtectedNoArgConstructor && hasArgConstructor) {
							diagnostics.add(createDiagnostic(type, unit, "A class using the @Entity annotation must contain a public or protected constructor with no arguments.", PersistenceConstants.DIAGNOSTIC_CODE_MISSING_EMPTY_CONSTRUCTOR));
						}

						if (isEntityClassFinal) {
							diagnostics.add(createDiagnostic(type, unit,  "A class using the @Entity annotation must not be final.", PersistenceConstants.DIAGNOSTIC_CODE_FINAL_CLASS));
						}
					}
				}
			} catch (JavaModelException e) {
				Activator.logException("Cannot calculate persistence diagnostics", e);
			}
		}
		// We do not do anything if the found unit is null
	}
	
}
