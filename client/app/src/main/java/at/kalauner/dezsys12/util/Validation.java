package at.kalauner.dezsys12.util;

import java.util.regex.Pattern;

/**
 * Some util methods for validation
 *
 * @author Paul Kalauner 5BHIT
 * @version 20160219.1
 */
public final class Validation {
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    /**
     * Checks if the email address is valid
     *
     * @param email the email address which should be checked
     * @return true if valid
     */
    public static boolean isEmailValid(String email) {
        return pattern.matcher(email).matches();
    }

    /**
     * Checks if the password is valid
     *
     * @param password the password which should be checked
     * @return true if valid
     */
    public static boolean isPasswordValid(String password) {
        return password.length() > 4;
    }
}
