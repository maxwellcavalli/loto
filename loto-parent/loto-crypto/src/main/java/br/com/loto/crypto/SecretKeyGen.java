/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.crypto;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 *
 * @author maxwe
 */
public class SecretKeyGen {

    private static final Logger LOG = Logger.getLogger(SecretKeyGen.class.getName());

    private static SecretKey generateSecretKey() {
        KeyGenerator keyGen = null;
        try {
            /*
    * Get KeyGenerator object that generates secret keys for the
    * specified algorithm.
             */
            keyGen = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            LOG.log(Level.SEVERE, null, e);
        }

        /* Initializes this key generator for key size to 256. */
        keyGen.init(256);

        /* Generates a secret key */
        SecretKey secretKey = keyGen.generateKey();

        return secretKey;
    }

    public static String encodedKey() {
        SecretKey secretKey = generateSecretKey();
        String encodedKey = Base64.getEncoder().encodeToString(
                secretKey.getEncoded());
        
        return encodedKey;
    }

}
