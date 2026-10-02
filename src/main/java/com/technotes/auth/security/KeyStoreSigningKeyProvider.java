package com.technotes.auth.security;

import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Component
public class KeyStoreSigningKeyProvider implements SigningKeyProvider {

    private final String keyStorePath;
    private final String keyStorePassword;
    private final String keyAlias;
    private final String keyPassword;

    public KeyStoreSigningKeyProvider(
        @Value("${OAUTH_KEYSTORE_PATH}") String keyStorePath,
        @Value("${OAUTH_KEYSTORE_PASSWORD}") String keyStorePassword,
        @Value("${OAUTH_KEY_ALIAS}") String keyAlias,
        @Value("${OAUTH_KEY_PASSWORD}") String keyPassword) {

        this.keyStorePath = keyStorePath;
        this.keyStorePassword = keyStorePassword;
        this.keyAlias = keyAlias;
        this.keyPassword = keyPassword;
    }

    @Override
    public RSAKey loadSigningKey() {
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");

            try (FileInputStream inputStream =
                     new FileInputStream(keyStorePath)) {

                keyStore.load(
                    inputStream,
                    keyStorePassword.toCharArray()
                );
            }

            PrivateKey privateKey = (PrivateKey) keyStore.getKey(
                keyAlias,
                keyPassword.toCharArray()
            );

            Certificate certificate =
                keyStore.getCertificate(keyAlias);

            if (!(privateKey instanceof RSAPrivateKey rsaPrivateKey)) {
                throw new IllegalStateException(
                    "Signing private key is not an RSA private key"
                );
            }

            if (certificate == null
                || !(certificate.getPublicKey()
                instanceof RSAPublicKey rsaPublicKey)) {

                throw new IllegalStateException(
                    "Signing certificate does not contain an RSA public key"
                );
            }

            return new RSAKey.Builder(rsaPublicKey)
                .privateKey(rsaPrivateKey)
                .keyID(keyAlias)
                .build();

        } catch (Exception exception) {
            throw new IllegalStateException(
                "Unable to load persistent OAuth signing key",
                exception
            );
        }
    }
}
