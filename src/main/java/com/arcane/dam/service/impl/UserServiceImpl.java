package com.arcane.dam.service.impl;

import com.arcane.dam.dto.AuthRequest;
import com.arcane.dam.dto.AuthResponseDTO;
import com.arcane.dam.dto.UsersDTO;
import com.arcane.dam.entity.Space;
import com.arcane.dam.entity.Users;
import com.arcane.dam.exception.IllegalStateException;
import com.arcane.dam.repository.UserRepository;
import com.arcane.dam.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper mapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManager authManager;
    private final JWTService jwtService;

    @Override
    public List<UsersDTO> getAllUsers() {
        return userRepository.getAllUsers()
                .stream()
                .map(user -> mapper.map(user, UsersDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public UsersDTO getUserById(String id) {
        return mapper.map(userRepository.getUsersById(id), UsersDTO.class);
    }

    @Override
    public UsersDTO getUserByEmail(String email) {
        return mapper.map(userRepository.findUserByEmail(email), UsersDTO.class);
    }

    @Override
    public UsersDTO addUser(UsersDTO usersDTO) {
        usersDTO.setPassword(bCryptPasswordEncoder.encode(usersDTO.getPassword()));
        usersDTO.setCreatedAt(Instant.now());
        usersDTO.setUpdatedAt(Instant.now());
        usersDTO.setRole("user");
        usersDTO.setIsEnabled(true);
        Users user = userRepository.save(mapper.map(usersDTO, Users.class));
        return mapper.map(user, UsersDTO.class);
    }

    @Override
    public boolean updateUser(String id, UsersDTO usersDTO) {
        usersDTO.setPassword(bCryptPasswordEncoder.encode(usersDTO.getPassword()));
        usersDTO.setUpdatedAt(Instant.now());
        return userRepository.update(id, mapper.map(usersDTO, Users.class));
    }

    @Override
    public boolean deleteUser(String id) {
        return userRepository.delete(id);
    }

    @Override
    public AuthResponseDTO verify(AuthRequest authRequest) {
        Authentication authentication =
                authManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                authRequest.getEmail(),
                                authRequest.getPassword()
                        )
                );

        if (authentication.isAuthenticated()) {
            String token = jwtService.generateToken(authRequest.getEmail());
            UsersDTO  user = getUserByEmail(authRequest.getEmail());
            return new AuthResponseDTO(token, user);
        }
        throw new IllegalStateException("Invalid access");
    }

    @Override
    public UsersDTO updateSpace(String userId, Space updatedSpace) {
        Users user = userRepository.updateSpace(userId, updatedSpace);
        return mapper.map(user, UsersDTO.class);
    }
}
