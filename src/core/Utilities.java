package core;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utilities {
    private static final Logger LOG = new Logger(Utilities.class);

    public static String hash(String raw) throws NoSuchAlgorithmException {
        LOG.log(Logger.Action.BEGIN, "passwordToEncrypt");

        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(raw.getBytes(StandardCharsets.UTF_8));
        byte[] digest = messageDigest.digest();

        LOG.log(Logger.Action.RETURN, "encryptedPassword");
        return String.format("%064x", new BigInteger(1, digest));
    }

}
