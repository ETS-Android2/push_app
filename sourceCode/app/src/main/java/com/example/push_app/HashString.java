package com.example.push_app;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Class used to calculate MD5 hash
 */
public class HashString {
    final static String MD5 = "MD5";

    /**
     * Generate MD5 hash.
     * @param input: string to be hashed
     * @return hashed string.
     */
    public static String hashMD5(String input){
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(input.getBytes());
            byte[] messageDigest = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                StringBuilder h = new StringBuilder(Integer.toHexString(0xFF & aMessageDigest));
                while (h.length() < 2)
                    h.insert(0, "0");
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
