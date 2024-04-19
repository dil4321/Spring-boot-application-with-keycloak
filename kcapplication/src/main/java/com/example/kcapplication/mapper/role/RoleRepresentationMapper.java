package com.example.kcapplication.mapper.role;

import com.example.kcapplication.entity.function.Function;
import com.example.kcapplication.request.RoleRepresentationDTO;
import org.keycloak.representations.idm.RoleRepresentation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoleRepresentationMapper {
    @Mapping(source = "keycloakFunctionId", target = "id")
    @Mapping(source = "clientRole", target = "clientRole")
    RoleRepresentation toEntityRepresentation(RoleRepresentationDTO dto);

    @Mapping(source = "functionId", target = "id")
    RoleRepresentationDTO toDtoFunction(Function function);

    @Mapping(source = "id", target = "keycloakFunctionId")
    @Mapping(source = "clientRole", target = "clientRole")
    RoleRepresentationDTO toDtoRepresentation(RoleRepresentation entity);
}
