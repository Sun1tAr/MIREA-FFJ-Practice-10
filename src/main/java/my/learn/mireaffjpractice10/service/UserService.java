package my.learn.mireaffjpractice10.service;

import lombok.RequiredArgsConstructor;
import my.learn.mireaffjpractice10.dto.UserRequest;
import my.learn.mireaffjpractice10.exception.AppException;
import my.learn.mireaffjpractice10.model.User;
import my.learn.mireaffjpractice10.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
    }


    public User createNewUser(UserRequest userRequest) {
        try {
            loadUserByUsername(userRequest.getEmail());
        } catch (AppException e) {
            User user = User.builder()
                    .email(userRequest.getEmail())
                    .password(passwordEncoder.encode(userRequest.getPassword()))
                    .build();
            return userRepository.save(user);
        }
        throw new AppException("User already exists", HttpStatus.CONFLICT);
    }














}
