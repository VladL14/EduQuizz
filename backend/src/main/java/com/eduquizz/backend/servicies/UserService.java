package com.eduquizz.backend.servicies;

import java.util.Optional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.eduquizz.backend.repositories.ClassroomRepository;
import com.eduquizz.backend.repositories.UserRepository;
import com.eduquizz.backend.utils.RequestRole;

import jakarta.transaction.Transactional;

import com.eduquizz.backend.entities.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.eduquizz.backend.entities.Classroom;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ClassroomRepository classroomRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, ClassroomRepository classroomRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.classroomRepository = classroomRepository;
    }

    public User getUserById(Long userId)
    {
        return userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }

   
    public User createUser(User user)
    {
        String username = user.getUsername();
        if(userRepository.findByUsername(username).isPresent())
        {
            throw new RuntimeException("Username already exists: " + username); 
        }

        String email = user.getEmail();
        if(userRepository.findByEmail(email).isPresent())
        {
            throw new RuntimeException("Email already exists: " + email);
        }

        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        user.setId(null);
        user.setRole(RequestRole.STUDENT); // MOMENTAN 
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long userId)
    {
        Optional<User> existingUser = userRepository.findById(userId);
        if(existingUser.isEmpty())
        {
            throw new RuntimeException("User not found with id: " + userId);
        }
        
        userRepository.deleteById(userId);
    }
}
