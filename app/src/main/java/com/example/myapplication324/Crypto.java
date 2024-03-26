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
//import java.util.Base64;
import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

//public class Crypto {
//
//    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
//    private static final int GCM_NONCE_LENGTH = 12; // 96 bits
//   // private static final String key = "Sbox8fh849rewuf9wos3edfg>lfs<ffs";
//    private static final int SALT_LENGTH = 0;   ///idk next semster
//
//    // Method to generate a SecretKeySpec from a given password using PBKDF2
//    private static SecretKeySpec generateKey(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
//        String algorithm = "PBKDF2WithHmacSHA256";
//        int keyLength = 256; // in bits
//        int iterations = 10000; // adjust based on your security requirements
//// Convert the password string to a character array
//        char[] passwordChars = password.toCharArray();
//        // Create a PBEKeySpec object with the password characters, salt, iterations, and key length
//        PBEKeySpec spec = new PBEKeySpec(passwordChars, salt, iterations, keyLength);
//        // Get an instance of the SecretKeyFactory using the specified algorithm
//        SecretKeyFactory factory = SecretKeyFactory.getInstance(algorithm);
//        // Generate the secret key using the PBEKeySpec
//        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
//// Create a SecretKeySpec object from the key bytes using the AES algorithm
//        return new SecretKeySpec(keyBytes, "AES");
//    }
//
//    // Method to generate a random salt
//    private static byte[] generateSalt() {
//        byte[] salt = new byte[16];
//        SecureRandom random = new SecureRandom();
//        random.nextBytes(salt);
//        return salt;
//    }
//
//    // Method to encrypt data using AES/GCM
//    public static byte[] encryptFile(InputStream inputFile, String password) {
//        try {
//            byte[] salt = generateSalt(); // Generate a random salt
//            SecretKeySpec secretKey = generateKey(password, salt); // Generate the secret key using PBKDF2
//            Cipher cipher = Cipher.getInstance(TRANSFORMATION); // Create a cipher instance
//
//            SecureRandom random = new SecureRandom();
//            byte[] iv = new byte[GCM_NONCE_LENGTH]; // Generate a random initialization vector (IV)
//            random.nextBytes(iv);
//
//            GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);
//            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec); // Initialize the cipher for encryption
//
//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//            outputStream.write(salt); // Write the salt to the output stream
//
//            try (BufferedInputStream bufferedInputStream = new BufferedInputStream(inputFile)) {
//                byte[] buffer = new byte[4096];
//                int bytesRead;
//                while ((bytesRead = bufferedInputStream.read(buffer)) != -1) {
//                    byte[] encryptedBytes = cipher.update(buffer, 0, bytesRead); // Encrypt the input data
//                    if (encryptedBytes != null) {
//                        outputStream.write(encryptedBytes); // Write the encrypted data to the output stream
//                    }
//                }
//            }
//
//            byte[] finalBytes = cipher.doFinal(); // Finalize the encryption process
//            if (finalBytes != null) {
//                outputStream.write(finalBytes); // Write the final encrypted data to the output stream
//            }
//
//            return outputStream.toByteArray(); // Return the encrypted file as byte[]
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return null; // Return null if encryption fails
//    }
//
//    public static void decryptFile(byte[] encryptedFile, String password, OutputStream outputFile) {
//        try {
//            byte[] salt = Arrays.copyOfRange(encryptedFile, 0, SALT_LENGTH);
//            SecretKeySpec secretKey = generateKey(password, salt);
//            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
//
//            byte[] iv = Arrays.copyOfRange(encryptedFile, SALT_LENGTH, SALT_LENGTH + GCM_NONCE_LENGTH);
//            GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);
//            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);
//
//            try (CipherInputStream cipherInputStream = new CipherInputStream(new ByteArrayInputStream(encryptedFile, SALT_LENGTH + GCM_NONCE_LENGTH, encryptedFile.length - SALT_LENGTH - GCM_NONCE_LENGTH), cipher)) {
//                byte[] buffer = new byte[4096];
//                int bytesRead;
//                while ((bytesRead = cipherInputStream.read(buffer)) != -1) {
//                    outputFile.write(buffer, 0, bytesRead);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//}









import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
//import java.util.Base64;
//
//public class Crypto {
//
//    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding"; // Changed to ECB mode for simplicity
//    private static final int KEY_SIZE = 128; // Key size in bits
//    private static final String RANDOM_KEY_PREF = "random_key";
//
//    // Method to generate a random key
//    protected static String generateRandomKey() {
//        byte[] keyBytes = new byte[KEY_SIZE / 8]; // Convert bits to bytes
//        new SecureRandom().nextBytes(keyBytes);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            return Base64.getEncoder().encodeToString(keyBytes);
//        }
//        return null;
//    }
//
////    protected static String generateRandomKey() {
////        return "FEMLUJMaOhgfzB+WsictJg==";
////    }
//
//    // Method to save the random key securely on the user's device
//    protected static void saveRandomKey(Context context, String key) {
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putString(RANDOM_KEY_PREF, key);
//        editor.apply();
//    }
//
//  //   Method to retrieve the random key from the user's device
//    protected static String getRandomKey(Context context) {
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
//        return preferences.getString(RANDOM_KEY_PREF, null);
//    }
//
////    protected static String getRandomKey(Context context) {
////        // Return the specific key
////        return "FEMLUJMaOhgfzB+WsictJg==";
////    }
//
//    // Method to encrypt data using AES
//    public static byte[] encryptFile(InputStream inputFile, Context context) {
//        try {
//            String randomKey = generateRandomKey();
//            saveRandomKey(context, randomKey);
//
//            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(Base64.getDecoder().decode(randomKey), "AES"));
//            }
//
//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//
//            try (CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher)) {
//                byte[] buffer = new byte[4096];
//                int bytesRead;
//                while ((bytesRead = inputFile.read(buffer)) != -1) {
//                    cipherOutputStream.write(buffer, 0, bytesRead);
//                }
//            }
//
//            return outputStream.toByteArray();
//        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    // Method to decrypt data using AES
//    public static byte[] decryptFile(byte[] encryptedFile, Context context) {
//        try {
//            String randomKey = getRandomKey(context);
//            if (randomKey == null) {
//                // Key not found, unable to decrypt
//                Log.e("Crypto", "Random key not found");
//                return null;
//            }
//
//            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(Base64.getDecoder().decode(randomKey), "AES"));
//            }
//
//            ByteArrayInputStream inputStream = new ByteArrayInputStream(encryptedFile);
//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//
//            try (CipherInputStream cipherInputStream = new CipherInputStream(inputStream, cipher)) {
//                byte[] buffer = new byte[4096];
//                int bytesRead;
//                while ((bytesRead = cipherInputStream.read(buffer)) != -1) {
//                    outputStream.write(buffer, 0, bytesRead);
//                }
//            }
//
//            return outputStream.toByteArray();
//        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//}


public class Crypto {

   // private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding"; // Changed to ECB mode for simplicity
   private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private static final int KEY_SIZE = 128; // Key size in bits
   // private static final String SPECIFIC_KEY = "FEMLUJMaOhgfzB+WsictJg==";




    // نوف هذي اكوادك الي كنتي كاتبتها


    // Method to encrypt data using AES with the specific key
   /*public static byte[] encryptFile(InputStream inputFile, String specificKey) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(Base64.getDecoder().decode(specificKey), "AES"));
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            try (CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputFile.read(buffer)) != -1) {
                    cipherOutputStream.write(buffer, 0, bytesRead);
                }
            }

            return outputStream.toByteArray();
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Method to decrypt data using AES with the specific key
    public static byte[] decryptFile(byte[] encryptedFile, String specificKey) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(Base64.getDecoder().decode(specificKey), "AES"));
            }

            ByteArrayInputStream inputStream = new ByteArrayInputStream(encryptedFile);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            try (CipherInputStream cipherInputStream = new CipherInputStream(inputStream, cipher)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = cipherInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

            return outputStream.toByteArray();
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }


*/

























 // هذي الاكواد الجديده الي انا كاتبتها

    public static byte[] encryptFile(InputStream inputFile, Context context, String fileName) {
        try {
            // Generate a new unique key
            String uniqueKey = generateUniqueKey();

            // Store the key in SharedPreferences
            storeKeyInSharedPreferences(context, fileName, uniqueKey);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(Base64.decode(uniqueKey, Base64.DEFAULT), "AES"));
            }


            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            try (CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputFile.read(buffer)) != -1) {
                    cipherOutputStream.write(buffer, 0, bytesRead);
                }
            }

            // Return the encrypted data along with the unique key
            return outputStream.toByteArray();
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Method to decrypt data using AES with the stored key
    public static byte[] decryptFile(byte[] encryptedFile, Context context, String fileName) {
        // Retrieve the unique key from SharedPreferences using the provided filename
        String uniqueKey = getKeyFromSharedPreferences(context, fileName);
        Log.e("Decrypt", "the key is " + uniqueKey);

        if (uniqueKey == null) {
            Log.e("Crypto", "Unique key not found for file: " + fileName);
            return null;
        }

        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(Base64.decode(uniqueKey, Base64.DEFAULT), "AES"));
            }

            ByteArrayInputStream inputStream = new ByteArrayInputStream(encryptedFile);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            try (CipherInputStream cipherInputStream = new CipherInputStream(inputStream, cipher)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = cipherInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

            // Return the decrypted data
            return outputStream.toByteArray();
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    // Method to generate a new unique key for each file
    private static String generateUniqueKey() {
        // Generate a 128-bit (16 bytes) random key
        byte[] key = new byte[16];
        new SecureRandom().nextBytes(key);
        Log.e("Crypto", "Unique key is: " + key);

        // Encode the key to Base64 string for storage
        return Base64.encodeToString(key, Base64.DEFAULT);

    }

    // Method to store the key in SharedPreferences
    private static void storeKeyInSharedPreferences(Context context, String fileName, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("FileKeys", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(fileName, key);
        editor.apply();
        Log.e("Crypto", "Unique key not found for file: " + key);
    }

    // Method to retrieve the key from SharedPreferences
    public static String getKeyFromSharedPreferences(Context context, String fileName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("FileKeys", Context.MODE_PRIVATE);
        return sharedPreferences.getString(fileName, null);
    }







}
