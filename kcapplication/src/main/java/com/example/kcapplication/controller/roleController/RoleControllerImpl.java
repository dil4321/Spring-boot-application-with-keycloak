package com.example.kcapplication.controller.roleController;

import com.example.kcapplication.request.RoleCreateDTO;
import com.example.kcapplication.request.RoleUpdateDTO;
import com.example.kcapplication.service.roleService.RoleService;
import com.example.kcapplication.service.userService.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import static com.example.kcapplication.util.ResponseHandler.generateResponse;

@RestController
@RequiredArgsConstructor
public class RoleControllerImpl implements RoleController {

    private final RoleService roleService;

    @Override
    @PreAuthorize("hasRole('getAllUsers')")
    public ResponseEntity<Object> createRole(RoleCreateDTO roleCreateDTO) {
        try {
            ResponseEntity res = roleService.createRole(roleCreateDTO);
            if (res.getStatusCode().isSameCodeAs(HttpStatus.CREATED)) {
                return generateResponse("Role Create Success", HttpStatus.CREATED, res.getBody());
            } else {
                return generateResponse((String) res.getBody(), (HttpStatus) res.getStatusCode(), null);
            }
        }catch (Exception e) {
            return generateResponse(e.getMessage(), HttpStatus.BAD_REQUEST, null);
        }
    }

    @Override
    @PreAuthorize("hasRole('getAllUsers')")
    public ResponseEntity<Object> updateRole(RoleUpdateDTO roleUpdateDTO) {
        try {
            ResponseEntity res = roleService.updateRole(roleUpdateDTO);
            if (res.getStatusCode().isSameCodeAs(HttpStatus.OK)) {
                return generateResponse("Role Update Success", HttpStatus.OK, res.getBody());
            } else {
                return generateResponse((String) res.getBody(), (HttpStatus) res.getStatusCode(), null);
            }
        }catch (Exception e) {
            return generateResponse(e.getMessage(), HttpStatus.BAD_REQUEST, null);
        }
    }

    @Override
    @PreAuthorize("hasRole('getAllUsers')")
    public ResponseEntity<Object> deleteRole(Long id) {
        try {
            ResponseEntity res = roleService.deleteRole(id);
            if (res.getStatusCode().isSameCodeAs(HttpStatus.OK)) {
                return generateResponse("Role Delete Success", HttpStatus.OK, res.getBody());
            } else {
                return generateResponse((String) res.getBody(), (HttpStatus) res.getStatusCode(), null);
            }
        }catch (Exception e) {
            return generateResponse(e.getMessage(), HttpStatus.BAD_REQUEST, null);
        }
    }

    @Override
    @PreAuthorize("hasRole('getAllRoles')")
    public ResponseEntity<Object> getAllRoles() {
        try {
            ResponseEntity res = roleService.getAllRoles();
            if (res.getStatusCode().isSameCodeAs(HttpStatus.OK)) {
                return generateResponse("Role Get Success", HttpStatus.OK, res.getBody());
            } else {
                return generateResponse((String) res.getBody(), (HttpStatus) res.getStatusCode(), null);
            }
        }catch (Exception e) {
            return generateResponse(e.getMessage(), HttpStatus.BAD_REQUEST, null);
        }
    }
}
