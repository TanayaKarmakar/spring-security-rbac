package com.app.rbac.controller;

import com.app.rbac.entity.User;
import com.app.rbac.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/join")
    public String createUser(@RequestBody User user) {
        User createdUser = userService.createNewUser(user);
        return "Hello " + createdUser.getUserName() + ", welcome to the group";
    }

    @GetMapping("/access/{userId}/{roles}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MODERATOR')")
    public String giveAccessToUser(@PathVariable Long userId, @PathVariable String roles, Principal principal) {
        User user = userService.giveAccessToUser(userId, roles, principal);
        return "Role has given to user " + user.getUserName() + " by " + principal.getName();
    }

    @GetMapping
    @Secured("ROLE_ADMIN")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
}
