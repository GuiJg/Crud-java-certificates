package in.intranet.springbootmongodb.model;

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
@Document(collection = "certificate")
public class CertificateModel {

    @Id
    private String id;
    private String name;
    private Number code;
    private String company;
    private String cnpj;
    private String municipality;
    private String uf;
    private String type;
    private String status;
    private Date maturityDate;
    private Date createdAt;
    private Date updatedAt;
}
