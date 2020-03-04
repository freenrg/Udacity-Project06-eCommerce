package com.example.demo.security;

public class SecurityConstants {

    public static final long EXPIRATION_TIME = 864_000_000; // equals 10 days.
    public static final String SECRET = "oursecretkey"; // Secret Key to Generate JWT.
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String LOGIN_URL = "/login";
    public static final String SIGN_UP_URL = "/api/user/create";
}
