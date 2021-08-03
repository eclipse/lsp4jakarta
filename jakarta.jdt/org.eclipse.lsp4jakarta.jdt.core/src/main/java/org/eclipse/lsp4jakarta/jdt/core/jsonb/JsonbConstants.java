package org.jakarta.jdt.jsonb;

public class JsonbConstants {
    
    /* Source */
    public static final String DIAGNOSTIC_SOURCE = "jakarta-jsonb";

    /* Code */
    public static final String DIAGNOSTIC_CODE_ANNOTATION = "MultipleJsonbCreatorAnnotations";
    
    /* Annotation Constants */
    public static final String JSONB_CREATOR = "JsonbCreator";
    public static final String ERROR_MESSAGE = "Only one constructor or static factory method can be annotated with @JsonbCreator in a given class.";
    public static final int MAX_METHOD_WITH_JSONBCREATOR = 1;
}
