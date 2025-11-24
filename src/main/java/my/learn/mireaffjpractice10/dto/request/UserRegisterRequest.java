package my.learn.mireaffjpractice10.dto.request;

import lombok.Data;

@Data
public class UserRegisterRequest {

    private String email;
    private String password;
    private boolean autoLogin;
}
