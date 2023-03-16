package com.example.rest.service;

import com.example.rest.exception.InvalidRequestException;
import com.example.rest.exception.UserNotFoundException;
import com.example.rest.model.User;
import com.example.rest.repository.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UsersService {

    @Autowired
    private UsersRepository usersRepository;

    public User save(User user) throws InvalidRequestException {
        if (getByEmail(user.getEmail()) != null) {
            throw new InvalidRequestException("User with email " + user.getEmail() + " already exists");
        } else {
            user.setPassword(passwordEncoder(user.getPassword()));
            return usersRepository.save(user);
        }
    }

    public User update(User user) throws InvalidRequestException {

        if (user.getPassword() != null) {
            user.setPassword(passwordEncoder(user.getPassword()));
        }
        return usersRepository.save(user);
    }

    public List<User> getAll() {
        return usersRepository.findAll();
    }

    public User getById(Long id) throws UserNotFoundException {
        Optional<User> optionalUser = usersRepository.findById(id);
        if (!optionalUser.isPresent()) {
            throw new UserNotFoundException("User doesn't exist");
        }
        return optionalUser.get();
    }


    public User getByEmail(String email) {
        return usersRepository.findUserByEmail(email);
    }

    public Boolean delete(Long id) throws UserNotFoundException {
        Optional<User> optionalUser = usersRepository.findById(id);
        if (!optionalUser.isPresent()) {
            throw new UserNotFoundException("User doesn't exist");
        }
        usersRepository.deleteById(id);
        return true;
    }

    public String passwordEncoder(String password) {
        int strength = 10; // work factor of bcrypt
        BCryptPasswordEncoder bCryptPasswordEncoder =
                new BCryptPasswordEncoder(strength, new SecureRandom());
        String encodedPassword = bCryptPasswordEncoder.encode(password);
        return encodedPassword;
    }
}