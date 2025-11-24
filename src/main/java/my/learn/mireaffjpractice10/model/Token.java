package my.learn.mireaffjpractice10.model;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class Token {

    private String token;
    private TokenType type;
    private TokenPurpose purpose;
    private Date expiredAt;

}
