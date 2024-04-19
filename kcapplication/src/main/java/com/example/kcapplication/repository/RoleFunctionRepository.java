package com.example.kcapplication.repository;

import com.example.kcapplication.entity.role.Role;
import com.example.kcapplication.entity.role.RoleFunction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleFunctionRepository extends JpaRepository<RoleFunction,Long> {
    List<RoleFunction> findRoleFunctionByRole(Role role);
    RoleFunction findByKeycloakRoleIdAndKeycloakFunctionId(String kcRoleId, String kcFunctionId);
    RoleFunction findByRoleRoleIdAndFunctionFunctionId(Long roleId, Long functionId);
    RoleFunction findByKeycloakFunctionId(String kcFunctionId);
    List<RoleFunction> findByRole(Role role);
    Boolean existsRoleFunctionsByRoleRoleId(Long roleId);
}
