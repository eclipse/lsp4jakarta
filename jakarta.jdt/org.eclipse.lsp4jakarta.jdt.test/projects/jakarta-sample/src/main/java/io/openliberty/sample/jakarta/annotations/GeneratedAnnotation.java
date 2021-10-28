import jakarta.annotation.Generated;


@Generated(value = "demoServlet", date="")
public class GraduatingStudent {

	@Generated(value = "demoServlet", date="not_ISO_compliant")
    private Integer studentId;

	// @Generated(value = "demoServlet", date="2001-07-04T12:08:56.235-0700")
    private boolean isHappy;

	// @Generated(value = "demoServletijiojioj", date="")
    private boolean isSad;


    private String emailAddress;

    public GraduatingStudent(Integer studentId, Boolean isHappy, boolean isSad, Calendar graduationDate, Integer gpa,
            String emailAddress) {
        this.studentId = studentId;
        this.isHappy = isHappy;
        this.isSad = isSad;
        this.emailAddress = emailAddress;
    }

}