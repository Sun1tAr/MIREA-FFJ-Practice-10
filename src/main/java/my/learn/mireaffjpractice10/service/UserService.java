package my.learn.mireaffjpractice10.service;

import lombok.RequiredArgsConstructor;
import my.learn.mireaffjpractice10.dto.request.UserRegisterRequest;
import my.learn.mireaffjpractice10.exception.AppException;
import my.learn.mireaffjpractice10.model.User;
import my.learn.mireaffjpractice10.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
    }

    public boolean userIsExists(String email) {
        try {
            findUserByEmail(email);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findUserByEmail(username);
    }

    public Long getCountOfUsers() {
        return userRepository.count();
    }

    public User findMe() {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return findUserByEmail(principal.getUsername());
    }

    @PreAuthorize("hasRole('ADMIN') or principal.id == #id")
    public User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
    }



}
