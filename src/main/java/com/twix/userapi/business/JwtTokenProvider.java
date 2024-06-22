package com.twix.userapi.business;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Base64;

@Component
public class JwtTokenProvider {
    public JwtClaims getClaimsFromToken(String token) {
        String[] parts = token.split("\\.");
        if (parts.length == 3) {
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper.readValue(payload, JwtClaims.class);
            } catch (IOException e) {
                throw new RuntimeException("Failed to decode JWT payload", e);
            }
        } else {
            throw new IllegalArgumentException("Invalid JWT token");
        }
    }
}
