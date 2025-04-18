package org.example.firststep.utils;

import lombok.extern.log4j.Log4j2;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Base64;

@Log4j2
public class MongoPasswordEncryptor {

    // Secrete Key for encryption
    final byte[] SECRET_KEY;

    // Encryption algorithm
    final String ENC = "AES";

    final String ALGORITHM = "AES/ECB/PKCS5Padding";

    public MongoPasswordEncryptor() {
        try {
            // Anahtarı uygun uzunluğa getir (pad ile doldur)
            byte[] keyBytes = "Mongo_Session_App_*%$#@!_47567243".getBytes(StandardCharsets.UTF_8);
            this.SECRET_KEY = Arrays.copyOf(keyBytes, 32); // fazlaysa keser, eksikse sıfırla doldurur
        } catch (Exception e) {
            throw new RuntimeException("Error while creating MongoPasswordEncryptor", e);
        }
    }
    
    public String encryptPassword(String password) {
        // Encode data to store into database
        log.info("Convert Application to Database data: {}", password);
        try {
            Key key = new SecretKeySpec(SECRET_KEY, ENC);
            Cipher c = Cipher.getInstance(ALGORITHM);
            c.init(Cipher.ENCRYPT_MODE, key);
            return Base64.getEncoder().encodeToString(c.doFinal(password.getBytes()));
        } catch(Exception e) {
            log.error("Failed to encode", e);
        }
        return null;
    }

    public String decryptPassword(String encryptedPassword) {
        // Decode data to use in Application
        log.info("Convert Database to Application data: {}", encryptedPassword);
        String value = null;
        try {
            Key key = new SecretKeySpec(SECRET_KEY, ENC);
            Cipher c = Cipher.getInstance(ALGORITHM);
            c.init(Cipher.DECRYPT_MODE, key);
            value = new String(c.doFinal(Base64.getDecoder().decode(encryptedPassword)));

        } catch(Exception e) {
            log.info("Failed to decode: "+ e.getMessage());
        }
        return value;
    }
}
