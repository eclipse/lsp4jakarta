package org.jakarta.jdt.persistence;

import org.eclipse.lsp4j.DiagnosticSeverity;

public class PersistenceConstants {
    /* Annotation Constants */
    public static final String ENTITY = "Entity";
    public static final String MAPKEY = "MapKey";
    public static final String MAPKEYCLASS = "MapKeyClass";
    public static final String MAPKEYJOINCOLUMN = "MapKeyJoinColumn";

    /* Annotation Fields */
    public static final String NAME = "name";
    public static final String REFERENCEDCOLUMNNAME = "referencedColumnName";

    /* Source */
    public static final String DIAGNOSTIC_SOURCE = "jakarta-persistence";

    /* Severity */
    public static final DiagnosticSeverity SEVERITY = DiagnosticSeverity.Error;

    /* Entity Codes */
    public static final String DIAGNOSTIC_CODE_MISSING_EMPTY_CONSTRUCTOR = "MissingEmptyConstructor";
    public static final String DIAGNOSTIC_CODE_FINAL_METHODS = "RemoveFinalMethods";
    public static final String DIAGNOSTIC_CODE_FINAL_VARIABLES = "RemoveFinalVariables";
    public static final String DIAGNOSTIC_CODE_FINAL_CLASS = "InvalidClass";

    /* MapKey Codes */
    public static final String DIAGNOSTIC_CODE_INVALID_ANNOTATION = "RemoveMapKeyorMapKeyClass";
    public static final String DIAGNOSTIC_CODE_MISSING_ATTRIBUTES = "SupplyAttributesToAnnotations";

}