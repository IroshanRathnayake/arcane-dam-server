package com.arcane.dam.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.arcane.dam.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class AuthRepository {
    private final DynamoDBMapper dynamoDBMapper;

    public Users findUserByEmail(String email) {
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("email = :email")
                .withExpressionAttributeValues(Map.of(":email", new AttributeValue(email)));

        List<Users> result = dynamoDBMapper.scan(Users.class, scanExpression);

        return result.isEmpty() ? null : result.get(0);
    }

}
