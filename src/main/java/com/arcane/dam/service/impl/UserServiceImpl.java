package com.arcane.dam.service.impl;

import com.arcane.dam.dto.JwtResponseDTO;
import com.arcane.dam.dto.UsersDTO;
import com.arcane.dam.entity.Users;
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
import java.time.LocalDateTime;
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
    public JwtResponseDTO verify(UsersDTO usersDTO) {
        Authentication authentication =
                authManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                usersDTO.getEmail(),
                                usersDTO.getPassword()
                        )
                );

        if (authentication.isAuthenticated()) {
            String token = jwtService.generateToken(usersDTO.getEmail());
            UsersDTO  user = getUserByEmail(usersDTO.getEmail());
            return new JwtResponseDTO(token, user);
        }
        throw new RuntimeException("Invalid access");
    }
}
