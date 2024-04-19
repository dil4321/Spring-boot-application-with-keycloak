package com.example.kcapplication.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoleCreateDTO {
    private String roleCode;
    private String roleName;
    private String keycloakRoleId;
    private List<Long> functionIdList;
}
