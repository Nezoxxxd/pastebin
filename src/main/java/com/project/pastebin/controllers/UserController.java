package com.project.pastebin.controllers;

import com.project.pastebin.entities.User;
import com.project.pastebin.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @DeleteMapping("/delete/{email}")
    public String deleteUser(@PathVariable String email) {
        userService.deleteUserByEmail(email);
        return "User deleted successfully";
    }

    @PutMapping("/update/{email}")
    public User  updateUser(@PathVariable String email, @RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
       return userService.updateUserByEmail(email, user);
    }

    @GetMapping("/{email}")
    public User getUser(@PathVariable String email) {
        return (User) userService.loadUserByUsername(email);
    }
}
