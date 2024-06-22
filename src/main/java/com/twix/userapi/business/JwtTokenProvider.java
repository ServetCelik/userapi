package com.twix.userapi.business;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {
    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .parseClaimsJws(token)
                .getBody();
    }
}
