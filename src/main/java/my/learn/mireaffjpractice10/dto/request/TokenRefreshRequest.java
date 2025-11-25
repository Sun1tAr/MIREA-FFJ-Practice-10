package my.learn.mireaffjpractice10.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TokenRefreshRequest {

    @NotNull
    private String refreshToken;

}
