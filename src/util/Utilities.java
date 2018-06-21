package util;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utilities {

    public static String hash(String raw) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(raw.getBytes(StandardCharsets.UTF_8));
        byte[] digest = messageDigest.digest();
        return String.format("%064x", new BigInteger(1, digest));
    }

}
