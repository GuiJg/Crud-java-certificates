package in.intranet.springbootmongodb.model;

import in.intranet.springbootmongodb.enums.Roles;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class UserModel {

    @Id
    private String id;

    @NotBlank(message = "Nome de usuário é obrigatório")
    private String username;

    @Email(message = "E-mail inválido")
    @Pattern(regexp = "^[\\w.+\\-]+@absolutacontabilidade\\.com\\.br$", message = "Somente e-mails do domínio absolutacontabilidade.com.br são permitidos")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 12, message = "Senha deve ter pelo menos 12 caracteres")
    private String password;

    @Size(min = 1, max = 2, message = "Você pode atribuir no máximo 2 departamentos")
    private List<Roles> roles;
}
