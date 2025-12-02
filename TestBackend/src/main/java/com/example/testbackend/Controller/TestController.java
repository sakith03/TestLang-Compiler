package com.example.testbackend.Controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class TestController {

    private Map<Integer, Map<String, Object>> users = new HashMap<>();

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        Map<String, Object> response = new HashMap<>();
        response.put("token", "abc123xyz");
        response.put("username", credentials.get("username"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return ResponseEntity.ok().headers(headers).body(response);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable int id) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", id);
        response.put("username", "admin");
        response.put("role", "USER");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-App", "TestLangDemo");

        return ResponseEntity.ok().headers(headers).body(response);
    }

    @PostMapping("/users")
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody Map<String, Object> newUser) {
        int newId = users.size() + 1;
        newUser.put("id", newId);
        users.put(newId, newUser);

        Map<String, Object> response = new HashMap<>();
        response.put("id", newId);
        response.put("name", newUser.get("name"));
        response.put("role", newUser.get("role"));
        response.put("created", true);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable int id,
            @RequestBody Map<String, String> updates) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", id);
        response.put("updated", true);
        response.put("role", updates.get("role"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-App", "TestLangDemo");

        return ResponseEntity.ok().headers(headers).body(response);
    }
}