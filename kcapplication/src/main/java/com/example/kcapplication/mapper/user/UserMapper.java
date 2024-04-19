package com.example.kcapplication.mapper.user;

import com.example.kcapplication.entity.user.User;
import com.example.kcapplication.request.UserCreateDTO;
import com.example.kcapplication.request.UserUpdateDTO;
import com.example.kcapplication.response.UserResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    User toEntity(UserCreateDTO dto);
    @Mapping(source = "userId", target = "id")
    UserResponseDTO toDTO(User entity);
    User toUpdateEntity(@MappingTarget User entity, UserUpdateDTO dto);
}
