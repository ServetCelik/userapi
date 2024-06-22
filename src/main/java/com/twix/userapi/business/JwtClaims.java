package com.twix.userapi.business;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtClaims {
    @JsonProperty("sub")
    private String subject;

    @JsonProperty("iat")
    private long issuedAt;

    @JsonProperty("exp")
    private long expiration;

    @JsonProperty("userId")
    private long userId;
}

