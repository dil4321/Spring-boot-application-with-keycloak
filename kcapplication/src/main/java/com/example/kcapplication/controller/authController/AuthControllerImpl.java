package com.example.kcapplication.controller.authController;

import com.example.kcapplication.request.LoginDTO;
import com.example.kcapplication.service.authService.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.stream.Stream;

import static com.example.kcapplication.util.ResponseHandler.generateResponse;
@RestController
@RequiredArgsConstructor
public class AuthControllerImpl implements AuthController{

    private final AuthService authService;
    @Override
    public ResponseEntity<Object> login(LoginDTO loginDTO) {
        try {
            if (Stream.of(loginDTO.getUserName(), loginDTO.getPassword()).noneMatch(Objects::isNull)) {
                ResponseEntity res = authService.login(loginDTO);
                if (res.getStatusCode().isSameCodeAs(HttpStatus.OK)) {
                    return generateResponse("Login Success", HttpStatus.OK, res.getBody());
                } else {
                    return generateResponse((String) res.getBody(), (HttpStatus) res.getStatusCode(), null);
                }
            } else {
                return generateResponse("Credential/s Missing", HttpStatus.NOT_ACCEPTABLE, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return generateResponse(e.getMessage(), HttpStatus.BAD_REQUEST, null);
        }
    }

    @Override
    public ResponseEntity<Object> logout(Long userId) {
        try {
                ResponseEntity res = authService.logout(userId);
                if (res.getStatusCode().isSameCodeAs(HttpStatus.OK)) {
                    return generateResponse("Logout Success", HttpStatus.OK, res.getBody());
                } else {
                    return generateResponse((String) res.getBody(), (HttpStatus) res.getStatusCode(), null);
                }
        } catch (Exception e) {
            e.printStackTrace();
            return generateResponse(e.getMessage(), HttpStatus.BAD_REQUEST, null);
        }
    }
}
