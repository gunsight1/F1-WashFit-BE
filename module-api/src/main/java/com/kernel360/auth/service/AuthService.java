package com.kernel360.auth.service;

import com.kernel360.auth.dto.AuthDto;
import com.kernel360.auth.entity.Auth;
import com.kernel360.auth.repository.AuthRepository;
import com.kernel360.global.jwt.JwtTokenProvider;
import com.kernel360.member.entity.Member;
import com.kernel360.utils.ConvertSHA256;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

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
            throw new RuntimeException("Refresh Token 이 유효하지 않습니다."); // TODO: Custom Exception
        }

        // 2. Refresh Token 에서 Member ID 가져오기
        String memberId = jwtTokenProvider.getSubject(refreshToken);

        // 3. 저장소에서 Member ID 를 기반으로 Refresh Token 값 가져옴
        Auth auth = authRepository.findOneByMemberId(memberId) // findOneByMemberId 추가 필요
                .orElseThrow(() -> new RuntimeException("로그아웃 된 사용자입니다.")); // TODO: Custom Exception

        // 4. Refresh Token 일치하는지 검사
        if (!auth.getJwtToken().equals(ConvertSHA256.convertToSHA256(refreshToken))) {
            throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다."); // TODO: Custom Exception
        }

        // 5. 새로운 토큰 생성
        Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);
        String newAccessToken = jwtTokenProvider.createAccessToken(authentication);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(authentication);

        // 6. 저장소 정보 업데이트
        String clientIP = getClientIP(request);
        auth.updateJwt(ConvertSHA256.convertToSHA256(newRefreshToken), clientIP);

        return AuthDto.of(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void saveRefreshToken(Member member, String refreshToken, HttpServletRequest request) {
        Auth auth = authRepository.findOneByMemberNo(member.getMemberNo());
        String clientIP = getClientIP(request);
        String encryptedToken = ConvertSHA256.convertToSHA256(refreshToken);

        if (auth == null) {
            Auth newAuth = Auth.of(null, member.getMemberNo(), encryptedToken, null, clientIP);
            authRepository.save(newAuth);
        } else {
            auth.updateJwt(encryptedToken, clientIP);
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
