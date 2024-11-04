package com.arcane.dam.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.arcane.dam.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final DynamoDBMapper dynamoDBMapper;

    public Users save(Users user) {
        dynamoDBMapper.save(user);
        return user;
    }

    public List<Users> getAllUsers() {
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        return dynamoDBMapper.scan(Users.class, scanExpression);
    }

    public Users getUsersById(String id) {
        return dynamoDBMapper.load(Users.class, id);
    }

    public boolean delete(String id) {
        Users emp = dynamoDBMapper.load(Users.class, id);
        dynamoDBMapper.delete(emp);
        return true;
    }

    public boolean update(String id, Users user) {
        dynamoDBMapper.save(user,
                new DynamoDBSaveExpression()
                        .withExpectedEntry("id",
                                new ExpectedAttributeValue(
                                        new AttributeValue().withS(id)
                                )));

        return true;
    }

    public Users findUserByEmail(String email) {
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("email = :email")
                .withExpressionAttributeValues(Map.of(":email", new AttributeValue(email)));

        List<Users> result = dynamoDBMapper.scan(Users.class, scanExpression);

        return result.isEmpty() ? null : result.get(0);
    }

    public Users getUserByUserName(String userName) {
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("userName = :userName")
                .withExpressionAttributeValues(Map.of(":userName", new AttributeValue(userName)));

        List<Users> result = dynamoDBMapper.scan(Users.class, scanExpression);

        return result.isEmpty() ? null : result.get(0);
    }
}
