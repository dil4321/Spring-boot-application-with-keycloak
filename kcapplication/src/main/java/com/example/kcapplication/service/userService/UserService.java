package com.example.kcapplication.service.userService;

import com.example.kcapplication.request.UserCreateDTO;
import com.example.kcapplication.request.UserUpdateDTO;
import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<Object> createUser(UserCreateDTO userCreateDTO);
    ResponseEntity<Object> updateUser(UserUpdateDTO user);
    ResponseEntity<Object> deleteUser(Long id);
    ResponseEntity<Object> getAllUsers();

}
