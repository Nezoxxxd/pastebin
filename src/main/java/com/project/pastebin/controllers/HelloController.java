package com.project.pastebin.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class HelloController {

    @GetMapping("/home")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("Welcome to Pastebin");
    }

    @GetMapping("/user/home")
    public ResponseEntity<String> userHome() {
        return ResponseEntity.ok("This is user home page");
    }

    @GetMapping("/admin/home")
    public ResponseEntity<String> adminHome() {
        return ResponseEntity.ok("This is admin home page");
    }
}
