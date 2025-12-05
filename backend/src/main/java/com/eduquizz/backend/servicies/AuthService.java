package com.eduquizz.backend.servicies;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.eduquizz.backend.entities.User;
import com.eduquizz.backend.repositories.UserRepository;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User login(String email, String password)
    {
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isPresent())
        {
            User foundUser = user.get();
            if(passwordEncoder.matches(password, foundUser.getPassword()))
            {
                return foundUser;
            }
            else
            {
                throw new RuntimeException("Invalid password.");
            }
        }
        else
        {
            throw new RuntimeException("User not found with email: " + email);
        }

    } 
}
