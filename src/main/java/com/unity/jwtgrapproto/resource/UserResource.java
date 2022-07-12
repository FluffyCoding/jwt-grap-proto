package com.unity.jwtgrapproto.resource;

import com.unity.jwtgrapproto.jwtutilty.JwtTokenProvider;
import com.unity.jwtgrapproto.models.User;
import com.unity.jwtgrapproto.security.UserPrincipal;
import com.unity.jwtgrapproto.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import static com.unity.jwtgrapproto.constants.SecurityConstants.JWT_TOKEN_HEADER;


@RestController
@RequestMapping(value = "/user")
public class UserResource {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public UserResource(AuthenticationManager authenticationManager, UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }


    @GetMapping(path = "/home")
    public String showUser() {
        return "Application Works";
    }

    @GetMapping(path = "/secure")
    public String secure() {
        return "This is a Secure Resource";
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user) {
        authenticate(user.getUsername(), user.getPassword());
        var loginUser = userService.findByUsername(user.getUsername());
        var userPrinciple = new UserPrincipal(loginUser);
        var jwtHeader = getJwtHeader(userPrinciple);
        return new ResponseEntity<>(loginUser, jwtHeader, HttpStatus.OK);
    }


    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    private HttpHeaders getJwtHeader(UserPrincipal userPrincipal) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER, jwtTokenProvider.generateJwtToken(userPrincipal));
        return headers;
    }

}
