package com.kernel360.auth.store;

import com.kernel360.auth.entity.Auth;
import com.kernel360.auth.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AuthStoreImpl implements AuthStore {
    private final AuthRepository authRepository;
    @Override
    public void saveAuth(Auth authJwt) {
        authRepository.save(authJwt);
    }

    @Override
    public void saveReissuanceJwt(Auth storedAuthInfo) {
        authRepository.save(storedAuthInfo);
    }
}
