package com.example.kcapplication.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserCreateDTO {
    private String firstName;
    private String lastName;
    private String userName;
    private String password;
    private String email;
    private Boolean isDeleted;
    private Boolean isActive;
    private List<Long> rolesIds;
}
