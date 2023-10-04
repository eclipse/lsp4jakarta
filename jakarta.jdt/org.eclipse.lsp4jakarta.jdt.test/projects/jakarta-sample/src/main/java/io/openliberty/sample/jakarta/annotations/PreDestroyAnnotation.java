package io.openliberty.sample.jakarta.annotations;

import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;

@Resource(type = Object.class, name = "aa") 
public class PreDestroyAnnotation { 

    private Integer studentId;
	
    private boolean isHappy;

    private boolean isSad;
	
	@PreDestroy()
	public Integer getStudentId() {
		return this.studentId;
	}
	
	@PreDestroy()
	public boolean getHappiness(String type) {
		if (type.equals("happy")) return this.isHappy;
		return this.isSad;
	}
	
	@PreDestroy()
	public static void makeUnhappy() {
		System.out.println("I'm sad");
	}
	
	@PreDestroy()
	public void throwTantrum() throws Exception {
		System.out.println("I'm sad");
	}


    private String emailAddress;


}



