package com.rdmishra.backend_tms.dto.response;

public record TokenRespnose(
        String accessToken,
        String refreshToken,
        long experin,
        String tokenType,
        UserResponse user

) {
    public static TokenRespnose of(String accessToken, String refreshToken, long experin, UserResponse user) {
        return new TokenRespnose(accessToken, refreshToken, experin, "Bearer", user);
    }

}
