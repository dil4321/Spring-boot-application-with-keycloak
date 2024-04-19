package com.example.kcapplication.controller.userController;

import com.example.kcapplication.request.UserCreateDTO;
import com.example.kcapplication.request.UserUpdateDTO;
import com.example.kcapplication.service.userService.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.annotation.RestController;

import static com.example.kcapplication.util.ResponseHandler.generateResponse;

@RestController
@RequiredArgsConstructor
public class UserControllerImpl implements UserController{

    private final UserService userService;
    @Override
    @PreAuthorize("hasRole('getAllUsers')")
    public ResponseEntity<Object> createUser(UserCreateDTO userCreateDTO) {
        try {
            ResponseEntity res = userService.createUser(userCreateDTO);
            if (res.getStatusCode().isSameCodeAs(HttpStatus.CREATED)) {
                return generateResponse("User Create Success", HttpStatus.CREATED, res.getBody());
            } else {
                return generateResponse((String) res.getBody(), (HttpStatus) res.getStatusCode(), null);
            }
        }catch (Exception e) {
            return generateResponse(e.getMessage(), HttpStatus.BAD_REQUEST, null);
        }
    }

    @Override
    @PreAuthorize("hasRole('getAllUsers')")
    public ResponseEntity<Object> updateUser(UserUpdateDTO user) {
        try {
            ResponseEntity res = userService.updateUser(user);
            if (res.getStatusCode().isSameCodeAs(HttpStatus.OK)) {
                return generateResponse("User Update Success", HttpStatus.OK, res.getBody());
            } else {
                return generateResponse((String) res.getBody(), (HttpStatus) res.getStatusCode(), null);
            }
        }catch (Exception e) {
            return generateResponse(e.getMessage(), HttpStatus.BAD_REQUEST, null);
        }
    }

    @Override
    @PreAuthorize("hasRole('getAllUsers')")
    public ResponseEntity<Object> deleteUser(Long id) {
        try {
            ResponseEntity res = userService.deleteUser(id);
            if (res.getStatusCode().isSameCodeAs(HttpStatus.OK)) {
                return generateResponse("User Delete Success", HttpStatus.OK, res.getBody());
            } else {
                return generateResponse((String) res.getBody(), (HttpStatus) res.getStatusCode(), null);
            }
        }catch (Exception e) {
            return generateResponse(e.getMessage(), HttpStatus.BAD_REQUEST, null);
        }
    }

    @Override
    @PreAuthorize("hasRole('getAllUsers')")
    public ResponseEntity<Object> getAllUsers() {
        try {
            ResponseEntity res = userService.getAllUsers();
            if (res.getStatusCode().isSameCodeAs(HttpStatus.OK)) {
                return generateResponse("User Get Success", HttpStatus.OK, res.getBody());
            } else {
                return generateResponse((String) res.getBody(), (HttpStatus) res.getStatusCode(), null);
            }
        }catch (AuthenticationCredentialsNotFoundException e){
            return generateResponse(e.getMessage(), HttpStatus.UNAUTHORIZED, null);
        }catch (Exception e) {
            return generateResponse(e.getMessage(), HttpStatus.BAD_REQUEST, null);
        }
    }
}
