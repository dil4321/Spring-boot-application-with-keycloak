package com.example.kcapplication.repository;

import com.example.kcapplication.entity.role.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role,Long> {
    List<Role> getRolesByIsDeletedFalse();
    Role getRoleByRoleIdAndIsDeletedFalse(Long id);
    List<Role> findRolesByUsersUserId(Long id);
    Role getRoleByKeycloakRoleIdAndIsDeletedFalse(String id);
    Boolean existsRoleByRoleCodeAndIsDeletedFalse(String code);
    Boolean existsRoleByRoleIdAndIsDeletedIsFalse(Long code);
    Boolean existsByRoleNameAndIsDeletedFalse(String name);
    Boolean existsByRoleCodeAndIsDeletedFalseAndRoleIdNot(String code, Long id);
    Boolean existsByRoleNameAndIsDeletedFalseAndRoleIdNot(String name, Long id);
    Boolean existsRolesByUsersUserId(Long id);
    @Transactional
    void deleteByRoleIdAndUsersUserId(Long roleId, Long userId);
}
