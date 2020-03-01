package com.example.demo.security;

public class SecurityConstants {

    public static final long EXPIRATION_TIME = 864_000_000; // equals 10 days.
    public static final String SECRET = "a9s87hAh21ka90ka"; // Secret Key to Generate JWT.
    public static final String TOKEN_PREFIX = "TokenPrefix ";
    public static final String HEADER_STRING = "Authorization Header";
    public static final String LOGIN_URL = "/login";
    public static final String SIGN_UP_URL = "/api/user/create";
}
