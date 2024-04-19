package com.example.kcapplication.controller.userController;

import com.example.kcapplication.request.UserCreateDTO;
import com.example.kcapplication.request.UserUpdateDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/user")
public interface UserController {
    @PostMapping("/createUser")
    ResponseEntity<Object> createUser(@RequestBody UserCreateDTO userCreateDTO);
    @PutMapping("/updateUser")
    ResponseEntity<Object> updateUser(@RequestBody UserUpdateDTO user);
    @DeleteMapping("/deleteUser")
    ResponseEntity<Object> deleteUser(@RequestParam Long id);
    @GetMapping("/getAllUsers")
    ResponseEntity<Object> getAllUsers();
}
