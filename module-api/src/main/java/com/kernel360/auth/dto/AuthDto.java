package com.kernel360.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthDto(
        @JsonProperty("access_token")
        String accessToken,
        @JsonProperty("refresh_token")
        String refreshToken
) {
    public static AuthDto of(
            String accessToken,
            String refreshToken
    ){
        return new AuthDto(
                accessToken,
                refreshToken
        );
    }
}
