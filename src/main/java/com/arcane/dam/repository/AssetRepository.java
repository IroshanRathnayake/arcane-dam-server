package com.arcane.dam.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.arcane.dam.entity.AssetMetaData;
import com.arcane.dam.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AssetRepository {
    private final DynamoDBMapper dynamoDBMapper;

    public void save(AssetMetaData metaData) {
        dynamoDBMapper.save(metaData);
    }

    public Optional<AssetMetaData> findById(String fileId) {
        AssetMetaData assetMetaData = dynamoDBMapper.load(AssetMetaData.class, fileId);
        return Optional.ofNullable(assetMetaData);
    }

    public boolean update(String id, AssetMetaData metaData) {
        dynamoDBMapper.save(metaData,
                new DynamoDBSaveExpression()
                        .withExpectedEntry("asset_id",
                                new ExpectedAttributeValue(
                                        new AttributeValue().withS(id)
                                )));

        return true;
    }

    public boolean delete(AssetMetaData assetMetaData) {
        dynamoDBMapper.delete(assetMetaData);
        return true;
    }
    public boolean delete(String id) {
        AssetMetaData metaData = dynamoDBMapper.load(AssetMetaData.class, id);
        dynamoDBMapper.delete(metaData);
        return true;
    }
}
