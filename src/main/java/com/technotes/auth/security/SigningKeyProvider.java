package com.technotes.auth.security;

import com.nimbusds.jose.jwk.RSAKey;

public interface SigningKeyProvider {

    RSAKey loadSigningKey();
}
