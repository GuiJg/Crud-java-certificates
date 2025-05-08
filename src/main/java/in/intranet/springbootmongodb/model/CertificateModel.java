package in.intranet.springbootmongodb.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import in.intranet.springbootmongodb.enums.Status;
import in.intranet.springbootmongodb.enums.Types;
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
    private String slug;
    private String file;
    private String company;
    private Number code;
    private String cpfCnpj;
    private String municipality;
    private String uf;
    private Types type;
    private Status status;
    private String password;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date maturityDate;
    private Date createdAt;
    private Date updatedAt;
}
