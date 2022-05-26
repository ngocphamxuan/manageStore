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
public class AccountDto implements Serializable {
    @NotEmpty
    @Length(min = 6)
    private String username;
    @NotEmpty
    @Length(min = 6)
    private String password;

    private Boolean isEdit = false;

}
