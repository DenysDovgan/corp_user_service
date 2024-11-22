package school.faang.user_service.pojo.person;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.opencsv.bean.CsvRecurse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Person {

    private String firstName;
    private String lastName;
    private Integer yearOfBirth;
    private String group;
    private String studentID;
    @JsonProperty("contactInfo")
    private ContactInfo contactInfo;
    @CsvRecurse
    private Education education;
    private String status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date admissionDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date graduationDate;
    @CsvRecurse
    private List<PreviousEducation> previousEducation;
    private Boolean scholarship;
    private String employer;

}
