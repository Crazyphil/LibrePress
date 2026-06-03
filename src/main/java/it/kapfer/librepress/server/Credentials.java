package it.kapfer.librepress.server;

import java.util.Arrays;
import java.util.Objects;

/**
 * Authentication credentials using username and password to access the NewspaperDirect API
 *
 * @param username the username (usually an e-mail address)
 * @param password the user's password, represented as a char array to avoid leaving traces in the heap space
 */
public record Credentials(String username, char[] password) {
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
        return "AuthenticationData{" +
                "username='" + username + '\'' +
                ", password=" + "*".repeat(password.length) +
                "}";
    }
}
