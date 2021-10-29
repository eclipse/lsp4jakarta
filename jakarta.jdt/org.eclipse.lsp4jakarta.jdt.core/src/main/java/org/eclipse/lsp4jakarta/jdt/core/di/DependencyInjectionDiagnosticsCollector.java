package org.eclipse.lsp4jakarta.jdt.core.di;
import static org.eclipse.lsp4jakarta.jdt.core.di.DependencyInjectionConstants.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4jakarta.jdt.core.DiagnosticsCollector;
import org.eclipse.lsp4jakarta.jdt.core.JDTUtils;
import org.eclipse.lsp4jakarta.jdt.core.JakartaCorePlugin;

public class DependencyInjectionDiagnosticsCollector implements DiagnosticsCollector{
	 
	private Diagnostic createDiagnostic(IJavaElement el, ICompilationUnit unit, String msg, String code) {
	        try {
	            ISourceRange nameRange = JDTUtils.getNameRange(el);
	            Range range = JDTUtils.toRange(unit, nameRange.getOffset(), nameRange.getLength());
	            Diagnostic diagnostic = new Diagnostic(range, msg);
	            diagnostic.setCode(code);
	            completeDiagnostic(diagnostic);
	            return diagnostic;
	        } catch (JavaModelException e) {
	        	JakartaCorePlugin.logException("Cannot calculate diagnostics", e);
	        }
	        return null;
	}
	    

	@Override
	public void completeDiagnostic(Diagnostic diagnostic) {
		diagnostic.setSource(DIAGNOSTIC_SOURCE);
        diagnostic.setSeverity(SEVERITY);
		
	}
	
	// checks if a method is a constructor
	private boolean isConstructorMethod(IMethod m) {
        try {
            return m.isConstructor();
        } catch (JavaModelException e) {
            return false;
        }
    }

	@Override
	public void collectDiagnostics(ICompilationUnit unit, List<Diagnostic> diagnostics) {
		if (unit == null)
            return;
		IType[] alltypes;
        try {	
			alltypes = unit.getAllTypes();
			for (IType type : alltypes) {
				List<IMethod> constructorMethods = Arrays.stream(type.getMethods())
	                    .filter(this::isConstructorMethod).collect(Collectors.toList());
				
				//there are no constructors
				if(constructorMethods.size() == 0)
					return;
				boolean hasInjectConstructor = false;
				int injectedConstructors = 0;
				for (IMethod m : constructorMethods) {
					hasInjectConstructor = Arrays.stream(m.getAnnotations())
                    .map(annotation -> annotation.getElementName())
                    .anyMatch(annotation -> annotation.equals("Inject"));
					if (hasInjectConstructor) {
						injectedConstructors++;
					}
					if(injectedConstructors > 1) {
						 diagnostics.add(createDiagnostic(m,unit,"Inject cannot be used with multiple constructors",DIAGNOSTIC_CODE_INJECT_CONSTRUCTOR));
					}
				}
				
				
			}
				
		}
		catch (JavaModelException e) {
        	JakartaCorePlugin.logException("Cannot calculate diagnostics", e);
        }
	}
}