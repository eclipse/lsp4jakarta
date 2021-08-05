/*******************************************************************************
 * Copyright (c) 2021 IBM Corporation, Matthew Shocrylas and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation, Matthew Shocrylas - initial API and implementation, Bera Sogut
 *******************************************************************************/

package org.eclipse.lsp4jakarta.jdt.core.jax_rs;

import java.util.ArrayList;
import java.util.List;

public class Jax_RSConstants {

    public static final String RESOURCE_METHOD = "ResourceMethod";

    /* Annotation Constants */
    public static final ArrayList<String> METHOD_DESIGNATORS = new ArrayList<String>(
            List.of("GET", "POST", "PUT", "DELETE", "PATCH", "HEAD", "OPTIONS"));

    /* Annotations which make a resource method parameter a non entity parameter. */
    public static final ArrayList<String> NON_ENTITY_PARAM_ANNOTATIONS = new ArrayList<String>(
            List.of("FormParam", "MatrixParam", "QueryParam", "PathParam", "CookieParam", "HeaderParam", "Context"));

    public static final String PATH_ANNOTATION = "Path";

    /* Source */
    public static final String DIAGNOSTIC_SOURCE = "jakarta-jax_rs";

    /* Diagnostics fields constants */
    public static final String DIAGNOSTIC_CODE_NON_PUBLIC = "NonPublicResourceMethod";
    public static final String DIAGNOSTIC_CODE_MULTIPLE_ENTITY_PARAMS = "ResourceMethodMultipleEntityParams";
    public static final String DIAGNOSTIC_CODE_UNUSED_CONSTRUCTOR = "UnusedConstructor";
    public static final String DIAGNOSTIC_CODE_AMBIGUOUS_CONSTRUCTORS = "AmbiguousConstructors";

}
