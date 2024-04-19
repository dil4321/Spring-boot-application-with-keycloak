package com.example.kcapplication.service.roleService;

import com.example.kcapplication.entity.role.Role;
import com.example.kcapplication.request.RoleCreateDTO;
import com.example.kcapplication.request.RoleUpdateDTO;
import org.springframework.http.ResponseEntity;

public interface RoleService {
    ResponseEntity<Object> createRole(RoleCreateDTO role);
    ResponseEntity<Object> updateRole(RoleUpdateDTO role);
    ResponseEntity<Object> getAllRoles();
    ResponseEntity<Object> deleteRole(Long roleId);
}
