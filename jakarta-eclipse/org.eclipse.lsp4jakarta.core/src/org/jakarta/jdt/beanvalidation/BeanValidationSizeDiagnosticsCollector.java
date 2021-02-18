package org.jakarta.jdt.beanvalidation;

import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.DIAGNOSTIC_SOURCE;
import static org.jakarta.jdt.beanvalidation.BeanValidationConstants.SEVERITY;

import java.util.List;
import java.util.Objects;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.lsp4j.Diagnostic;
import org.jakarta.jdt.DiagnosticsCollector;

public class BeanValidationSizeDiagnosticsCollector implements DiagnosticsCollector {

    @Override
    public void completeDiagnostic(Diagnostic diagnostic) {
        // TODO Auto-generated method stub
        diagnostic.setSource(DIAGNOSTIC_SOURCE);
        diagnostic.setSeverity(SEVERITY);
    }
    

    @Override
    public void collectDiagnostics(ICompilationUnit unit, List<Diagnostic> diagnostics) {

        if (Objects.isNull(unit)) {
            return;
        }

        try {
            IType[] types = unit.getAllTypes();

            for (IType type : types) {

                IField[] fields = type.getFields();

                for (IField field : fields) {

                    IAnnotation[] annotations = field.getAnnotations();

                    for (IAnnotation annotation : annotations) {
                        
                        if (BeanValidationConstants.SIZE.equals(annotation.getElementName())) {
                            
                        }
                    }

                }

            }

        } catch (

        JavaModelException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
