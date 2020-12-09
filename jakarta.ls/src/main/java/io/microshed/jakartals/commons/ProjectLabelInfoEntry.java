package io.microshed.jakartals.commons;

import java.util.Collections;
import java.util.List;

/**
 * Stores labels for the project located at a specific project uri
 *
 * @author Ankush Sharma, credit to dakwon
 *
 */
public class ProjectLabelInfoEntry {
    public static final ProjectLabelInfoEntry EMPTY_PROJECT_INFO = new ProjectLabelInfoEntry("",
            Collections.emptyList());

    private final String uri;
    private final List<String> labels;

    public ProjectLabelInfoEntry(String uri, List<String> labels) {
        this.uri = uri;
        this.labels = labels;
    }

    /**
     * Returns the project uri
     *
     * @return the project uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * Returns the labels for the current project uri
     *
     * @return the labels for the current project uri
     */
    public List<String> getLabels() {
        return labels;
    }

    /**
     * Returns true if the project has the given label and false otherwise.
     *
     * @param label the label.
     * @return true if the project has the given label and false otherwise.
     */
    public boolean hasLabel(String label) {
        return labels != null && labels.contains(label);
    }
}
