package my.learn.mireaffjpractice10.controller;

import lombok.RequiredArgsConstructor;
import my.learn.mireaffjpractice10.dto.request.UserLoginRequest;
import my.learn.mireaffjpractice10.dto.request.UserRegisterRequest;
import my.learn.mireaffjpractice10.dto.response.AuthResponse;
import my.learn.mireaffjpractice10.exception.AppException;
import my.learn.mireaffjpractice10.model.Token;
import my.learn.mireaffjpractice10.model.TokenPurpose;
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
import org.springframework.security.core.userdetails.UserDetails;
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
        List<Token> tokens = registerUserAndGetToken(userRegisterRequest);

        refreshTokenService.saveRefreshToken(getRefreshToken(tokens));

        return new ResponseEntity<>(
                mapper.mapToAuthResponse(
                        getAccessToken(tokens),
                        getRefreshToken(tokens)
                ),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody UserLoginRequest userLoginRequest) {
        List<Token> tokens = loginUserAndGetToken(userLoginRequest);

        refreshTokenService.saveRefreshToken(getRefreshToken(tokens));

        return new ResponseEntity<>(
                mapper.mapToAuthResponse(
                        getAccessToken(tokens),
                        getRefreshToken(tokens)
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

    private List<Token> registerUserAndGetToken(UserRegisterRequest req) {
        if (userService.userIsExists(req.getEmail())) {
            throw new AppException("User already exists", HttpStatus.CONFLICT);
        }

        User user = User.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .roles(List.of(UserRole.USER))
                .build();

        User saved = userService.save(user);

        Token access = jwtTokenUtils.generateAccessToken(saved);
        Token refresh = jwtTokenUtils.generateRefreshToken(saved);
        return List.of(access, refresh);
    }

    private List<Token> loginUserAndGetToken(UserLoginRequest req) {
        Authentication authenticate;
        try {
            authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
            );
        } catch (AuthenticationException e) {
            throw new AppException(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }

        SecurityContextHolder.getContext().setAuthentication(authenticate);

        Token access = jwtTokenUtils.generateAccessToken((UserDetails) authenticate.getPrincipal());
        Token refresh = jwtTokenUtils.generateRefreshToken((UserDetails) authenticate.getPrincipal());

        return List.of(access, refresh);
    }

    private Token getAccessToken(List<Token> tokens) {
        return tokens.stream()
                .filter(t -> t.getPurpose().equals(TokenPurpose.ACCESS))
                .findFirst()
                .orElseThrow(() -> new AppException("Access token not found", HttpStatus.INTERNAL_SERVER_ERROR));
    }

    private Token getRefreshToken(List<Token> tokens) {
        return tokens.stream()
                .filter(t -> t.getPurpose().equals(TokenPurpose.REFRESH))
                .findFirst()
                .orElseThrow(() -> new AppException("Refresh token not found", HttpStatus.INTERNAL_SERVER_ERROR));
    }
}