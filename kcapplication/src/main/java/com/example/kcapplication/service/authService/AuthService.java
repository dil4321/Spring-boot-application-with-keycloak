package com.example.kcapplication.service.authService;

import com.example.kcapplication.request.LoginDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface AuthService {
    ResponseEntity<Object> login(LoginDTO loginDTO);
    ResponseEntity<Object> logout(Long userId);
}
