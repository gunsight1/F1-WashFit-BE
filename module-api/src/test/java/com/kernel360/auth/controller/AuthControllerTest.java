package com.kernel360.auth.controller;

import com.kernel360.auth.dto.AuthDto;
import com.kernel360.auth.dto.RefreshTokenDto;
import com.kernel360.common.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import static com.kernel360.common.utils.RestDocumentUtils.getDocumentRequest;
import static com.kernel360.common.utils.RestDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends ControllerTest {

    @Test
    @DisplayName("토큰 갱신 요청 시 201 상태코드와 갱신된 토큰을 반환한다")
    void reissueToken() throws Exception {
        // given
        String refreshToken = "refresh-token";
        String newAccessToken = "new-access-token";
        String newRefreshToken = "new-refresh-token";
        
        RefreshTokenDto requestDto = new RefreshTokenDto(refreshToken);
        AuthDto responseDto = AuthDto.of(newAccessToken, newRefreshToken);

        given(authService.reissue(any(), any())).willReturn(responseDto);

        // when & then
        mockMvc.perform(post("/auth/reissue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.code").value("BAC001"))
                .andExpect(jsonPath("$.message").value("JWT 토큰 재발급 성공"))
                .andExpect(jsonPath("$.value.access_token").value(newAccessToken))
                .andExpect(jsonPath("$.value.refresh_token").value(newRefreshToken))
                .andDo(document("auth/reissue",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("refresh_token").type(JsonFieldType.STRING).description("리프레시 토큰")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.NUMBER).description("상태 코드"),
                                fieldWithPath("code").type(JsonFieldType.STRING).description("비즈니스 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("value.access_token").type(JsonFieldType.STRING).description("새로운 액세스 토큰"),
                                fieldWithPath("value.refresh_token").type(JsonFieldType.STRING).description("새로운 리프레시 토큰")
                        )));
    }
}
