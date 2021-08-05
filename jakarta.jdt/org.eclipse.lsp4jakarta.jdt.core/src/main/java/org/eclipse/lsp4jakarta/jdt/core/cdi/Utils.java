package org.eclipse.lsp4jakarta.jdt.core.cdi;

import org.eclipse.jdt.core.IAnnotatable;
import org.eclipse.jdt.core.IType;

import static org.eclipse.lsp4jakarta.jdt.core.cdi.ManagedBeanConstants.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections;

public class Utils {
    /**
     * Detects if a class is a managed bean by looking for a bean defining
     * annotation.
     * 
     * @param type the type representing the potential bean class
     * @return true if the class has a bean defining annotation.
     */
    static boolean isManagedBean(IType type) {
        return getScopeAnnotations(type).size() > 0;
    }

    /**
     * Returns the list of recognised managed bean defining annotations applied to a
     * class.
     * 
     * @param type the type representing the class
     * @return list of recognised managed bean defining annotations.
     */
    static List<String> getScopeAnnotations(IAnnotatable type) {
        try {
            // Construct a stream of only the annotations applied to the type that are also
            // recognised managed bean annotations.
            return Arrays.stream(type.getAnnotations()).map(annotation -> annotation.getElementName())
                    .filter(SCOPES::contains).distinct().collect(Collectors.toList());

        } catch (Exception e) {
            return Collections.<String>emptyList();
        }
    }
}
