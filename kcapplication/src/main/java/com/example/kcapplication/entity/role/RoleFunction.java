package com.example.kcapplication.entity.role;

import com.example.kcapplication.entity.function.Function;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ROLE_FUNCTION")
public class RoleFunction {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ROLE_FUNCTION")
    @SequenceGenerator(sequenceName = "ROLE_FUNCTION_SEQ", allocationSize = 1, name = "ROLE_FUNCTION")
    @Column(name = "ROLE_FUNCTION_ID", nullable = false)
    private Long roleFunctionId;

    @JoinColumn(name = "ROLE_ID", referencedColumnName = "ROLE_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @Getter(AccessLevel.NONE)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Role role;

    @JoinColumn(name = "FUNCTION_ID", referencedColumnName = "FUNCTION_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @Getter(AccessLevel.NONE)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Function function;

    @Column(name = "KC_ROLE_ID", nullable = false)
    private String keycloakRoleId;
    @Column(name = "KC_FUNCTION_ID", nullable = false)
    private String keycloakFunctionId;

    @Column(name = "IS_ACTIVE", nullable = false)
    private Boolean isActive;


    @JsonIgnore
    public Role getRole() {
        return this.role;
    }

    @JsonIgnore
    public Function getFunction() {
        return this.function;
    }
}
