/*******************************************************************************
* Copyright (c) 2020, 2022 IBM Corporation, Ankush Sharma and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0.
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*     IBM Corporation, Ankush Sharma - initial API and implementation
*******************************************************************************/

package org.eclipse.lsp4jakarta.jdt.core.persistence;

public class PersistenceConstants {
    /* Annotation Constants */
    public static final String ENTITY = "jakarta.persistence.Entity";
    public static final String MAPKEY = "jakarta.persistence.MapKey";
    public static final String MAPKEYCLASS = "jakarta.persistence.MapKeyClass";
    public static final String MAPKEYJOINCOLUMN = "jakarta.persistence.MapKeyJoinColumn";

    /* Annotation Fields */
    public static final String NAME = "name";
    public static final String REFERENCEDCOLUMNNAME = "referencedColumnName";

    /* Source */
    public static final String DIAGNOSTIC_SOURCE = "jakarta-persistence";

    /* Entity Codes */
    public static final String DIAGNOSTIC_CODE_MISSING_EMPTY_CONSTRUCTOR = "MissingEmptyConstructor";
    public static final String DIAGNOSTIC_CODE_FINAL_METHODS = "RemoveFinalMethods";
    public static final String DIAGNOSTIC_CODE_FINAL_VARIABLES = "RemoveFinalVariables";
    public static final String DIAGNOSTIC_CODE_FINAL_CLASS = "InvalidClass";

    /* MapKey Codes */
    public static final String DIAGNOSTIC_CODE_INVALID_ANNOTATION = "RemoveMapKeyorMapKeyClass";
    public static final String DIAGNOSTIC_CODE_MISSING_ATTRIBUTES = "SupplyAttributesToAnnotations";
    
    public final static String[] SET_OF_PERSISTENCE_ANNOTATIONS = {MAPKEY, MAPKEYCLASS, MAPKEYJOINCOLUMN};
}