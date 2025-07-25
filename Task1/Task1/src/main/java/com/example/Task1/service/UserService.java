package com.example.Task1.service;

import com.example.Task1.dto.LoginRequest;
import com.example.Task1.dto.LoginResponse;
import com.example.Task1.entity.User;
import com.example.Task1.helper.Helper;
import com.example.Task1.repository.UserRepository;
import com.example.Task1.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;

    @Transactional
    public User register(User user){
        try{
            if(userRepository.existsByEmail(user.getEmail())){
                throw new RuntimeException("User with email " + user.getEmail() + " already exists");
            }
            // Encrypt password before saving
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return userRepository.save(user);

        }catch(Exception e){
            throw new RuntimeException("Error saving user: " + e.getMessage());
        }
    }
    
    public LoginResponse login(LoginRequest loginRequest) {
        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());
        
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found with email: " + loginRequest.getEmail());
        }
        
        User user = userOptional.get();
        
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        
        String token = jwtUtil.generateToken(user.getEmail(), user.getUserType());
        
        return new LoginResponse(token, user.getUserType().toString(), "Login successful");
    }

    // converts data from excel file to list of users and will save
    @Transactional
    public void save(MultipartFile file) throws IOException {
        // System.out.println("file name is " + file.getOriginalFilename()); 
        
        List<User> usersList;
        
        if (Helper.checkExcelFormat(file)) {
            usersList = Helper.convertExcelToListOfUser(file.getInputStream());
        } else if (Helper.checkCSVFormat(file)) {
            usersList = Helper.convertCSVToListOfUser(file.getInputStream());
        } else {
            throw new IllegalArgumentException("Unsupported file format. Please upload Excel or CSV file.");
        }
        
        this.userRepository.saveAll(usersList);
    }

    // returns all list of users as json
    public List<User> getAllUsers(){
        return this.userRepository.findAll();
    }
}
