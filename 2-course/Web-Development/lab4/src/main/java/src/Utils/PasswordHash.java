package src.Utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHash {
    public String toSHA384(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-384");
            byte[] result = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : result) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}