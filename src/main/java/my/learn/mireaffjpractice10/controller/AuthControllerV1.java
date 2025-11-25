package my.learn.mireaffjpractice10.controller;

import lombok.RequiredArgsConstructor;
import my.learn.mireaffjpractice10.dto.request.UserLoginRequest;
import my.learn.mireaffjpractice10.dto.response.AuthResponse;
import my.learn.mireaffjpractice10.dto.request.UserRegisterRequest;
import my.learn.mireaffjpractice10.model.Token;
import my.learn.mireaffjpractice10.model.User;
import my.learn.mireaffjpractice10.model.UserRole;
import my.learn.mireaffjpractice10.service.UserAuthService;
import my.learn.mireaffjpractice10.service.TokenService;
import my.learn.mireaffjpractice10.service.UserService;
import my.learn.mireaffjpractice10.util.CommonMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthControllerV1 {

    private final CommonMapper mapper;
    private final UserService userService;
    private final TokenService tokenService;
    private final UserAuthService userAuthService;



    @PostMapping("/register")
    @Transactional
    public ResponseEntity<AuthResponse> registerUser(@RequestBody UserRegisterRequest userRegisterRequest) {
        User user = userAuthService.registerUser(userRegisterRequest);
        Token token = tokenService.generateAccessToken(user);
        return new ResponseEntity<>(mapper.mapToAuthResponse(token), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody UserLoginRequest userLoginRequest) {
        User user = userAuthService.loginUser(userLoginRequest);
        Token token = tokenService.generateAccessToken(user);
        return new ResponseEntity<>(mapper.mapToAuthResponse(token), HttpStatus.OK);
    }

    // Метод-заглушка для упрощенного назначения админских прав
    // ХАРДКОООД - нужен СТРОГО для изучения технологии
    @PostMapping("/addAdminAuthorities/{email}")
    public ResponseEntity<AuthResponse> addAdminAuthorities(@PathVariable("email") String email) {
        User user = userService.findUserByEmail(email);
        user.setRoles(List.of(UserRole.USER, UserRole.ADMIN));
        User saved = userService.save(user);
        Token token = tokenService.generateAccessToken(saved);
        return new ResponseEntity<>(mapper.mapToAuthResponse(token), HttpStatus.OK);
    }


}
