package com.kernel360.Auth;

import com.kernel360.auth.entity.Auth;

public interface AuthService {
    Auth modifyAuthJwt(Auth storedAuthInfo, String newEncryptToken);

    Auth findOneByMemberNo(Long memberNo);

    void saveAuth(Auth authJwt);

    Auth createAuthJwt(Long memberNo, String encryptToken);

    Auth findOneAuthByJwt(String encryptToken);

}
