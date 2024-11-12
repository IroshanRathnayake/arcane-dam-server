package com.arcane.dam.dto;

import com.arcane.dam.entity.Space;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsersDTO {
    private String id;
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String role;
    private List<Space> spaces;
    private Instant createdAt;
    private Instant updatedAt;
    private Boolean isEnabled;
}
