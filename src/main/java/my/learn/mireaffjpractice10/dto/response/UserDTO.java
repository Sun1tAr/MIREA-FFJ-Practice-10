package my.learn.mireaffjpractice10.dto.response;

import lombok.Data;
import my.learn.mireaffjpractice10.model.UserRole;

import java.util.List;

@Data
public class UserDTO {

    private Long id;
    private String email;
    private List<UserRole> roles;

}
