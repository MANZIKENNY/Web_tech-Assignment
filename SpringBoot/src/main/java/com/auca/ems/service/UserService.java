package com.auca.ems.service;

import com.auca.ems.model.User;
import com.auca.ems.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    public User save(User user) {
        return userRepository.save(user);
    }
    
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }
    
    public List<User> findAll() {
        return userRepository.findAll();
    }
    
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}
