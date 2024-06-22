package com.twix.userapi.business;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenServiceImp implements TokenService{
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Override
    public boolean isAuthorized(String token, Long idFromParams) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            Claims claims = jwtTokenProvider.getClaimsFromToken(token);
            Long userId = claims.get("userId", Long.class);
            return userId.equals(idFromParams);
        }
        return false;
    }
    @Override
    public boolean isAuthorized(String token, String idFromParams) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            Claims claims = jwtTokenProvider.getClaimsFromToken(token);
            String subject = claims.get("sub", String.class);
            return subject.equals(idFromParams);
        }
        return false;
    }
}
