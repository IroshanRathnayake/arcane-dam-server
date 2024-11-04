package com.arcane.dam.service;

import com.arcane.dam.dto.UsersDTO;

import java.util.List;

public interface UserService {
    List<UsersDTO> getAllUsers();
    UsersDTO getUserById(String id);
    UsersDTO getUserByEmail(String email);
    UsersDTO addUser(UsersDTO usersDTO);
    boolean updateUser(String id, UsersDTO usersDTO);
    boolean deleteUser(String id);
    String verify(UsersDTO usersDTO);
}
