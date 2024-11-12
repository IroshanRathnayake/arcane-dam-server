package com.arcane.dam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamDTO {
    private String id;
    private String name;
    private Instant createdAt;
    private Instant updatedAt;
    private Boolean isEnabled;
}
