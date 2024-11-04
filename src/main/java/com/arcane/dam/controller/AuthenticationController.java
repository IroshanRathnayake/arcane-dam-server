package com.arcane.dam.controller;

import com.arcane.dam.dto.UsersDTO;
import com.arcane.dam.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserService userService;

    @GetMapping("/test")
    public ResponseEntity<String> login() {
        return new ResponseEntity<>("Welcome", HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UsersDTO user) {
        return new ResponseEntity<>(userService.verify(user), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<UsersDTO> register(@RequestBody UsersDTO user) {
        UsersDTO savedUser = userService.addUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }
}
