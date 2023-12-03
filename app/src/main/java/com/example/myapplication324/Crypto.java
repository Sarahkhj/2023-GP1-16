package com.example.myapplication324;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Crypto {

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_NONCE_LENGTH = 12; // 96 bits
    private static final String key = "Sbox8fh849rewuf9wos3edfg>lfs<ffs";
    private static final int SALT_LENGTH = 0;   ///idk next semster

    // Method to generate a SecretKeySpec from a given password using PBKDF2
    private static SecretKeySpec generateKey(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String algorithm = "PBKDF2WithHmacSHA256";
        int keyLength = 256; // in bits
        int iterations = 10000; // adjust based on your security requirements

        char[] passwordChars = password.toCharArray();
        PBEKeySpec spec = new PBEKeySpec(passwordChars, salt, iterations, keyLength);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(algorithm);
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();

        return new SecretKeySpec(keyBytes, "AES");
    }

    // Method to generate a random salt
    private static byte[] generateSalt() {
        byte[] salt = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);
        return salt;
    }

    // Method to encrypt data using AES/GCM
    public static byte[] encryptFile(InputStream inputFile, String password) {
        try {
            byte[] salt = generateSalt();
            SecretKeySpec secretKey = generateKey(password, salt);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            SecureRandom random = new SecureRandom();
            byte[] iv = new byte[GCM_NONCE_LENGTH];
            random.nextBytes(iv);

            GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(salt); // Write the salt to the output stream

            try (BufferedInputStream bufferedInputStream = new BufferedInputStream(inputFile)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = bufferedInputStream.read(buffer)) != -1) {
                    byte[] encryptedBytes = cipher.update(buffer, 0, bytesRead);
                    if (encryptedBytes != null) {
                        outputStream.write(encryptedBytes);
                    }
                }
            }

            byte[] finalBytes = cipher.doFinal();
            if (finalBytes != null) {
                outputStream.write(finalBytes);
            }

            return outputStream.toByteArray(); // Return the encrypted file as byte[]
        } catch (Exception e) {
            e.printStackTrace();
        }
////
        return null; // Return null if encryption fails
    }
    public static void decryptFile(byte[] encryptedFile, String password, OutputStream outputFile) {
        try {
            byte[] salt = Arrays.copyOfRange(encryptedFile, 0, SALT_LENGTH);
            SecretKeySpec secretKey = generateKey(password, salt);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            byte[] iv = Arrays.copyOfRange(encryptedFile, SALT_LENGTH, SALT_LENGTH + GCM_NONCE_LENGTH);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

            try (CipherInputStream cipherInputStream = new CipherInputStream(new ByteArrayInputStream(encryptedFile, SALT_LENGTH + GCM_NONCE_LENGTH, encryptedFile.length - SALT_LENGTH - GCM_NONCE_LENGTH), cipher)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = cipherInputStream.read(buffer)) != -1) {
                    outputFile.write(buffer, 0, bytesRead);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}