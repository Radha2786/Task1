package com.example.Task1.service;

import com.example.Task1.entity.User;
import com.example.Task1.helper.Helper;
import com.example.Task1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User register(User user){
        if(userRepository.existsByEmail(user.getEmail())){
            System.out.println("user with the given email already exists");
        }
        return userRepository.save(user);
    }

    // converts data from excel file to list of users and will save
    public void save(MultipartFile file) throws IOException {
        System.out.println("inside save function of UserController..");
        System.out.println("file name is "+file.getOriginalFilename());
       List<User> usersList = Helper.convertExcelToListOfUser(file.getInputStream());
       this.userRepository.saveAll(usersList);
    }

    // returns all list of users as json
    public List<User> getAllUsers(){
        return this.userRepository.findAll();
    }
}
