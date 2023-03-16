package com.example.rest.controller;

import com.example.rest.exception.InvalidRequestException;
import com.example.rest.exception.UserNotFoundException;
import com.example.rest.model.User;
import com.example.rest.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UsersController {
    @Autowired
    private UsersService usersService;

    @GetMapping
    public ResponseEntity<?> getAll() {
        List<User> users = usersService.getAll();
        return ResponseEntity.ok().body(users);
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody User user) throws InvalidRequestException {
        User savedUser = usersService.save(user);
        return ResponseEntity.ok().body(savedUser);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getById(@PathVariable Long userId) throws UserNotFoundException {
        User user = usersService.getById(userId);
        return ResponseEntity.ok().body(user);
    }

    @PutMapping("/")
    public ResponseEntity<?> update(@RequestBody User user) throws InvalidRequestException {
        User savedUser = usersService.update(user);
        return ResponseEntity.ok().body(savedUser);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> delete(@PathVariable Long userId) throws UserNotFoundException {
        Boolean res = usersService.delete(userId);
        return ResponseEntity.ok().body(res);
    }

}