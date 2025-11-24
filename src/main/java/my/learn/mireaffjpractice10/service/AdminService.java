package my.learn.mireaffjpractice10.service;

import lombok.RequiredArgsConstructor;
import my.learn.mireaffjpractice10.dto.response.AdminStats;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserService userService;

    public AdminStats getStats() {
        return AdminStats.builder()
                .users(userService.getCountOfUsers())
                .version("1.0")
                .build();
    }

}
