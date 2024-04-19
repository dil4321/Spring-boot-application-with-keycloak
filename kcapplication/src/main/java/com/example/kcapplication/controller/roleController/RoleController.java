package com.example.kcapplication.controller.roleController;

import com.example.kcapplication.request.RoleCreateDTO;
import com.example.kcapplication.request.RoleUpdateDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/v1/role")
public interface RoleController {
    @PostMapping("/create")
    ResponseEntity<Object> createRole(@RequestBody RoleCreateDTO roleCreateDTO);

    @PutMapping("/update")
    ResponseEntity<Object> updateRole(@RequestBody RoleUpdateDTO roleUpdateDTO);

    @DeleteMapping("/{id}")
    ResponseEntity<Object> deleteRole(@PathVariable("id") Long id);

    @GetMapping("/all")
    ResponseEntity<Object> getAllRoles();
}
