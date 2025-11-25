package my.learn.mireaffjpractice10.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import my.learn.mireaffjpractice10.dto.request.TokenRefreshRequest;
import my.learn.mireaffjpractice10.dto.request.UserLoginRequest;
import my.learn.mireaffjpractice10.dto.request.UserRegisterRequest;
import my.learn.mireaffjpractice10.dto.response.AuthResponse;
import my.learn.mireaffjpractice10.exception.AppException;
import my.learn.mireaffjpractice10.model.Token;
import my.learn.mireaffjpractice10.model.User;
import my.learn.mireaffjpractice10.model.UserRole;
import my.learn.mireaffjpractice10.service.TokenService;
import my.learn.mireaffjpractice10.service.RefreshTokenService;
import my.learn.mireaffjpractice10.service.UserAuthService;
import my.learn.mireaffjpractice10.service.UserService;
import my.learn.mireaffjpractice10.util.CommonMapper;
import my.learn.mireaffjpractice10.util.JwtTokenUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/auth")
public class AuthControllerV2 {

    private final CommonMapper mapper;
    private final JwtTokenUtils jwtTokenUtils;
    private final UserService userService;
    private final TokenService tokenService;
    private final UserAuthService userAuthService;


    @PostMapping("/register")
    @Transactional
    public ResponseEntity<AuthResponse> registerUser(@RequestBody @Valid UserRegisterRequest userRegisterRequest) {

        User saved = userAuthService.registerUser(userRegisterRequest);
        Token access = tokenService.generateAccessToken(saved);
        Token refresh = tokenService.generateRefreshToken(saved);

        tokenService.saveRefreshToken(saved.getId(), refresh);

        return new ResponseEntity<>(
                mapper.mapToAuthResponse(
                        access,
                        refresh
                ),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody @Valid UserLoginRequest userLoginRequest) {
        User principal = userAuthService.loginUser(userLoginRequest);

        Token access = tokenService.generateAccessToken(principal);
        Token refresh = tokenService.generateRefreshToken(principal);

        tokenService.saveRefreshToken(principal.getId(), refresh);

        return new ResponseEntity<>(
                mapper.mapToAuthResponse(
                        access,
                        refresh
                ),
                HttpStatus.OK
        );
    }

    // Метод-заглушка для упрощенного назначения админских прав
    // ХАРДКОООД - нужен СТРОГО для изучения технологии
    @PostMapping("/addAdminAuthorities/{email}")
    public ResponseEntity<AuthResponse> addAdminAuthorities(@PathVariable("email") String email) {
        User user = userService.findUserByEmail(email);
        user.setRoles(List.of(UserRole.USER, UserRole.ADMIN));
        User saved = userService.save(user);
        Token accessToken = tokenService.generateAccessToken(saved);
        Token refreshToken = tokenService.generateRefreshToken(saved);

        tokenService.saveRefreshToken(user.getId(), refreshToken);

        return new ResponseEntity<>(
                mapper.mapToAuthResponse(
                        accessToken,
                        refreshToken
                ),
                HttpStatus.OK
        );
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        userAuthService.logoutUser();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody @Valid TokenRefreshRequest tokenRefreshRequest) {
        User user = userAuthService.refreshToken(tokenRefreshRequest);

        Token accessToken = tokenService.generateAccessToken(user);
        Token refreshToken = tokenService.generateRefreshToken(user);

        tokenService.saveRefreshToken(user.getId(), refreshToken);

        return new ResponseEntity<>(mapper.mapToAuthResponse(accessToken, refreshToken), HttpStatus.OK);

    }




}