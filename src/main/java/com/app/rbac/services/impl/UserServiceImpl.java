package com.app.rbac.services.impl;

import com.app.rbac.entity.User;
import com.app.rbac.exception.AccessDeniedException;
import com.app.rbac.exception.NotFoundException;
import com.app.rbac.repository.UserRepository;
import com.app.rbac.services.UserService;
import com.app.rbac.utils.UserConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import java.security.Principal;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public User createNewUser(User user) {
        user.setRoles(UserConstants.DEFAULT_ROLE);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User giveAccessToUser(Long userId, String roles, Principal principal) {
        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isEmpty()) {
            throw new NotFoundException("User with ID " + userId + " doesn't exist in the system");
        }
        List<String> loggedInUserRoles = extractRolesOfLoggedInUser(principal);
        User user = userOptional.get();
        StringBuilder sb = new StringBuilder();
        if(loggedInUserRoles.contains(roles)) {
            sb.append(user.getRoles())
                    .append(",")
                    .append(roles);
            user.setRoles(sb.toString());
            return userRepository.save(user);

        }
        throw new AccessDeniedException("User " + principal.getName() + " doesn't have enough " +
                "permission to give the roles [" + roles + "] to user " + userOptional.get().getUserName());

    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    private List<String> extractRolesOfLoggedInUser(Principal principal) {
        Optional<User> userOptional = userRepository.findByUserName(principal.getName());
        if(userOptional.isEmpty()) {
            throw new NotFoundException("User with name " + principal.getName() +
                    " doesn't exist in the system");
        }
        String roles = userOptional.get().getRoles();
        List<String> userRoles = Arrays.stream(roles.split(","))
                .toList();
        if(userRoles.contains("ROLE_ADMIN"))
            return Arrays.stream(UserConstants.ADMIN_ACCESS).toList();
        if(userRoles.contains("ROLE_MODERATOR"))
            return Arrays.stream(UserConstants.MODERATOR_ACCESS).toList();
        return new ArrayList<>();
    }
}
