package my.learn.mireaffjpractice10.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminStats {

    private Long users;
    private String version;

}
