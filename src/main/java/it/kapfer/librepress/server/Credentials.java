package it.kapfer.librepress.server;

import java.util.Arrays;
import java.util.Objects;

/**
 * Authentication credentials using username and password to access the NewspaperDirect API
 */
public class Credentials {
    private final String username;
    private final char[] password;

    /**
     * Creates a new credentials object.
     *
     * @param username the username (usually an e-mail address)
     * @param password the user's password, represented as a char array to avoid leaving traces in the heap space
     */
    public Credentials(String username, char[] password) {
        this.username = username;
        this.password = password;
    }

    /**
     * {@return the username (usually an e-mail address)}
     */
    public String username() {
        return username;
    }

    /**
     * {@return the user's password, represented as a char array to avoid leaving traces in the heap space}
     */
    public char[] password() {
        return password;
    }

    /**
     * Clears the stored password from this credentials object after it has been used. This allows keeping it in memory for an even shorter time than waiting
     * for garbage collection.
     */
    public void clear() {
        Arrays.fill(password, '*');
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Credentials that = (Credentials) o;
        return Objects.equals(username, that.username) && Arrays.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, Arrays.hashCode(password));
    }

    @Override
    public String toString() {
        return "Credentials{" +
                "username='" + username + '\'' +
                ", password=" + "*".repeat(password.length) +
                "}";
    }
}
