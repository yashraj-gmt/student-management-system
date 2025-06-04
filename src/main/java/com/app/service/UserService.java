package com.app.service;

import com.app.entity.User;

import java.util.Optional;

public interface UserService {

    User save(User user);

    Optional<User> findByEmail(String email);
}
