package com.eric.onlineresourcemanagementsys.utils;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.*;

public class KeyHandler {
    // saving the secretKey to a file
    public static void saveSecretKey(SecretKey secretKey, String fileName) {
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(secretKey);
            System.out.println("Secret key saved successfully.");
        }catch(Exception e) {
            System.out.println("Error when saving the secretKey" + e.getMessage());
        }
    }

    public static SecretKey loadSecretKey(String fileName) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            System.out.println("Secret key loaded successfully from " + fileName);
            return (SecretKey) ois.readObject();
        }catch (FileNotFoundException e) {
            System.out.println("Secret key file not found: " + fileName);
            return null;
        } catch(Exception e) {
            System.out.println("Error when loading the secretKey" + e.getMessage());
            return null;
        }
    }

    // Generates a SecretKey for AES encryption
    public static SecretKey generateSecretKey() throws Exception {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256); // Specify key size
            return keyGenerator.generateKey();
        } catch (Exception e) {
            throw new RuntimeException("Error generating secret key", e);
        }
    }
}
