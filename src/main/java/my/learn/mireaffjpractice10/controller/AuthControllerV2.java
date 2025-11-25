package my.learn.mireaffjpractice10.controller;

import lombok.RequiredArgsConstructor;
import my.learn.mireaffjpractice10.dto.request.TokenRefreshRequest;
import my.learn.mireaffjpractice10.dto.request.UserLoginRequest;
import my.learn.mireaffjpractice10.dto.request.UserRegisterRequest;
import my.learn.mireaffjpractice10.dto.response.AuthResponse;
import my.learn.mireaffjpractice10.exception.AppException;
import my.learn.mireaffjpractice10.model.Token;
import my.learn.mireaffjpractice10.model.User;
import my.learn.mireaffjpractice10.model.UserRole;
import my.learn.mireaffjpractice10.service.RefreshTokenService;
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
    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;


    @PostMapping("/register")
    @Transactional
    public ResponseEntity<AuthResponse> registerUser(@RequestBody UserRegisterRequest userRegisterRequest) {

        if (userService.userIsExists(userRegisterRequest.getEmail())) {
            throw new AppException("User already exists", HttpStatus.CONFLICT);
        }

        User user = User.builder()
                .email(userRegisterRequest.getEmail())
                .password(passwordEncoder.encode(userRegisterRequest.getPassword()))
                .roles(List.of(UserRole.USER))
                .build();

        User saved = userService.save(user);
        Token access = jwtTokenUtils.generateAccessToken(saved);
        Token refresh = jwtTokenUtils.generateRefreshToken(saved);

        refreshTokenService.saveRefreshToken(saved.getId(), refresh);

        return new ResponseEntity<>(
                mapper.mapToAuthResponse(
                        access,
                        refresh
                ),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody UserLoginRequest userLoginRequest) {
        Authentication authenticate;
        try {
            authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userLoginRequest.getEmail(), userLoginRequest.getPassword())
            );
        } catch (AuthenticationException e) {
            throw new AppException(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }

        SecurityContextHolder.getContext().setAuthentication(authenticate);

        User principal = (User) authenticate.getPrincipal();

        Token access = jwtTokenUtils.generateAccessToken(principal);
        Token refresh = jwtTokenUtils.generateRefreshToken(principal);

        refreshTokenService.saveRefreshToken(principal.getId(), refresh);

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
        Token accessToken = jwtTokenUtils.generateAccessToken(saved);
        Token refreshToken = jwtTokenUtils.generateRefreshToken(saved);

        refreshTokenService.saveRefreshToken(user.getId(), refreshToken);

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
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        refreshTokenService.deleteRefreshToken(principal.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody TokenRefreshRequest tokenRefreshRequest) {
        String username = jwtTokenUtils.getUsernameFromToken(tokenRefreshRequest.getRefreshToken());
        User user = userService.findUserByEmail(username);
        refreshTokenService.deleteRefreshToken(user.getId());

        if (refreshTokenService.isValidRefreshToken(user.getId(), tokenRefreshRequest.getRefreshToken())) {
            Token accessToken = jwtTokenUtils.generateAccessToken(user);
            Token refreshToken = jwtTokenUtils.generateRefreshToken(user);
            refreshTokenService.saveRefreshToken(user.getId(), refreshToken);
            return new ResponseEntity<>(mapper.mapToAuthResponse(accessToken, refreshToken), HttpStatus.OK);
        }
        throw new AppException("Access denied! Please repeat login.", HttpStatus.FORBIDDEN);
    }




}