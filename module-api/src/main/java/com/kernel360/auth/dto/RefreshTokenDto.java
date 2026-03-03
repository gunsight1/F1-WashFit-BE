package com.kernel360.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RefreshTokenDto(
        @JsonProperty("refresh_token")
        String refreshToken
) {
}
