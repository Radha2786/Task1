package com.example.Task1.controller;

import com.example.Task1.dto.LoginRequest;
import com.example.Task1.dto.LoginResponse;
import com.example.Task1.entity.User;
import com.example.Task1.helper.Helper;
import com.example.Task1.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RestControllerAdvice
@CrossOrigin("*")
public class UserController {
    @Autowired
    UserService userService;

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleEnumErrors(HttpMessageNotReadableException ex) {
        String msg = ex.getMostSpecificCause().getMessage();
        if (msg.contains("from String")) {
            return ResponseEntity.badRequest().body("Invalid userType. Allowed values: ADMIN, USER, EMPLOYEE");
        }
        return ResponseEntity.badRequest().body("Invalid request: " + msg);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        if (ex.getMessage().contains("already exists")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }
        if (ex.getMessage().contains("not found") || ex.getMessage().contains("Invalid password")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody User user) {
        userService.register(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = userService.login(loginRequest);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file")MultipartFile file) throws IOException {

        if(Helper.checkFileFormat(file)){
            // File is either Excel or CSV
            this.userService.save(file);
            return ResponseEntity.ok(Map.of("message", "File uploaded and saved to database successfully"));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please upload Excel (.xlsx) or CSV (.csv) file");
    }

    @GetMapping("/allUsers")
    public List<User> getAllUser(){
        return this.userService.getAllUsers();
    }

}
