package my.learn.mireaffjpractice10.controller;


import lombok.RequiredArgsConstructor;
import my.learn.mireaffjpractice10.dto.response.AdminStats;
import my.learn.mireaffjpractice10.service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admins")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/stats")
    public ResponseEntity<AdminStats> getStats() {
        return new ResponseEntity<>(adminService.getStats(), HttpStatus.OK);
    }


}
