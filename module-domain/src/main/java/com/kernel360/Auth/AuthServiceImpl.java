package com.kernel360.Auth;

import com.kernel360.auth.entity.Auth;
import com.kernel360.auth.reader.AuthReader;
import com.kernel360.auth.store.AuthStore;
import com.kernel360.utils.JWT;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JWT jwt;
    private final AuthStore authStore;
    private final AuthReader authReader;


    public Auth findOneAuthByJwt(String encryptToken) {
        return authReader.findOneByJwtToken(encryptToken);
    }

    public void reissuanceJwt(Auth storedAuthInfo) {
        authStore.saveReissuanceJwt(storedAuthInfo);
    }

    public Auth modifyAuthJwt(Auth modifyAuth, String encryptToken) {
        modifyAuth.updateJwt(encryptToken);

        return modifyAuth;
    }

    @Override
    public Auth findOneByMemberNo(Long memberNo) {
        return authReader.findOneByMemberNo(memberNo);
    }

    @Override
    public void saveAuth(Auth authJwt) {
        authStore.saveAuth(authJwt);
    }

    public Auth createAuthJwt(Long memberNo, String encryptToken) {

        return Auth.of(null, memberNo, encryptToken, null);
    }

}
