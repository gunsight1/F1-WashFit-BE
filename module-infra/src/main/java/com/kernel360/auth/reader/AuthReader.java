package com.kernel360.auth.reader;

import com.kernel360.auth.entity.Auth;

public interface AuthReader {
    Auth findOneByMemberNo(Long memberNo);

    Auth findOneByJwtToken(String encryptToken);
}
