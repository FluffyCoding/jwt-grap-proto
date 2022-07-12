package com.unity.jwtgrapproto.service;

import com.unity.jwtgrapproto.models.User;
import com.unity.jwtgrapproto.repositories.UserRepository;
import com.unity.jwtgrapproto.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.util.List;

import static com.unity.jwtgrapproto.enumeration.Role.ROLE_USER;

@RequiredArgsConstructor
@Slf4j
@Transactional
@Service
@Qualifier("userDetailService")
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByUsername(username).orElseThrow(
                () -> {
                    log.error("username not found {}", username);
                    throw new UsernameNotFoundException("User not found");
                });
        log.info("user found with {}", user.getUsername());
        return new UserPrincipal(user);
    }


    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User findByUsername(String name) {
        return userRepository.findByUsername(name)
                .orElseThrow(() ->
                        new EntityNotFoundException("Invalid Username"));
    }

    public User findByUseEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new EntityNotFoundException("Invalid User Email"));
    }

    public User saveUser(@NotNull User user) {
        userRepository.findByUsername(user.getUsername()).ifPresent(u -> {
            throw new EntityExistsException("User Wit " + u.getUsername() + " Already Exists");
        });
        userRepository.findByEmail(user.getEmail()).ifPresent(u -> {
            throw new EntityExistsException("User email " + u.getEmail() + " Already Exists");
        });
        user.setRole(ROLE_USER.name());
        user.setAuthorities(ROLE_USER.getAuthorities());
        return userRepository.save(user);
    }

    public User updateUser(User user) {
        if (user.getId() == null | user.getId() == 0) throw new EntityNotFoundException("Invalid User Id");
        User userRec = userRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("User Does Not Exists"));
        user.setPassword(encodedPassword(user.getPassword()));
        return userRepository.save(user);
    }

    public User disableUserAccount(@NotNull Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User Does Not Exists"));
        user.setIsNotLocked("false");
        return userRepository.save(user);
    }

    private String encodedPassword(String password) {
        return passwordEncoder.encode(password);
    }

}

