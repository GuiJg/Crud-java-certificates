package in.intranet.springbootmongodb.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notifications")
public class NotificationModel {

    @Id
    private String id;

    private String user;
    private String company;
    private Date date;
    private String action;

    private Set<String> visualizadoPor = new HashSet<>();
}
