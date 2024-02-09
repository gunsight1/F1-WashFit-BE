package com.kernel360.auth.reader;

import com.kernel360.auth.entity.Auth;
import com.kernel360.auth.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AuthReaderImpl implements AuthReader {

    private final AuthRepository authRepository;
    @Override
    public Auth findOneByMemberNo(Long memberNo) {
        return authRepository.findOneByMemberNo(memberNo);
    }

    @Override
    public Auth findOneByJwtToken(String encryptToken) {    return authRepository.findOneByJwtToken(encryptToken);  }
}
