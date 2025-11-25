package my.learn.mireaffjpractice10.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegisterRequest {

    @Email
    private String email;
    @Size(min = 6)
    @NotBlank
    private String password;
}
