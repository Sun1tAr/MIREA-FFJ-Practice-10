package my.learn.mireaffjpractice10.service;

import lombok.RequiredArgsConstructor;
import my.learn.mireaffjpractice10.dto.request.TokenRefreshRequest;
import my.learn.mireaffjpractice10.dto.request.UserLoginRequest;
import my.learn.mireaffjpractice10.dto.request.UserRegisterRequest;
import my.learn.mireaffjpractice10.exception.AppException;
import my.learn.mireaffjpractice10.model.User;
import my.learn.mireaffjpractice10.model.UserRole;
import my.learn.mireaffjpractice10.util.JwtTokenUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAuthService {

    private final UserService userService;
    private final TokenService tokenService;
    private final JwtTokenUtils jwtTokenUtils;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;


    public User registerUser(UserRegisterRequest userRegisterRequest) {
        if (userService.userIsExists(userRegisterRequest.getEmail())) {
            throw new AppException("User already exists", HttpStatus.CONFLICT);
        }

        User user =  User.builder()
                .email(userRegisterRequest.getEmail())
                .password(passwordEncoder.encode(userRegisterRequest.getPassword()))
                .roles(List.of(UserRole.USER))
                .build();

        return userService.save(user);
    }

    public User loginUser(UserLoginRequest userLoginRequest) {
        Authentication authenticate;
        try {
            authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userLoginRequest.getEmail(),
                            userLoginRequest.getPassword())
            );
        } catch (AuthenticationException e) {
            throw new AppException(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }

        SecurityContextHolder.getContext().setAuthentication(authenticate);
        return (User) authenticate.getPrincipal();
    }

    public void logoutUser() {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        tokenService.deleteRefreshToken(principal.getId());
    }

    public User refreshToken(TokenRefreshRequest tokenRefreshRequest) {
        User userFromJwt = getUserFromJwt(tokenRefreshRequest.getRefreshToken());
        String deleted = tokenService.deleteRefreshToken(userFromJwt.getId());

        if (!deleted.equals(tokenRefreshRequest.getRefreshToken())) {
            throw new AppException("Invalid refresh token", HttpStatus.UNAUTHORIZED);
        }
        return userFromJwt;
    }

    private User getUserFromJwt(String token) {
        String username = jwtTokenUtils.getUsernameFromToken(token);
        return userService.findUserByEmail(username);
    }

}
