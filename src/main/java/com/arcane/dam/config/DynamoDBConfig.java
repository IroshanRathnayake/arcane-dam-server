package com.arcane.dam.config;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import lombok.RequiredArgsConstructor;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DynamoDBConfig{

    private final AWSConfig awsConfig;

    @Bean
    public DynamoDBMapper dynamoDBMapper() {
        return new DynamoDBMapper(awsConfig.buildAmazonDynamoDB());
    }
}
