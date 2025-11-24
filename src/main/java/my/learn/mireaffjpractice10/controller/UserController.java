package my.learn.mireaffjpractice10.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

//    @GetMapping("/me")
//    public ResponseEntity<UserDTO> getMe() {
//
//    }
//
//    @GetMapping("/logout")
//    public ResponseEntity<UserDTO> logout() {
//
//    }
//
//    @GetMapping("/refresh")
//    public ResponseEntity<UserDTO> refreshToken() {
//
//    }

    @GetMapping("/test")
    public String getTest() {
        return "So work!";
    }



}
