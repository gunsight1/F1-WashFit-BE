package com.kernel360.auth.service;

import com.kernel360.auth.dto.AuthDto;
import com.kernel360.auth.entity.Auth;
import com.kernel360.auth.repository.AuthRepository;
import com.kernel360.exception.BusinessException;
import com.kernel360.global.jwt.JwtTokenProvider;
import com.kernel360.member.code.MemberErrorCode;
import com.kernel360.member.entity.Member;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthRepository authRepository;

    @Transactional
    public AuthDto reissue(String refreshToken, HttpServletRequest request) {
        // 1. Refresh Token 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BusinessException(MemberErrorCode.INVALID_TOKEN_REQUEST); //FIXME
        }

        // 2. Refresh Token 에서 Member ID 가져오기
        String memberId = jwtTokenProvider.getSubject(refreshToken);

        // 3. 저장소에서 Member ID 를 기반으로 Refresh Token 값 가져옴
        Auth auth = authRepository.findOneByMemberId(memberId)
                .orElseThrow(() -> new BusinessException(MemberErrorCode.LOGOUT_MEMBER)); //FIXME

        // 4. Refresh Token 일치하는지 검사
        if (!auth.getJwtToken().equals(refreshToken)) {
            throw new BusinessException(MemberErrorCode.INVALID_TOKEN_REQUEST); //FIXME
        }

        // 5. 새로운 토큰 생성
        Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);
        String newAccessToken = jwtTokenProvider.createAccessToken(authentication);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(authentication);

        // 6. 저장소 정보 업데이트
        String clientIP = getClientIP(request);
        auth.updateJwt(newRefreshToken, clientIP);

        return AuthDto.of(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void saveRefreshToken(Member member, String refreshToken, HttpServletRequest request) {
        Auth auth = authRepository.findOneByMemberNo(member.getMemberNo());
        String clientIP = getClientIP(request);

        if (auth == null) {
            Auth newAuth = Auth.of(null, member.getMemberNo(), refreshToken, null, clientIP);
            authRepository.save(newAuth);
        } else {
            auth.updateJwt(refreshToken, clientIP);
        }
    }

    private String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null) ip = request.getHeader("Proxy-Client-IP");
        if (ip == null) ip = request.getHeader("WL-Proxy-Client-IP");
        if (ip == null) ip = request.getHeader("HTTP_CLIENT_IP");
        if (ip == null) ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        if (ip == null) ip = request.getRemoteAddr();
        return ip;
    }
}
