package com.example.kcapplication.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FunctionCreateDTO {
    private String functionName;
    private String description;
}
