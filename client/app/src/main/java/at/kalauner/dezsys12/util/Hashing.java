package at.kalauner.dezsys12.util;

import java.security.MessageDigest;

/**
 * Methods for hashing
 *
 * @author Paul Kalauner 5BHIT
 * @version 20160228.1
 */
public class Hashing {
    public static String sha256hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes("UTF-8"));
            return new String(md.digest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
