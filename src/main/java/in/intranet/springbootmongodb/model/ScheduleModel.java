package in.intranet.springbootmongodb.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "schedule")
public class ScheduleModel {
    @Id
    private String id;
    private String slug;
    private String name;
    private String company;
    private String subject;
    private String phone;
    private String email;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private String date;    // formato: dd/MM/yyyy
    private String time; // formato: HH:mm
    private String createdBy; // email do usuário
    private Date createdAt;
    private Date updatedAt;
}
