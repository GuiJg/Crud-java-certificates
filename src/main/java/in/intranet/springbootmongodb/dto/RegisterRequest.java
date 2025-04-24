package in.intranet.springbootmongodb.dto;

import in.intranet.springbootmongodb.enums.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank
    private String username;

    @Email
    @Pattern(regexp = "^[\\w.+\\-]+@absolutacontabilidade\\.com\\.br$")
    private String email;

    @NotBlank
    @Size(min = 6)
    private String password;

    @NotBlank
    private String confirmPassword;

    @Size(min = 1, max = 2)
    private List<Role> roles;
}
