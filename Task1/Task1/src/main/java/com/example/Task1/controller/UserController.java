package com.example.Task1.controller;

import com.example.Task1.entity.User;
import com.example.Task1.helper.Helper;
import com.example.Task1.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
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
            return ResponseEntity.badRequest().body("Invalid userType. Allowed values: ADMIN, CUSTOMER, GUEST");
        }
        return ResponseEntity.badRequest().body("Invalid request: " + msg);
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody User user) {
        userService.register(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully!");
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file")MultipartFile file) throws IOException {

        if(Helper.checkExcelFormat(file)){
            //true
            this.userService.save(file);
            return ResponseEntity.ok(Map.of("message", "file uploaded and saved to db successfully"));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("please upload excel file");
    }

    @GetMapping("/allUsers")
    public List<User> getAllUser(){
        return this.userService.getAllUsers();
    }

}
