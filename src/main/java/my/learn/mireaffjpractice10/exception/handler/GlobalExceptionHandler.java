package my.learn.mireaffjpractice10.exception.handler;

import my.learn.mireaffjpractice10.exception.AppException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<Object> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleAppException(AppException e) {
        return new ResponseEntity<>(e.getMessage(), e.getStatus());
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleException(AuthenticationException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleValidateException(BindException e) {
        Map<String, String> body = new HashMap<>();
        e.getBindingResult().getFieldErrors().stream()
                .map(err -> body.put(err.getField(), err.getDefaultMessage())
        );
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }


}
