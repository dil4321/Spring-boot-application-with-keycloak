package com.example.kcapplication.entity.role;

import com.example.kcapplication.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ROLE")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ROLE")
    @SequenceGenerator(sequenceName = "ROLE_SEQ", allocationSize = 1, name = "ROLE")
    @Column(name = "ROLE_ID", nullable = false)
    private Long roleId;

    @Column(name = "ROLE_CODE", nullable = false)
    private String roleCode;

    @Column(name = "ROLE_NAME", nullable = false)
    private String roleName;

    @Column(name = "KC_ROLE_ID", nullable = false)
    private String keycloakRoleId;

    @Column(name = "IS_DELETED")
    private Boolean isDeleted = false;

    @ManyToMany(mappedBy = "roles",fetch = FetchType.LAZY)
    private Set<User> users = new HashSet<>();
}
