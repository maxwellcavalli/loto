/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.crypto;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 *
 * @author maxwe
 */
public class EncriptaDecriptaRSA {

    private static final Logger LOG = Logger.getLogger(EncriptaDecriptaRSA.class.getName());

    public static final String ALGORITHM = "RSA";

    /**
     * Gera a chave que contém um par de chave Privada e Pública usando 1025
     * bytes. Armazena o conjunto de chaves nos arquivos private.key e
     * public.key
     *
     * @param properties
     */
    public static void geraChave(Properties properties) {
        try {
            final KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
            keyGen.initialize(1024);
            final KeyPair key = keyGen.generateKeyPair();

            File chavePrivadaFile = new File(properties.getProperty("private.key"));
            File chavePublicaFile = new File(properties.getProperty("public.key"));

            // Cria os arquivos para armazenar a chave Privada e a chave Publica
            if (chavePrivadaFile.getParentFile() != null) {
                chavePrivadaFile.getParentFile().mkdirs();
            }

            chavePrivadaFile.createNewFile();

            if (chavePublicaFile.getParentFile() != null) {
                chavePublicaFile.getParentFile().mkdirs();
            }

            chavePublicaFile.createNewFile();

            try ( // Salva a Chave Pública no arquivo
                    ObjectOutputStream chavePublicaOS = new ObjectOutputStream(
                            new FileOutputStream(chavePublicaFile))) {
                chavePublicaOS.writeObject(key.getPublic());
            }

            try ( // Salva a Chave Privada no arquivo
                    ObjectOutputStream chavePrivadaOS = new ObjectOutputStream(
                            new FileOutputStream(chavePrivadaFile))) {
                chavePrivadaOS.writeObject(key.getPrivate());
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            LOG.log(Level.SEVERE, null, e);
        }

    }

    /**
     * Verifica se o par de chaves Pública e Privada já foram geradas.
     *
     * @return
     */
    public static boolean verificaSeExisteChavesNoSO(Properties properties) {

        File chavePrivadaFile = new File(properties.getProperty("private.key"));
        File chavePublicaFile = new File(properties.getProperty("public.key"));

        if (chavePrivadaFile.exists() && chavePublicaFile.exists()) {
            return true;
        }

        return false;
    }

    /**
     * Criptografa o texto puro usando chave pública.
     *
     * @param texto
     * @param chave
     * @return
     */
    public static byte[] criptografa(String texto, PublicKey chave) {
        byte[] cipherText = null;

        try {
            final Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            // Criptografa o texto puro usando a chave Púlica
            cipher.init(Cipher.ENCRYPT_MODE, chave);
            cipherText = cipher.doFinal(texto.getBytes());
        } catch (InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
            LOG.log(Level.SEVERE, null, e);
        }

        return cipherText;
    }

    /**
     * Decriptografa o texto puro usando chave privada.
     *
     * @param texto
     * @param chave
     * @return
     */
    public static String decriptografa(byte[] texto, PrivateKey chave) {
        byte[] dectyptedText = null;

        try {
            final Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            // Decriptografa o texto puro usando a chave Privada
            cipher.init(Cipher.DECRYPT_MODE, chave);
            dectyptedText = cipher.doFinal(texto);

        } catch (InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }

        return new String(dectyptedText);
    }
    
    public static byte[] decriptografaToBytes(byte[] texto, PrivateKey chave) {
        byte[] dectyptedText = null;

        try {
            final Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            // Decriptografa o texto puro usando a chave Privada
            cipher.init(Cipher.DECRYPT_MODE, chave);
            dectyptedText = cipher.doFinal(texto);

        } catch (InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }

        return dectyptedText;
    }
    

}
