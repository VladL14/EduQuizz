package com.eduquizz.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.eduquizz.backend.entities.User;
import com.eduquizz.backend.servicies.UserService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id)
    {
        try {
            User user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/createUser")
    public ResponseEntity<?> createUser(@RequestBody User user)
    {
       try {
           User createdUser = userService.createUser(user);
           return ResponseEntity.ok(createdUser);
       } catch (RuntimeException e) {
           return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
       }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id)
    {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok("User deleted successfully.");
        }
        catch (RuntimeException e)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    } 
    
}
