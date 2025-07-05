//encryption & decryption integration for Android Chat App

package com.example.connectify.Utils;

import android.util.Base64;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESHelper {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

    private static SecretKey sharedSecretKey;
    // Set the shared AES key globally
    public static void setSharedSecretKey(SecretKey key) {
        sharedSecretKey = key;
    }

    // Get the shared AES key
    public static SecretKey getSharedSecretKey() {
        return sharedSecretKey;
    }
    // Encrypt using sharedSecretKey
    public static String encrypt(String data) throws Exception {
        if (sharedSecretKey == null)
            throw new IllegalStateException("sharedSecretKey is not initialized!");

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);

        // Generate random IV
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        cipher.init(Cipher.ENCRYPT_MODE, sharedSecretKey, ivSpec);

        byte[] encrypted = cipher.doFinal(data.getBytes());
        byte[] encryptedWithIv = new byte[iv.length + encrypted.length];

        System.arraycopy(iv, 0, encryptedWithIv, 0, iv.length);
        System.arraycopy(encrypted, 0, encryptedWithIv, iv.length, encrypted.length);

        return Base64.encodeToString(encryptedWithIv, Base64.DEFAULT);
    }

    // Decrypt using sharedSecretKey
    public static String decrypt(String encryptedData) throws Exception {
        if (sharedSecretKey == null)
            throw new IllegalStateException("sharedSecretKey is not initialized!");

        byte[] encryptedIvData = Base64.decode(encryptedData, Base64.DEFAULT);

        // Extract IV and encrypted data
        byte[] iv = new byte[16];
        System.arraycopy(encryptedIvData, 0, iv, 0, iv.length);

        byte[] encrypted = new byte[encryptedIvData.length - iv.length];
        System.arraycopy(encryptedIvData, iv.length, encrypted, 0, encrypted.length);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, sharedSecretKey, ivSpec);

        byte[] decrypted = cipher.doFinal(encrypted);
        return new String(decrypted);
    }
    public static SecretKey generateAESKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256); // AES-256
        return keyGen.generateKey();
    }
    public static String encodeKeyToBase64(SecretKey key) {
        return Base64.encodeToString(key.getEncoded(), Base64.DEFAULT);
    }

    public static SecretKey decodeKeyFromBase64(String base64Key) {
        byte[] decodedKey = Base64.decode(base64Key, Base64.DEFAULT);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }


}
