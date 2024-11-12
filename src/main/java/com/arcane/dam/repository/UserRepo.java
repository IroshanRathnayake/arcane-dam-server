package com.arcane.dam.repository;

import com.arcane.dam.entity.Users;
import org.socialsignin.spring.data.dynamodb.repository.DynamoDBCrudRepository;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@EnableScan
public interface UserRepo extends CrudRepository<Users, String> {
    Optional<Users> findByEmail(String email);
    Optional<Users> findByUserName(String userName);
}
