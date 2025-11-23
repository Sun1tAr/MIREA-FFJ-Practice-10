package my.learn.mireaffjpractice10.controller;


import lombok.RequiredArgsConstructor;
import my.learn.mireaffjpractice10.dto.AdminStats;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        return new ResponseEntity<>(new AdminStats(), HttpStatus.OK);
    }

}
