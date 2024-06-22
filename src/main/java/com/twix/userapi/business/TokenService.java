package com.twix.userapi.business;

public interface TokenService {
    boolean isAuthorized(String token, Long idFromParams);
    boolean isAuthorized(String token, String idFromParams);
}
