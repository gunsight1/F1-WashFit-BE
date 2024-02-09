package com.kernel360.auth.store;

import com.kernel360.auth.entity.Auth;

public interface AuthStore {
    void saveAuth(Auth authJwt);

    void saveReissuanceJwt(Auth storedAuthInfo);
}
