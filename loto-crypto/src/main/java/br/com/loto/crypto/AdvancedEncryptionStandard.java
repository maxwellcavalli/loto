/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.crypto;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author maxwe
 */
public final class AdvancedEncryptionStandard {

    private static final String ALGORITHM = "AES";

    /**
     * Encrypts the given plain text
     *
     * @param key
     * @param plainText The plain text to encrypt
     * @return
     * @throws java.lang.Exception
     */
    public static byte[] encrypt(byte[] key, byte[] plainText) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        return cipher.doFinal(plainText);
    }

    /**
     * Decrypts the given byte array
     *
     * @param key
     * @param cipherText The data to decrypt
     * @return
     * @throws java.lang.Exception
     */
    public static byte[] decrypt(byte[] key, byte[] cipherText) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        return cipher.doFinal(cipherText);
    }
}
