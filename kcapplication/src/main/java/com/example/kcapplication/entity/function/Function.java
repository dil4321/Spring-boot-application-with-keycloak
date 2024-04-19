package com.example.kcapplication.entity.function;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "FUNCTIONR")
public class Function {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FUNCTIONR")
    @SequenceGenerator(sequenceName = "FUNCTIONR_SEQ", allocationSize = 1, name = "FUNCTIONR")
    @Column(name = "FUNCTION_ID", nullable = false)
    private Long functionId;

    @Column(name = "KC_FUNCTION_ID", nullable = true)
    private String keycloakFunctionId;

    @Column(name = "FUNCTION_NAME", nullable = false)
    private String name;

    @Column(name = "FUNCTION_DESC")
    private String description;


}
