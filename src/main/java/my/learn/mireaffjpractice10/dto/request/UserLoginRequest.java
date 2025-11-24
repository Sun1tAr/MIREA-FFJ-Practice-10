package my.learn.mireaffjpractice10.dto.request;

import lombok.Data;

@Data
public class UserLoginRequest {

    private String email;
    private String password;

}
