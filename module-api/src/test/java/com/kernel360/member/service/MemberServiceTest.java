package com.kernel360.member.service;

import com.kernel360.auth.service.AuthService;
import com.kernel360.global.jwt.JwtTokenProvider;
import com.kernel360.global.security.CustomUserDetails;
import com.kernel360.member.dto.MemberDto;
import com.kernel360.member.entity.Member;
import com.kernel360.member.enumset.AccountType;
import com.kernel360.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private AuthService authService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("회원가입 로직 테스트")
    void testJoinMember() {
        // given
        MemberDto requestDto = MemberDto.of("testID", "test@email.com", "password", "MALE", "AGE_20", null, null, null, null, null, null);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(memberRepository.findOneById(anyString())).thenReturn(null);

        // when
        memberService.joinMember(requestDto);

        // then
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    @DisplayName("로그인 테스트")
    void testLogin() {
        // given
        MemberDto loginDto = MemberDto.of("testUser", "password");
        Member mockMember = Member.of(1L, "testUser", "test@email.com", "encodedPassword", 0, 0, AccountType.PLATFORM.name());
        CustomUserDetails userDetails = new CustomUserDetails(mockMember);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtTokenProvider.createAccessToken(any())).thenReturn("access-token");
        when(jwtTokenProvider.createRefreshToken(any())).thenReturn("refresh-token");

        // when
        MemberDto result = memberService.login(loginDto, new MockHttpServletRequest());

        // then
        assertNotNull(result);
        assertEquals("access-token", result.jwtToken());
        assertEquals("refresh-token", result.refreshToken());
        verify(authService, times(1)).saveRefreshToken(any(Member.class), eq("refresh-token"), any());
    }

    @Test
    @DisplayName("비밀번호 변경 테스트")
    void testChangePassword() {
        // given
        String token = "some-token";
        String memberId = "testUser";
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";
        Member mockMember = Member.of(1L, memberId, "test@email.com", passwordEncoder.encode(oldPassword), 0, 0, AccountType.PLATFORM.name());

        when(jwtTokenProvider.getSubject(token)).thenReturn(memberId);
        when(memberRepository.findOneByIdForAccountTypeByPlatform(memberId)).thenReturn(mockMember);
        when(passwordEncoder.matches(newPassword, mockMember.getPassword())).thenReturn(false);
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");

        // when
        memberService.changePassword(newPassword, token);

        // then
        verify(mockMember, times(1)).updatePassword("encodedNewPassword");
    }
}
