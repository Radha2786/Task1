package com.example.Task1.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
    private String token;
    private String userType;
    private String message;
    
    public LoginResponse(String token, String userType, String message) {
        this.token = token;
        this.userType = userType;
        this.message = message;
    }
}
