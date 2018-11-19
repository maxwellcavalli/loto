/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.loto.crypto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.PublicKey;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author maxwe
 */
public class MainApp {

    static Properties properties;
    private static final Logger LOG = Logger.getLogger(MainApp.class.getName());

    public static void main(String[] args) {
        try {
            String configFilePath = System.getProperty("config.file");
            properties = new Properties();
            properties.load(new FileInputStream(new File(configFilePath)));

        } catch (IOException e) {
            LOG.log(Level.SEVERE, null, e);
            System.exit(0);
        }

        if (!EncriptaDecriptaRSA.verificaSeExisteChavesNoSO(properties)) {
            LOG.log(Level.INFO, "Gerando novas chaves de criptografia");
            EncriptaDecriptaRSA.geraChave(properties);

            try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(properties.getProperty("public.key")));) {

                final PublicKey chavePublica;

                chavePublica = (PublicKey) inputStream.readObject();
                String data = "64422D7F3BF0D1034D4AB55E8928BACB";

                byte[] passwordCrypto = EncriptaDecriptaRSA.criptografa(data, chavePublica);

                File file = new File(properties.getProperty("password.file"));

                try (FileOutputStream fOut = new FileOutputStream(file);) {
                    fOut.write(passwordCrypto);
                    fOut.flush();
                }

            } catch (IOException | ClassNotFoundException e) {
                LOG.log(Level.SEVERE, null, e);
            }

            //gerar password
        }
    }

}
