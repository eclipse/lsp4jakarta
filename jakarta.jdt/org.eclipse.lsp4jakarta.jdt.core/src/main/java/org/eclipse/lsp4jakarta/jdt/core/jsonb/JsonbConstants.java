package org.eclipse.lsp4jakarta.jdt.core.jsonb;

public class JsonbConstants {

    /* Source */
    public static final String DIAGNOSTIC_SOURCE = "jakarta-jsonb";

    /* Code */
    public static final String DIAGNOSTIC_CODE_ANNOTATION = "MultipleJsonbCreatorAnnotations";
    public static final String DIAGNOSTIC_CODE_ANNOTATION_TRANSIENT_FIELD = "NonmutualJsonbTransientAnnotation";
    

    /* Annotation Constants */
    public static final String JSONB_PREFIX = "Jsonb";

    public static final String JSONB_CREATOR = JSONB_PREFIX + "Creator";
    public static final String ERROR_MESSAGE_JSONB_CREATOR = "Only one constructor or static factory method can be annotated with @JsonbCreator in a given class.";
    public static final int MAX_METHOD_WITH_JSONBCREATOR = 1;

    public static final String JSONB_TRANSIENT = JSONB_PREFIX + "Transient";
    public static final String ERROR_MESSAGE_JSONB_TRANSIENT = "@JsonbTransient must be mutually exclusive with all other JSON Binding defined annotations.";

}
