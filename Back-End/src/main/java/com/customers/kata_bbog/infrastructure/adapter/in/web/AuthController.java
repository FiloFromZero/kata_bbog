package com.customers.kata_bbog.infrastructure.adapter.in.web;

import com.customers.kata_bbog.infrastructure.security.JwtTokenProvider;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtTokenProvider tokenProvider;

    public AuthController(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/login")
    public Map<String, String> login(Authentication authentication) {
        String token = tokenProvider.generateToken(authentication.getName());
        return Map.of("token", token);
    }
}
