package com.example.kcapplication.repository;

import com.example.kcapplication.entity.role.Role;
import com.example.kcapplication.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    List<User> getUsersByIsDeletedFalse();
    Boolean existsByUserNameAndIsDeletedFalse(String userName);
    Boolean existsByUserNameAndUserIdNotAndIsDeletedFalse(String userName, Long id);
    Boolean existsByUserIdAndIsDeletedFalse(Long userId);
    Boolean existsByUserIdAndRolesRoleId(Long userId,Long roleId);
    Optional<User> getUserByUserNameAndIsDeletedFalse(String userName);
    User getUserByUserName(String userName);
    Optional<User> getUserByUserIdAndIsDeletedFalse(Long id);
}
