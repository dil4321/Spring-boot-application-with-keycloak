package com.example.kcapplication.service.functionService;

import com.example.kcapplication.entity.function.Function;
import com.example.kcapplication.entity.role.Role;
import com.example.kcapplication.entity.user.User;
import com.example.kcapplication.repository.FunctionRepository;
import com.example.kcapplication.request.FunctionCreateDTO;
import com.example.kcapplication.request.RoleCreateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class FunctionServiceImpl implements FunctionService {

    private final FunctionRepository functionRepository;

    @Override
    public ResponseEntity<Object> createFunction(FunctionCreateDTO functionCreateDTO) {
        if (functionRepository.existsByNameIgnoreCase(functionCreateDTO.getFunctionName())) return new ResponseEntity<>("Function Name Already Exist", HttpStatus.CONFLICT);
        Function function = new Function();
        function.setName(functionCreateDTO.getFunctionName());
        function.setDescription(functionCreateDTO.getDescription());
        Function savedFunction = functionRepository.save(function);

        return new ResponseEntity<>(savedFunction, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Object> updateFunction(FunctionCreateDTO functionCreateDTO) {
        return null;
    }

    @Override
    public ResponseEntity<Object> getAllFunctions() {
        return null;
    }

    @Override
    public ResponseEntity<Object> deleteFunctions(Long roleId) {
        return null;
    }
}
