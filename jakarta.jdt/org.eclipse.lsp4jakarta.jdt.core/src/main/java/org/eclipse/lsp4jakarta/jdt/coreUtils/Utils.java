package org.eclipse.lsp4jakarta.jdt.coreUtils;


import org.eclipse.jdt.core.IAnnotatable;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.HashSet;

public class Utils {
    /**
     * Returns the list of recognised defining annotations applied to a
     * class.
     * 
     * @param type the type representing the class
     * @param scopes list of defining annotations
     * @return list of recognised defining annotations applied to a class
     */
    public static List<String> getScopeAnnotations(IAnnotatable type, Set<String> scopes) {
        try {
            // Construct a stream of only the annotations applied to the type that are also
            // recognised managed bean annotations.
            return Arrays.stream(type.getAnnotations()).map(annotation -> annotation.getElementName())
                    .filter(scopes::contains).distinct().collect(Collectors.toList());

        } catch (Exception e) {
            return Collections.<String>emptyList();
        }
    }
    
    public static List<String> getScopeAnnotation(IAnnotatable type, String scope) {
        try {
            // Construct a stream of only the annotations applied to the type that are also
            // recognised managed bean annotations.
            return Arrays.stream(type.getAnnotations()).map(annotation -> annotation.getElementName())
                    .filter(scope::equals).distinct().collect(Collectors.toList());

        } catch (Exception e) {
            return Collections.<String>emptyList();
        }
    }
}
