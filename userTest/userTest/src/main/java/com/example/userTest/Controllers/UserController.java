package com.example.userTest.Controllers;

import com.example.userTest.Entities.UserEntity;
import com.example.userTest.Repositories.UserRepository;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @PostMapping("/createUser")
    public UserEntity createUser(@RequestBody UserEntity user){
        return userRepository.saveAndFlush(user);
    }
    @GetMapping("/getUser/{id}")
    public Optional<UserEntity> getUserById(@PathVariable Optional<Long> id){
        if(id.isPresent()) {
            return userRepository.findById(id.get());
        }else{
            return null;
        }
    }
    @PutMapping("/updateUser/{id}")
    public UserEntity updateNameById(@PathVariable Long id,
                                     @RequestBody @NonNull UserEntity user){
        Optional<UserEntity> userExist = userRepository.findById(id);
       if(userExist.isPresent()) {
           return userRepository.saveAndFlush(user);
       }else{
           return null;
       }
    }
    @DeleteMapping("/deleteUser/{id}")
    public void deleteUserById(@PathVariable Optional<Long> id){
        if(id.isPresent()) {
            userRepository.deleteById(id.get());
        }
    }
}
