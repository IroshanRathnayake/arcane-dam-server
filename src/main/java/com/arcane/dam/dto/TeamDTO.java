package com.arcane.dam.dto;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamDTO {
    private String id;
    private String name;
    private String description;
    private List<String> tags;
    private Instant createdAt;
    private Instant updatedAt;
    private Boolean isEnabled;
}
