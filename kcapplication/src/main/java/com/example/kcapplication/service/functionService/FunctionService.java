package com.example.kcapplication.service.functionService;

import com.example.kcapplication.request.FunctionCreateDTO;
import com.example.kcapplication.request.RoleCreateDTO;
import com.example.kcapplication.request.UserCreateDTO;
import org.springframework.http.ResponseEntity;

public interface FunctionService {
    ResponseEntity<Object> createFunction(FunctionCreateDTO functionCreateDTO);
    ResponseEntity<Object> updateFunction(FunctionCreateDTO functionCreateDTO);
    ResponseEntity<Object> getAllFunctions();
    ResponseEntity<Object> deleteFunctions(Long funcId);
}
