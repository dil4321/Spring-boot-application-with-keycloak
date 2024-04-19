package com.example.kcapplication.mapper.role;

import com.example.kcapplication.entity.role.Role;
import com.example.kcapplication.request.RoleCreateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoleMapper {
    Role toEntity(RoleCreateDTO roleCreateDTO);
}
