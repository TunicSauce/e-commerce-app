package com.geraldikem.ecommerceapp.service;

import com.geraldikem.ecommerceapp.dto.UserRegistrationDto;
import com.geraldikem.ecommerceapp.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User save(UserRegistrationDto registrationDto);
    Optional<User> findByEmail(String email);
    void updateProfile(String email, String firstName, String lastName);
    List<User> findAllUsers();
    void updateUserRole(Long userId, Long roleId);
}