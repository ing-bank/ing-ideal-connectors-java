package com.ing.ideal.connector.crypto;

import java.security.KeyStore;

/**
 * Holds information about a keystore and certificate.
 */
public class KeyStoreItem {

    /**
     * Key store.
     */
    private final KeyStore keyStore;
    /**
     * Encrypted KeyStore password.
     */
    private final String encPasswd;
    /**
     * Certificate alias.
     */
    private final String alias;

    /**
     * Creates a new KeyStoreItem object.
     *
     * @param keyStore  keyStore
     * @param encPasswd encrypted password
     * @param alias     certificate alias
     */
    protected KeyStoreItem(KeyStore keyStore, String encPasswd, String alias) {
        this.keyStore = keyStore;
        this.encPasswd = encPasswd;
        this.alias = alias;
    }

    /**
     * Gets the value of the keystore.
     *
     * @return keystore
     */
    public KeyStore getKeyStore() {
        return keyStore;
    }

    /**
     * @return decrypted password
     */
    public String getKeyStorePassword() {
        return PasswordUtility.decode(encPasswd);
    }

    /**
     * Gets the certificate alias.
     *
     * @return certificate alias
     */
    public String getAlias() {
        return alias;
    }

    /**
     * @return decrypted password as {@code char[]}
     */
    public char[] getCharPassword() {
        return PasswordUtility.decode(encPasswd).toCharArray();
    }
}