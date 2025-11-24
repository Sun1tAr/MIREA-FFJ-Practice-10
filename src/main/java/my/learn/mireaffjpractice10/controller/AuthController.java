package my.learn.mireaffjpractice10.controller;

import lombok.RequiredArgsConstructor;
import my.learn.mireaffjpractice10.dto.request.UserLoginRequest;
import my.learn.mireaffjpractice10.dto.response.AuthResponse;
import my.learn.mireaffjpractice10.dto.request.UserRegisterRequest;
import my.learn.mireaffjpractice10.exception.AppException;
import my.learn.mireaffjpractice10.model.Token;
import my.learn.mireaffjpractice10.model.User;
import my.learn.mireaffjpractice10.model.UserRole;
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
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final CommonMapper mapper;
    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;


    @PostMapping("/register")
    @Transactional
    public ResponseEntity<AuthResponse> registerUser(@RequestBody UserRegisterRequest userRegisterRequest) {
        Token token = registerUserAndGetToken(userRegisterRequest);
        return new ResponseEntity<>(mapper.mapToAuthResponse(token), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody UserLoginRequest userLoginRequest) {
        Token token = loginUserAndGetToken(userLoginRequest);
        return new ResponseEntity<>(mapper.mapToAuthResponse(token), HttpStatus.OK);
    }

    // Метод-заглушка для упрощенного назначения админских прав
    // ХАРДКОООД - нужен СТРОГО для изучения технологии
    @PostMapping("/addAdminAuthorities/{email}")
    public ResponseEntity<AuthResponse> addAdminAuthorities(@PathVariable("email") String email) {
        User user = userService.findUserByEmail(email);
        user.setRoles(List.of(UserRole.USER, UserRole.ADMIN));
        User saved = userService.save(user);
        Token token = jwtTokenUtils.generateAccessToken(saved);
        return new ResponseEntity<>(mapper.mapToAuthResponse(token), HttpStatus.OK);
    }


    private Token registerUserAndGetToken(UserRegisterRequest req) {
        if (userService.userIsExists(req.getEmail())) {
            throw new AppException("User already exists", HttpStatus.CONFLICT);
        }

        User user =  User.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .roles(List.of(UserRole.USER))
                .build();

        User saved = userService.save(user);

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                req.getEmail(),
                req.getPassword(),
                saved.getAuthorities()
        ));

        return jwtTokenUtils.generateAccessToken(saved);
    }


    private Token loginUserAndGetToken(UserLoginRequest req) {
        Authentication authenticate;
        try {
            authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
            );
        } catch (AuthenticationException e) {
            throw new AppException(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }

        SecurityContextHolder.getContext().setAuthentication(authenticate);

        return jwtTokenUtils.generateAccessToken((UserDetails) authenticate.getPrincipal());
    }

}
