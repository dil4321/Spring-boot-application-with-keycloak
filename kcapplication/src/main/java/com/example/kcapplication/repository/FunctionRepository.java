package com.example.kcapplication.repository;

import com.example.kcapplication.entity.function.Function;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FunctionRepository extends JpaRepository<Function,Long> {
    boolean existsByNameIgnoreCase(String name);
    boolean existsByFunctionId(Long id);
    Function findByKeycloakFunctionId(String id);

}
