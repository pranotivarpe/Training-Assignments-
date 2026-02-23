package com.votingsystem.service;

import com.votingsystem.dto.RegistrationDto;
import com.votingsystem.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User registerUser(RegistrationDto registrationDto);

    Optional<User> findByEmail(String email);

    Optional<User> findById(Long id);

    List<User> findAllUsers();

    boolean emailExists(String email);
}
