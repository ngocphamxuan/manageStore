package com.example.version4.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminLoginDto implements Serializable {
    @NotEmpty

    private String username;
    @NotEmpty

    private String password;

    private Boolean rememberMe = false;

}
