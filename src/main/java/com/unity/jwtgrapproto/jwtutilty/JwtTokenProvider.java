package com.unity.jwtgrapproto.jwtutilty;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.unity.jwtgrapproto.security.UserPrincipal;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.unity.jwtgrapproto.constants.SecurityConstants.*;
import static java.util.Arrays.stream;

@Component
public class JwtTokenProvider {

    @Value("${jwt.security.key}")
    private String secret;

    public String generateJwtToken(UserPrincipal userPrincipal) {
        String[] claims = getClaimsFromUser(userPrincipal);
        return JWT.create()
                .withIssuer(GET_YUMY_PVT_LTD)
                .withAudience(GET_YUMY_ADMINISTRATION)
                .withIssuedAt(new Date())
                .withSubject(userPrincipal.getUsername())
                .withArrayClaim(AUTHORITIES, claims)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(secret.getBytes()));
    }


    public List<GrantedAuthority> getAuthories(String token) {
        String[] claims = getClaimsFromToken(token);
        assert claims != null;
        return stream(claims).map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }


    public Authentication getAuthentication(String userName,
                                            List<GrantedAuthority> grantedAuthorities,
                                            HttpServletRequest request) {
        var userPasswordAuthToken = new
                UsernamePasswordAuthenticationToken(userName, null, grantedAuthorities);
        userPasswordAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return userPasswordAuthToken;
    }

    public String getSubject(String token) {
        JWTVerifier verifier = getJwtVerifier();
        return verifier.verify(token).getSubject();
    }


    public boolean isTokenValid(String token, String username) {
        JWTVerifier verifier = getJwtVerifier();
        verifier.verify(token);
        return StringUtils.isNotBlank(username) && !isTokenExpired(verifier, token);
    }

    private String[] getClaimsFromUser(UserPrincipal userPrincipal) {

        var userAuth = userPrincipal.getAuthorities()
                .stream().map(List::of).toArray(String[]::new);

        System.out.println(Arrays.toString(userAuth));

        List<String> authorities = new ArrayList<>();
        for (GrantedAuthority auth : userPrincipal.getAuthorities()) {
            authorities.add(auth.getAuthority());
        }
        return authorities.toArray(new String[0]);
    }

    private String[] getClaimsFromToken(String token) {
        JWTVerifier jwtVerifier = getJwtVerifier();
        return jwtVerifier.verify(token).getClaim(AUTHORITIES).asArray(String.class);
    }

    private JWTVerifier getJwtVerifier() {
        JWTVerifier verifier;
        try {
            Algorithm algorithm = Algorithm.HMAC512(secret);
            verifier = JWT.require(algorithm).withIssuer(GET_YUMY_PVT_LTD).build();
        } catch (JWTVerificationException exception) {
            throw new JWTVerificationException(TOKEN_CANNOT_BE_VERIFIED);
        }
        return verifier;
    }


    private boolean isTokenExpired(JWTVerifier verifier, String token) {
        Date expiration = verifier.verify(token).getExpiresAt();
        return expiration.before(new Date());
    }


}
