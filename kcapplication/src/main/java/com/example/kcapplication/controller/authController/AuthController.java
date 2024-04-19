package com.example.kcapplication.controller.authController;

import com.example.kcapplication.request.LoginDTO;
import com.example.kcapplication.request.UserCreateDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/v1/auth")
public interface AuthController {
    @PostMapping("/login")
    ResponseEntity<Object> login(@RequestBody LoginDTO loginDTO);
    @PostMapping("/logout/{userId}")
    ResponseEntity<Object> logout(@PathVariable Long userId);
}
