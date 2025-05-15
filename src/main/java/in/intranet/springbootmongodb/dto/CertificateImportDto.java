package in.intranet.springbootmongodb.dto;

import lombok.Data;

import java.util.Date;

@Data
public class CertificateImportDto {
    private String file;
    private String company;
    private String code;
    private String cpfCnpj;
    private String municipality;
    private String uf;
    private String type;
    private String password;
    private Date maturityDate;
}
