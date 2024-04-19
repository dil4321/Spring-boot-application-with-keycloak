package com.example.kcapplication.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoleRepresentationDTO {
    private String id;
    private String keycloakFunctionId;
    private String name;
    private String description;
    private Boolean composite;
    private Boolean clientRole;
    private String containerId;
    private Boolean isDeleted;
    private Boolean isActive;
}