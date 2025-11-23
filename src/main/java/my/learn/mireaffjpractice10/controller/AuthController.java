package my.learn.mireaffjpractice10.controller;

import lombok.RequiredArgsConstructor;
import my.learn.mireaffjpractice10.dto.AuthResponse;
import my.learn.mireaffjpractice10.dto.UserRequest;
import my.learn.mireaffjpractice10.model.User;
import my.learn.mireaffjpractice10.service.UserService;
import my.learn.mireaffjpractice10.util.JwtTokenUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;


    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@RequestBody UserRequest userRequest) {
        User newUser = userService.createNewUser(userRequest);
        //todo get token
        AuthResponse response = new AuthResponse("");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody UserRequest userRequest) {

        //todo get user and token
        AuthResponse response = new AuthResponse("");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }







}
