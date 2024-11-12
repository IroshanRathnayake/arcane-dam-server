package com.arcane.dam.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.arcane.dam.entity.Team;
import com.arcane.dam.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TeamRepository {

    private final DynamoDBMapper dynamoDBMapper;

    public Team save(Team team) {
        dynamoDBMapper.save(team);
        return team;
    }

    public List<Team> findAll() {
        return dynamoDBMapper.scan(Team.class, new DynamoDBScanExpression());
    }

    public boolean delete(String id) {
        Team team = dynamoDBMapper.load(Team.class, id);
        if (team != null) {
            dynamoDBMapper.delete(team);
            return true;
        }
        return false;
    }

    public boolean update(Team team) {
        dynamoDBMapper.save(team,
                new DynamoDBSaveExpression()
                        .withExpectedEntry("id",
                                new ExpectedAttributeValue(
                                        new AttributeValue().withS(team.getId())
                                )));

        return true;
    }
}
