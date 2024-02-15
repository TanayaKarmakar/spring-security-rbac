package com.app.rbac.services;

import com.app.rbac.entity.User;

import java.security.Principal;
import java.util.List;

public interface UserService {
    User createNewUser(User user);

    User giveAccessToUser(Long userId, String roles, Principal principal);

    List<User> getAllUsers();
}
