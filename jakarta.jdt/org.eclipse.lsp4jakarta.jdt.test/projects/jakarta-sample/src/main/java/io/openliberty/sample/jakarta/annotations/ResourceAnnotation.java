package io.openliberty.sample.jakarta.annotations;

import jakarta.annotation.Resource;

@Resource(type = Object.class, name = "aa")
public class ResourceAnnotation {

    private Integer studentId;


	@Resource(shareable = true)
    private boolean isHappy;

	@Resource(name = "test")
    private boolean isSad;


    private String emailAddress;


}

@Resource(name = "aa")
class PostDoctoralStudent {

    private Integer studentId;


	@Resource(shareable = true)
    private boolean isHappy;

	@Resource
    private boolean isSad;


    private String emailAddress;

}

@Resource(type = Object.class)
class MasterStudent {

    private Integer studentId;

} 
