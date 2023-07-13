package com.example.usersservice.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RequestLogin {
    @NotNull(message = "Email cannot be null")
    @Size(min=2, message="Eail not be less then two characters")
    private String email;
    @NotNull(message = "Password cannot be null")
    @Size(min=8, message="Password not be less then eight characters")
    private String password;
}
