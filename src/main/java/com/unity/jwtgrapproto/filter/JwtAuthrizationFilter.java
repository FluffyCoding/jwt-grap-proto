package com.unity.jwtgrapproto.filter;

import com.unity.jwtgrapproto.constants.SecurityConstants;
import com.unity.jwtgrapproto.jwtutilty.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.unity.jwtgrapproto.constants.SecurityConstants.OPTIONS_HTTP_METHOD;
import static com.unity.jwtgrapproto.constants.SecurityConstants.TOKEN_PREFIX;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpStatus.OK;

@RequiredArgsConstructor
@Component
public class JwtAuthrizationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {


        if (request.getMethod().equalsIgnoreCase(OPTIONS_HTTP_METHOD)) {
            response.setStatus(OK.value());
        } else {
            String authHeader = request.getHeader(AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith(TOKEN_PREFIX)) {
                filterChain.doFilter(request, response);
                return;
            }
            String token = authHeader.substring(TOKEN_PREFIX.length());
            String username = tokenProvider.getSubject(token);
            if (tokenProvider.isTokenValid(token, username)) {
                var authorities = tokenProvider.getAuthories(token);
                var authentication = tokenProvider.getAuthentication(username, authorities, request);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }
}
