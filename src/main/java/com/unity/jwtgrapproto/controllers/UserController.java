package com.unity.jwtgrapproto.controllers;

import com.unity.jwtgrapproto.models.User;
import com.unity.jwtgrapproto.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import javax.persistence.EntityExistsException;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @MutationMapping
    public User saveNewUser(@Argument User user) {


        return userService.saveUser(user);
    }

    @MutationMapping
    public User updateExistingUser(@Valid @Argument User user) {
        return userService.updateUser(user);
    }

    @QueryMapping
    public User findUserWithName(@Argument String name) {
        return userService.findByUsername(name);
    }

    @QueryMapping
    public List<User> findAllUsers() {
        return userService.findAllUsers();
    }

    @QueryMapping
    public String hello() {
        //throw new SimpleGraphQLException("Simple Error",ErrorType.BAD_REQUEST);
        //throw new ConstraintViolationException("Simple Error", Collections.emptySet());
        throw new EntityExistsException("Record Found");
    }


}

