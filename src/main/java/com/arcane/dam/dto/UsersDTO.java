package com.arcane.dam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsersDTO {
    private String id;
    private String userName;
    private String email;
    private String password;
    private String role;
    private String createdAt;
}
